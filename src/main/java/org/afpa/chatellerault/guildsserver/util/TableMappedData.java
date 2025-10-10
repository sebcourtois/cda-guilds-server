package org.afpa.chatellerault.guildsserver.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class TableMappedData implements TableMappedObj {
    private List<TableFieldSpec> $primaryFields;
    private List<TableFieldSpec> $tableFields;

    public abstract String tableName();

    public abstract List<TableFieldSpec> tableFields();

    public final List<TableFieldSpec> getTableFields() {
        if (this.$tableFields != null) return this.$tableFields;
        List<TableFieldSpec> fieldsConfig = this.tableFields();
        this.$tableFields = fieldsConfig;
        return fieldsConfig;
    }

    public final List<TableFieldSpec> getPrimaryFields() throws NoSuchElementException {
        if (this.$primaryFields != null) return this.$primaryFields;
        List<TableFieldSpec> fields = this.getTableFields().stream()
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
}
