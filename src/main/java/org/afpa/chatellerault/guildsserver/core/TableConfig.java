package org.afpa.chatellerault.guildsserver.core;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class TableConfig<T> {
    private final String name;
    private final List<TableConfigField<T, ?>> fields;

    public TableConfig(String name, List<TableConfigField<T, ?>> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String name() {
        return this.name;
    }

    public List<TableConfigField<T, ?>> fields() {
        return this.fields;
    }

    public List<TableConfigField<T, ?>> getPrimaryFields() throws NoSuchElementException {
        var primaryFields = this.fields.stream()
                .filter(TableConfigField::isPrimaryKey)
                .toList();

        if (primaryFields.isEmpty()) {
            throw new NoSuchElementException();
        }
        return primaryFields;
    }

    public TableConfigRowMapper<T> rowMapper(Supplier<T> tableMappedObj) {
        return new TableConfigRowMapper<>(this, tableMappedObj);
    }
}
