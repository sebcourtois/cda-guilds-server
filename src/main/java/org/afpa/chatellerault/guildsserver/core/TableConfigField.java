package org.afpa.chatellerault.guildsserver.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Function;


@Data
@Builder(builderMethodName = "")
public class TableConfigField<T, V> {
    String name;
    Class<V> javaType;
    Function<T, V> getter;
    BiConsumer<T, V> setter;

    @Builder.Default
    boolean isPrimaryKey = false;
    @Builder.Default
    boolean isGenerated = false;

    static public <T, V> TableConfigFieldBuilder<T, V> builder(
            @NonNull String name,
            @NonNull Class<V> javaType,
            @NonNull Function<T, V> getter,
            @NonNull BiConsumer<T, V> setter
    ) {
        return new TableConfigFieldBuilder<T, V>().name(name)
                .javaType(javaType)
                .getter(getter)
                .setter(setter);
    }

    public V getValue(T rowData) {
        return this.getter.apply(rowData);
    }

    public void setValue(T rowData, Object value) {
        this.setter.accept(rowData, this.javaType.cast(value));
    }
}
