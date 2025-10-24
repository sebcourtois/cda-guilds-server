package org.afpa.chatellerault.guildsserver.core;

import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TableConfigRowMapper<T> implements RowMapper<T> {
    private final TableConfig<T> tableConfig;
    private final Supplier<T> objectSupplier;

    public TableConfigRowMapper(TableConfig<T> tableConfig, Supplier<T> objectSupplier) {
        this.tableConfig = tableConfig;
        this.objectSupplier = objectSupplier;
    }

    @Override
    public T mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
        var mappedObj = this.objectSupplier.get();
        for (TableConfigField<T, ?> tableField : this.tableConfig.fields()) {
            Object fieldValue = res.getObject(tableField.getName(), tableField.getJavaType());
            tableField.setValue(mappedObj, fieldValue);
        }
        return mappedObj;
    }
}
