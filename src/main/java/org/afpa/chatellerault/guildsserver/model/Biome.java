package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.afpa.chatellerault.guildsserver.util.PersistedEntity;

import java.util.UUID;

public class Biome extends PersistedEntity<BiomeData, BiomeRepository> {
    public Biome(BiomeData data, BiomeRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }

}
