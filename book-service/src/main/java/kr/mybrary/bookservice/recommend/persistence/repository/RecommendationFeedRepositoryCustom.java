package kr.mybrary.bookservice.recommend.persistence.repository;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;

public interface RecommendationFeedRepositoryCustom {

    List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll(Long RecommendationFeedId, int pageSize);

    Optional<RecommendationFeed> getRecommendationFeedWithTargets(Long recommendationFeedId);
}
