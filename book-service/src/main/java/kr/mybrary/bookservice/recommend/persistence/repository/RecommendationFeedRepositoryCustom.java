package kr.mybrary.bookservice.recommend.persistence.repository;

import java.util.List;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;

public interface RecommendationFeedRepositoryCustom {

    List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll(Long RecommendationFeedId, int pageSize);

}
