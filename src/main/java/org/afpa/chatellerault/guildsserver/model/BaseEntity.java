package org.afpa.chatellerault.guildsserver.model;

import lombok.Getter;
import org.afpa.chatellerault.guildsserver.repository.BaseRepository;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.afpa.chatellerault.guildsserver.util.TableRowEntity;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public abstract class BaseEntity<D extends BaseEntityData, R extends BaseRepository<D>> implements TableRowEntity {
    @Getter
    protected final D data;
    @Getter
    protected final R repository;

    public BaseEntity(D data, R repository) {
        this.data = data;
        this.repository = repository;
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
