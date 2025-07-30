package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.model.BaseEntityData;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
public abstract class BaseRepository<D extends BaseEntityData> {

    public final JdbcClient jdbcClient;

    public BaseRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(D entityData) throws SQLException {
        var sqlParamSource = new MapSqlParameterSource();
        var pgObjMapper = new PGobjectMapper();
        for (TableFieldSpec field : entityData.tableFields()) {
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
                entityData.tableName(),
                String.join("\", \"", fieldNames),
                String.join(", ", valuePlaceholders)
        );
        var rowData = this.jdbcClient.sql(sql)
                .paramSource(sqlParamSource)
                .query(entityData.tableRowMapper()).single();

        entityData.loadTableRow(rowData);
    }

    public int delete(D entityData) {
        String pkCondition = entityData.primaryFields().stream()
                .map(field -> "\"%s\" = ?".formatted(field.getName()))
                .collect(Collectors.joining(" AND "));
        String sql = """
                DELETE FROM "%s" WHERE %s;
                """.formatted(entityData.tableName(), pkCondition);

        return this.jdbcClient.sql(sql).params(entityData.primaryKeys()).update();
    }

    public EntityRowMapper<D> entityRowMapper(Supplier<D> supplier) {
        return new EntityRowMapper<>(supplier);
    }

    public static class EntityRowMapper<E extends BaseEntityData> implements RowMapper<E> {
        private final Supplier<E> supplier;

        public EntityRowMapper(Supplier<E> supplier) {
            this.supplier = supplier;
        }

        @Override
        public E mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
            var entity = this.supplier.get();
            var rowData = entity.tableRowMapper().mapRow(res, rowNum);
            entity.loadTableRow(rowData);
            return entity;
        }
    }

    public static class PGobjectMapper {
        public Object mapValue(Object value) throws SQLException {
            if (value instanceof InetAddress) {
                System.out.println(((InetAddress) value).getHostName());
                PGobject inet = new PGobject();
                inet.setType("inet");
                inet.setValue(((InetAddress) value).getHostName());
                return inet;
            }
            return value;
        }
    }

}


