package org.afpa.chatellerault.guildsserver.core;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class BaseRepository<T> {

    public final JdbcClient jdbcClient;
    private final TableConfig<T> tableConfig;
    private final Supplier<T> entitySupplier;

    public BaseRepository(
            JdbcClient jdbcClient,
            TableConfig<T> tableConfig,
            Supplier<T> entitySupplier
    ) {
        this.jdbcClient = jdbcClient;
        this.tableConfig = tableConfig;
        this.entitySupplier = entitySupplier;
    }

    public void create(T tableRow) throws SQLException {
        var sqlParamSource = new MapSqlParameterSource();
        var pgObjMapper = new PGobjectMapper();
        for (TableConfigField<T, ?> field : this.tableConfig.fields()) {
            if (field.isGenerated()) continue;
            sqlParamSource.addValue(
                    field.getName(),
                    pgObjMapper.mapValue(field.getValue(tableRow))
            );
        }
        List<String> fieldNames = List.of(sqlParamSource.getParameterNames());
        List<String> valuePlaceholders = fieldNames.stream().map(":%s"::formatted).toList();
        String sql = """
                INSERT INTO "%s" ("%s") VALUES (%s) RETURNING *;
                """.formatted(
                this.tableConfig.name(),
                String.join("\", \"", fieldNames),
                String.join(", ", valuePlaceholders)
        );
        this.jdbcClient.sql(sql)
                .paramSource(sqlParamSource)
                .query(this.tableConfig.rowMapper(() -> tableRow))
                .single();
    }

    public int delete(T tableRow) throws SQLException {
        var sqlParamSource = new MapSqlParameterSource();
        var pgObjMapper = new PGobjectMapper();
        for (TableConfigField<T, ?> field : this.tableConfig.getPrimaryFields()) {
            sqlParamSource.addValue(
                    field.getName(),
                    pgObjMapper.mapValue(field.getValue(tableRow))
            );
        }
        String pkCondition = Arrays.stream(sqlParamSource.getParameterNames())
                .map(fieldName -> "\"%s\" = :%s".formatted(fieldName, fieldName))
                .collect(Collectors.joining(" AND "));

        String sql = """
                DELETE FROM "%s" WHERE %s;
                """.formatted(this.tableConfig.name(), pkCondition);

        return this.jdbcClient.sql(sql).paramSource(sqlParamSource).update();
    }

    public Stream<T> findAll() {
        String statement = "SELECT * FROM \"%s\"".formatted(this.tableConfig.name());

        TableConfigRowMapper<T> rowMapper = this.rowMapper();
        return this.jdbcClient.sql(statement)
                .query(rowMapper)
                .stream();
    }

    public int getRowCount() {
        String sql = "SELECT COUNT(0) FROM \"%s\";".formatted(this.tableConfig.name());
        return this.jdbcClient.sql(sql).query(Integer.class).single();
    }

    public TableConfig<T> tableConfig() {
        return this.tableConfig;
    }

    public TableConfigRowMapper<T> rowMapper() {
        return this.tableConfig.rowMapper(this.entitySupplier);
    }

    public static class PGobjectMapper {
        public Object mapValue(Object value) throws SQLException {
            if (value instanceof InetAddress) {
                PGobject inet = new PGobject();
                inet.setType("inet");
                inet.setValue(((InetAddress) value).getHostAddress());
                return inet;
            }
            return value;
        }
    }
}


