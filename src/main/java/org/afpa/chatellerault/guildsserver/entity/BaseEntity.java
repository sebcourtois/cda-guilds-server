package org.afpa.chatellerault.guildsserver.entity;

import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.afpa.chatellerault.guildsserver.util.TableRowData;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class BaseEntity {
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
}
