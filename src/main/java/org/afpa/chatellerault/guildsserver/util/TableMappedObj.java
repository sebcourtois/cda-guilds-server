package org.afpa.chatellerault.guildsserver.util;

import java.util.List;
import java.util.NoSuchElementException;

public interface TableMappedObj {

    String tableName();

    List<TableFieldSpec> tableFields();

    List<TableFieldSpec> getTableFields();

    List<TableFieldSpec> getPrimaryFields() throws NoSuchElementException;

    List<Object> getPrimaryKeys();

}

