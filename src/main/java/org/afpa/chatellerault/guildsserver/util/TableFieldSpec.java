package org.afpa.chatellerault.guildsserver.util;

import lombok.Builder;
import lombok.Data;

import java.util.function.Supplier;

@Data
@Builder(builderMethodName = "")
public class TableFieldSpec {
    String name;
    @Builder.Default
    boolean isPrimaryKey = false;
    @Builder.Default
    boolean isGenerated = false;
    Supplier<Object> getter;

    static public TableFieldSpecBuilder builder(String name){
        return new TableFieldSpecBuilder().name(name);
    }
}
