package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TradingPosts {
    @Setter
    private static TradingPostRepository repository;

    public static TradingPost create(TradingPostData data) throws SQLException {
        repository.create(data);
        return new TradingPost(data, repository);
    }

    public static int delete(TradingPost tradingPost) throws SQLException {
        return repository.delete(tradingPost.getData());
    }

    public static Optional<TradingPost> findById(UUID someId) {
        Optional<TradingPostData> tradingPostData = repository.findById(someId);
        return tradingPostData.map((TradingPostData data) -> new TradingPost(data, repository));
    }
}
