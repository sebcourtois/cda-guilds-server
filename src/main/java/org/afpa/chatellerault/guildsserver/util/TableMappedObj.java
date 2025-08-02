package org.afpa.chatellerault.guildsserver.util;

import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.NoSuchElementException;

public interface TableMappedObj {

    String tableName();

    List<TableFieldSpec> tableFields();

    List<TableFieldSpec> getTableFields();

    List<TableFieldSpec> getPrimaryFields() throws NoSuchElementException;

    List<Object> getPrimaryKeys();

    TableRowMap toRowMap();

    RowMapper<TableRowMap> tableRowMapper();

    void loadFromRowMap(TableRowMap tableRow);

}

