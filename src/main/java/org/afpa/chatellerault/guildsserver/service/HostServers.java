package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.HostServer;
import org.afpa.chatellerault.guildsserver.model.HostServerData;
import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;

import java.sql.SQLException;

public class HostServers {
    @Setter
    private static HostServerRepository repository;

    public static HostServer create(HostServerData data) throws SQLException {
        repository.create(data);
        return new HostServer(data, repository);
    }

    public static int delete(HostServer caravan) {
        return repository.delete(caravan.getData());
    }
}
