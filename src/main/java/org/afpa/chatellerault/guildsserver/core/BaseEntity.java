package org.afpa.chatellerault.guildsserver.core;

import lombok.Getter;

public abstract class BaseEntity<D> {
    @Getter
    protected final D data;
    @Getter
    protected final BaseRepository<D> repository;

    public BaseEntity(D data, BaseRepository<D> repository) {
        this.data = data;
        this.repository = repository;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
