package org.afpa.chatellerault.guildsserver.util;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;


@Data
@Builder(builderMethodName = "")
public class TableFieldSpec {
    String name;
    Class<?> javaType;
    Supplier<Object> getter;
    Consumer<Object> setter;

    @Builder.Default
    boolean isPrimaryKey = false;
    @Builder.Default
    boolean isGenerated = false;

    static public TableFieldSpecBuilder builder(
            @NonNull String name,
            @NonNull Class<?> javaType,
            @NonNull Supplier<Object> getter,
            @NonNull Consumer<Object> setter
    ) {
        return new TableFieldSpecBuilder().name(name)
                .javaType(javaType)
                .getter(getter)
                .setter(setter);
    }
}
