package org.afpa.chatellerault.guildsserver.util;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class TableRowData implements TableMappedObj {
    private List<TableFieldSpec> $primaryFields;
    private List<TableFieldSpec> $tableFields;

    public abstract String tableName();

    public abstract List<TableFieldSpec> tableFields();

    public final List<TableFieldSpec> getTableFields() {
        if (this.$tableFields != null) return this.$tableFields;
        var fieldsConfig = this.tableFields();
        this.$tableFields = fieldsConfig;
        return fieldsConfig;
    }

    public final List<TableFieldSpec> getPrimaryFields() throws NoSuchElementException {
        if (this.$primaryFields != null) return this.$primaryFields;
        var fields = this.getTableFields().stream()
                .filter(TableFieldSpec::isPrimaryKey)
                .toList();

        if (fields.isEmpty()) {
            throw new NoSuchElementException();
        }
        this.$primaryFields = fields;
        return fields;
    }

    public final List<Object> getPrimaryKeys() {
        return this.getPrimaryFields().stream()
                .map(TableFieldSpec::getGetter)
                .map(Supplier::get)
                .toList();
    }

    public final TableRowMap toRowMap() {
        var fieldNames = this.getTableFields().stream()
                .map(TableFieldSpec::getName)
                .toList();

        var rowMap = new TableRowMap(fieldNames);
        for (var fieldSpec : this.getTableFields()) {
            var fieldGetter = fieldSpec.getGetter();
            if (fieldGetter != null) {
                rowMap.set(fieldSpec.getName(), fieldGetter.get());
            }
        }
        return rowMap;
    }

    public final void loadFromRowMap(TableRowMap rowMap) {
        for (var fieldSpec : this.getTableFields()) {
            var fieldName = fieldSpec.getName();
            var fieldSetter = fieldSpec.getSetter();
            fieldSetter.accept(rowMap.get(fieldName));
        }
    }

    public TableRowMapper rowMapper() {
        return new TableRowMapper(this.getTableFields());
    }

    public static class TableRowMapper implements RowMapper<TableRowMap> {
        private final List<TableFieldSpec> tableFields;

        public TableRowMapper(List<TableFieldSpec> tableFields) {
            this.tableFields = tableFields;
        }

        @Override
        public TableRowMap mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
            var fieldNames = this.tableFields.stream()
                    .map(TableFieldSpec::getName)
                    .toList();

            var rowMap = new TableRowMap(fieldNames);
            for (var fieldSpec : this.tableFields) {
                var fieldName = fieldSpec.getName();
                rowMap.set(fieldName, res.getObject(fieldName, fieldSpec.getJavaType()));
            }
            return rowMap;
        }
    }
}
