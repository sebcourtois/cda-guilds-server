package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.entity.BaseEntity;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.stream.Collectors;

public abstract class BaseRepository<E extends BaseEntity> implements RowMapper<E> {

    public final JdbcClient jdbcClient;

    public BaseRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(E entity) {
        var tableRow = entity.toTableRow();
        var generatedFields = entity.tableFields().stream()
                .filter(TableFieldSpec::isGenerated)
                .map(TableFieldSpec::getName)
                .toList();
        if (!generatedFields.isEmpty()) generatedFields.forEach(tableRow::removeField);

        var tableFields = tableRow.getFields();
        var valuePlaceholders = tableFields.stream().map(":%s"::formatted).toList();
        String sql = """
                INSERT INTO "%s" ("%s") VALUES (%s) RETURNING *;
                """.formatted(
                entity.tableName(),
                String.join("\", \"", tableFields),
                String.join(", ", valuePlaceholders)
        );
        var rowData = this.jdbcClient.sql(sql)
                .paramSource(tableRow.toSqlParamSource())
                .query(entity).single();

        entity.loadData(rowData);
    }

    public int delete(E entity) {
        String pkCondition = entity.primaryFields().stream()
                .map(field -> "\"%s\" = ?".formatted(field.getName()))
                .collect(Collectors.joining(" AND "));
        String sql = """
                DELETE FROM "%s" WHERE %s;
                """.formatted(entity.tableName(), pkCondition);

        return this.jdbcClient.sql(sql).params(entity.primaryKeys()).update();
    }
}
