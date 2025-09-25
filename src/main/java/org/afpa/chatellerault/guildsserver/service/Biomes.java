package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.Biome;
import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class Biomes {
    @Setter
    private static BiomeRepository repository;

    public static Biome create(BiomeData data) throws SQLException {
        repository.create(data);
        return new Biome(data, repository);
    }

    public static int delete(Biome biome) {
        return repository.delete(biome.getData());
    }
}