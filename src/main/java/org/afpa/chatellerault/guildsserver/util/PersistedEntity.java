package org.afpa.chatellerault.guildsserver.util;

import lombok.Getter;

public abstract class PersistedEntity<D extends TableMappedObj, R extends BaseRepository> {
    @Getter
    protected final D data;
    @Getter
    protected final R repository;

    public PersistedEntity(D data, R repository) {
        this.data = data;
        this.repository = repository;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
