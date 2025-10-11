package org.afpa.chatellerault.guildsserver.core;

import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableConfigRowMapper<T> implements RowMapper<T> {
    private final TableConfig<T> tableConfig;
    private final T mappedObj;

    public TableConfigRowMapper(TableConfig<T> tableConfig, T mappedObj) {
        this.tableConfig = tableConfig;
        this.mappedObj = mappedObj;
    }

    @Override
    public T mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
        for (TableConfigField<T, ?> tableField : this.tableConfig.fields()) {
            Object fieldValue = res.getObject(tableField.getName(), tableField.getJavaType());
            tableField.setValue(this.mappedObj, fieldValue);
        }
        return this.mappedObj;
    }
}
