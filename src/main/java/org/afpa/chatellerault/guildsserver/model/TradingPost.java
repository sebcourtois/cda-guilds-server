package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;

import java.util.UUID;

public class TradingPost extends BaseEntity<TradingPostData, TradingPostRepository> {

    public TradingPost(TradingPostData data, TradingPostRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }

    public void setId(UUID id) {
        this.data.setId(id);
    }

    public long getPosX() {
        return this.data.getPosX();
    }

    public void setPosX(long posX) {
        this.data.setPosX(posX);
    }

    public String getName() {
        return this.data.getName();
    }

    public void setName(String name) {
        this.data.setName(name);
    }

    public long getPosY() {
        return this.data.getPosY();
    }

    public void setPosY(long posY) {
        this.data.setPosY(posY);
    }

    public int getPopulation() {
        return this.data.getPopulation();
    }

    public void setPopulation(int population) {
        this.data.setPopulation(population);
    }

    public UUID getHostId() {
        return this.data.getHostId();
    }

    public void setHostId(UUID hostId) {
        this.data.setHostId(hostId);
    }
}
