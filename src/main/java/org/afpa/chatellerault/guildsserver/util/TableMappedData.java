package org.afpa.chatellerault.guildsserver.util;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class TableMappedData {

    public abstract String tableName();
    public abstract List<TableFieldSpec> tableFields();

    public List<TableFieldSpec> primaryFields() throws NoSuchElementException {
        var pkFields = this.tableFields().stream()
                .filter(TableFieldSpec::isPrimaryKey)
                .toList();

        if (pkFields.isEmpty()) {
            throw new NoSuchElementException();
        }
        return pkFields;
    }

    public List<Object> primaryKeys() {
        return this.primaryFields().stream()
                .map(TableFieldSpec::getGetter)
                .map(Supplier::get)
                .toList();
    }

    public TableRowData toTableRow() {
        var fieldNames = this.tableFields().stream()
                .map(TableFieldSpec::getName)
                .toList();

        var tableRow = new TableRowData(fieldNames);
        for (var fieldSpec : this.tableFields()) {
            var fieldGetter = fieldSpec.getGetter();
            if (fieldGetter != null) {
                tableRow.set(fieldSpec.getName(), fieldGetter.get());
            }
        }
        return tableRow;
    }

    public TableRowMapper tableRowMapper() {
        return new TableRowMapper(this.tableFields());
    }

    public void loadTableRow(TableRowData tableRow) {
        for (var fieldSpec : this.tableFields()) {
            var fieldName = fieldSpec.getName();
            var fieldSetter = fieldSpec.getSetter();
            fieldSetter.accept(tableRow.get(fieldName));
        }
    }

    public static class TableRowMapper implements RowMapper<TableRowData> {
        private final List<TableFieldSpec> tableFields;

        public TableRowMapper(List<TableFieldSpec> tableFields) {
            this.tableFields = tableFields;
        }

        @Override
        public TableRowData mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
            var fieldNames = this.tableFields.stream()
                    .map(TableFieldSpec::getName)
                    .toList();

            var tableRow = new TableRowData(fieldNames);
            for (var fieldSpec : this.tableFields) {
                var fieldName = fieldSpec.getName();
                tableRow.set(fieldName, res.getObject(fieldName, fieldSpec.getJavaType()));
            }
            return tableRow;
        }
    }
}

