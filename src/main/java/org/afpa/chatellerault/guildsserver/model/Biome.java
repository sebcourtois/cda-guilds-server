package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.afpa.chatellerault.guildsserver.core.BaseEntity;

import java.util.UUID;

public class Biome extends BaseEntity<BiomeData> {
    public Biome(BiomeData data, BiomeRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }

}
