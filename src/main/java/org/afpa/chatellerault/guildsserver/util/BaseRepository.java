package org.afpa.chatellerault.guildsserver.util;

import lombok.NonNull;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public abstract class BaseRepository {

    public final JdbcClient jdbcClient;

    public BaseRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(TableMappedObj tableRow) throws SQLException {
        var sqlParamSource = new MapSqlParameterSource();
        var pgObjMapper = new PGobjectMapper();
        for (TableFieldSpec field : tableRow.getTableFields()) {
            if (field.isGenerated()) continue;
            sqlParamSource.addValue(
                    field.getName(),
                    pgObjMapper.mapValue(field.getGetter().get())
            );
        }
        var fieldNames = List.of(sqlParamSource.getParameterNames());
        var valuePlaceholders = fieldNames.stream().map(":%s"::formatted).toList();
        String sql = """
                INSERT INTO "%s" ("%s") VALUES (%s) RETURNING *;
                """.formatted(
                tableRow.tableName(),
                String.join("\", \"", fieldNames),
                String.join(", ", valuePlaceholders)
        );
        var rowMap = this.jdbcClient.sql(sql)
                .paramSource(sqlParamSource)
                .query(tableRow.rowMapper()).single();

        tableRow.loadFromRowMap(rowMap);
    }

    public int delete(TableMappedObj tableRow) {
        String pkCondition = tableRow.getPrimaryFields().stream()
                .map(field -> "\"%s\" = ?".formatted(field.getName()))
                .collect(Collectors.joining(" AND "));
        String sql = """
                DELETE FROM "%s" WHERE %s;
                """.formatted(tableRow.tableName(), pkCondition);

        return this.jdbcClient.sql(sql).params(tableRow.getPrimaryKeys()).update();
    }

    public TableRowMapper rowMapper(Supplier<TableMappedObj> instanceBuilder) {
        return new TableRowMapper(instanceBuilder);
    }

    public static class TableRowMapper implements RowMapper<TableMappedObj> {
        private final Supplier<TableMappedObj> instanceBuilder;

        public TableRowMapper(Supplier<TableMappedObj> instanceBuilder) {
            this.instanceBuilder = instanceBuilder;
        }

        @Override
        public TableMappedObj mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
            var tableRow = this.instanceBuilder.get();
            var rowMap = tableRow.rowMapper().mapRow(res, rowNum);
            tableRow.loadFromRowMap(rowMap);
            return tableRow;
        }
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


