package org.afpa.chatellerault.guildsserver.util;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public interface TableMappedData {

    String tableName();

    List<TableFieldSpec> tableFields();

    default List<TableFieldSpec> primaryFields() throws NoSuchElementException {
        var pkFields = this.tableFields().stream()
                .filter(TableFieldSpec::isPrimaryKey)
                .toList();

        if (pkFields.isEmpty()) {
            throw new NoSuchElementException();
        }
        return pkFields;
    }

    default List<Object> primaryKeys() {
        return this.primaryFields().stream()
                .map(TableFieldSpec::getGetter)
                .map(Supplier::get)
                .toList();
    }

    default TableRowMap toTableRow() {
        var fieldNames = this.tableFields().stream()
                .map(TableFieldSpec::getName)
                .toList();

        var tableRow = new TableRowMap(fieldNames);
        for (var fieldSpec : this.tableFields()) {
            var fieldGetter = fieldSpec.getGetter();
            if (fieldGetter != null) {
                tableRow.set(fieldSpec.getName(), fieldGetter.get());
            }
        }
        return tableRow;
    }

    default TableRowMapper tableRowMapper() {
        return new TableRowMapper(this.tableFields());
    }

    default void loadTableRow(TableRowMap tableRow) {
        for (var fieldSpec : this.tableFields()) {
            var fieldName = fieldSpec.getName();
            var fieldSetter = fieldSpec.getSetter();
            fieldSetter.accept(tableRow.get(fieldName));
        }
    }

    class TableRowMapper implements RowMapper<TableRowMap> {
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

