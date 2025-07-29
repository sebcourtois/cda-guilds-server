package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.model.BaseEntityData;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseRepository<D extends BaseEntityData> {

    public final JdbcClient jdbcClient;

    public BaseRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(D entityData) {
        var tableRow = entityData.toTableRow();
        var generatedFields = entityData.tableFields().stream()
                .filter(TableFieldSpec::isGenerated)
                .map(TableFieldSpec::getName)
                .toList();
        if (!generatedFields.isEmpty()) generatedFields.forEach(tableRow::removeField);

        var tableFields = tableRow.getFields();
        var valuePlaceholders = tableFields.stream().map(":%s"::formatted).toList();
        String sql = """
                INSERT INTO "%s" ("%s") VALUES (%s) RETURNING *;
                """.formatted(
                entityData.tableName(),
                String.join("\", \"", tableFields),
                String.join(", ", valuePlaceholders)
        );
        var rowData = this.jdbcClient.sql(sql)
                .paramSource(tableRow.toSqlParamSource())
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
}


