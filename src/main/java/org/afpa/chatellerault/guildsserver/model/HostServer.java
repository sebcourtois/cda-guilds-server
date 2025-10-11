package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;
import org.afpa.chatellerault.guildsserver.core.BaseEntity;

import java.util.UUID;

public class HostServer extends BaseEntity<HostServerData> {
    public HostServer(HostServerData data, HostServerRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
