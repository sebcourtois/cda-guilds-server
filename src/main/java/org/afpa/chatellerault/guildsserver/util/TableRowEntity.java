package org.afpa.chatellerault.guildsserver.util;

import java.util.List;

public interface TableRowEntity {
    String tableName();

    List<TableFieldSpec> tableFields();
}
