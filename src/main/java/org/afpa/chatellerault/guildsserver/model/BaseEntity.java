package org.afpa.chatellerault.guildsserver.model;

import lombok.Getter;
import org.afpa.chatellerault.guildsserver.repository.BaseRepository;
import org.afpa.chatellerault.guildsserver.util.TableMappedObj;

public abstract class BaseEntity<D extends TableMappedObj, R extends BaseRepository<D>> {
    @Getter
    protected final D data;
    @Getter
    protected final R repository;

    public BaseEntity(D data, R repository) {
        this.data = data;
        this.repository = repository;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
