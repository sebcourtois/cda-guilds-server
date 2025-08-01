package org.afpa.chatellerault.guildsserver.util;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableRowMap implements Serializable {
    @lombok.Getter
    private final ArrayList<String> fields;
    private final ArrayList<Optional<Object>> values;

    public TableRowMap(List<String> fields) {
        this.fields = new ArrayList<>(fields);
        this.values = fields.stream()
                .map(field -> Optional.empty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Object get(String fieldName) {
        int fieldIndex = this.indexForField(fieldName);
        return this.values.get(fieldIndex).orElse(null);
    }

    public TableRowMap set(String fieldName, Object value) {
        int fieldIndex = this.indexForField(fieldName);
        var optValue = (value == null) ? Optional.empty() : Optional.of(value);
        this.values.set(fieldIndex, optValue);
        return this;
    }

    public List<Object> getValues() {
        return this.values.stream().map(val -> val.orElse(null)).toList();
    }

    public TableRowMap removeField(String fieldName) {
        int fieldIndex = this.indexForField(fieldName);
        this.values.remove(fieldIndex);
        this.fields.remove(fieldName);
        return this;
    }

    private int indexForField(String fieldName) throws NoSuchElementException {
        int fieldIndex = this.fields.indexOf(fieldName);
        if (fieldIndex < 0) {
            throw new NoSuchElementException("No such field: '%s'".formatted(fieldName));
        }
        return fieldIndex;
    }

    public Map<String, Object> toMap() {
        return IntStream.range(0, this.fields.size()).boxed()
                .collect(Collectors.toMap(
                        this.fields::get,
                        this.values::get
                ));
    }

    @Override
    public String toString() {
        return "%s%s".formatted(
                this.getClass().getName(),
                this.toMap().toString()
        );
    }
}
