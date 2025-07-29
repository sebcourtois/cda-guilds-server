package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;


public class Caravan extends BaseEntity<CaravanData, CaravanRepository> {

    private static final Logger LOG = LogManager.getLogger(Caravan.class);
    TradingPost destination;

    public Caravan(CaravanData data, CaravanRepository repository) {
        super(data, repository);
    }

    public Optional<TradingPost> getDestination() {
        if (this.destination == null) {
            var destId = this.data.getDestinationId();
            if (destId != null) {
                LOG.info("Retrieving data of Caravan's destination");
                var found = TradingPosts.findById(destId);
                this.destination = found.orElse(null);
            }
        }
        return Optional.ofNullable(this.destination);
    }

    public UUID getId() {
        return data.getId();
    }
}
