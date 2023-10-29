package kr.mybrary.bookservice.recommend.persistence.repository;

import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationFeedRepository extends JpaRepository<RecommendationFeed, Long>, RecommendationFeedRepositoryCustom {

    boolean existsByMyBookId(Long myBookId);
}
