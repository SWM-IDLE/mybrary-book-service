package kr.mybrary.bookservice.recommend.persistence.repository;

import java.util.Optional;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecommendationFeedRepository extends JpaRepository<RecommendationFeed, Long>, RecommendationFeedRepositoryCustom {

    boolean existsByMyBookId(Long myBookId);

    @Query(
            "select rf from RecommendationFeed rf " +
                    "join fetch rf.myBook mb " +
                    "join fetch mb.book b " +
                    "where rf.id = :id"
    )
    Optional<RecommendationFeed> findByIdWithMyBookAndBook(Long id);
}
