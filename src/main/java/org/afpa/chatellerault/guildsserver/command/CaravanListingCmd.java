package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;
import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.service.Caravans;

import java.util.Collection;

public class CaravanListingCmd extends RemoteCommand<Collection<Caravan>> {
    @Override
    public void loadArguments(JsonNode params) {
    }

    @Override
    public Collection<Caravan> execute() {
        return Caravans.findAll().toList();
    }
}
