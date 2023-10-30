package kr.mybrary.bookservice.recommend.persistence.repository;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;

public interface RecommendationFeedRepositoryCustom {

    List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll(Long recommendationFeedId, int pageSize, String userId);

    Optional<RecommendationFeed> getRecommendationFeedWithTargets(Long recommendationFeedId);

    List<RecommendationFeedOfUserViewModel> getRecommendationFeedViewOfUserModel(String userId);
}
