package kr.mybrary.bookservice.recommend.persistence.repository;

import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationFeedTargetRepository extends JpaRepository<RecommendationTarget, Long> {

}
