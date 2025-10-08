package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.MapCase;
import org.afpa.chatellerault.guildsserver.model.MapCaseData;
import org.afpa.chatellerault.guildsserver.repository.MapCaseRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class MapCases {
    @Setter
    private static MapCaseRepository repository;

    public static MapCase create(MapCaseData data) throws SQLException {
        repository.create(data);
        return new MapCase(data, repository);
    }

    public static int delete(MapCase mapCase) {
        return repository.delete(mapCase.getData());
    }

    public static int getRowCount() {
        return repository.numberOfRowsInTable(MapCaseData.builder().build().tableName());
    }
}