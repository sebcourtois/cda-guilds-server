package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.BaseRepository;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.afpa.chatellerault.guildsserver.util.TableRowEntity;

import java.util.List;

public abstract class BaseEntity<D extends BaseEntityData, R extends BaseRepository<D>> implements TableRowEntity {
    public final D data;
    public final R repository;

    public BaseEntity(D data, R repository) {
        this.data = data;
        this.repository = repository;
    }

    public int delete() {
        return this.repository.delete(this.data);
    }

    @Override
    public String tableName() {
        return this.data.tableName();
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return this.data.tableFields();
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
