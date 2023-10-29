package kr.mybrary.bookservice.recommend.persistence;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.util.List;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE recommendation_feed SET deleted = true WHERE id = ?")
public class RecommendationFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private MyBook myBook;

    @Embedded
    private RecommendationTargets recommendationTargets;

    private String userId;
    private String content;
    private boolean deleted;

    public static RecommendationFeed of(RecommendationFeedCreateServiceRequest request, MyBook myBook, RecommendationTargets recommendationTargets) {
        return RecommendationFeed.builder()
                .myBook(myBook)
                .userId(request.getUserId())
                .content(request.getContent())
                .recommendationTargets(recommendationTargets)
                .build();
    }

    public void update(RecommendationFeedUpdateServiceRequest request) {
        this.content = request.getContent();
        this.recommendationTargets.getFeedRecommendationTargets().clear();
        this.recommendationTargets.getFeedRecommendationTargets().addAll(
                request.getRecommendationTargetNames().stream()
                        .map(targetName -> RecommendationTarget.builder()
                                .recommendationFeed(this)
                                .targetName(targetName)
                                .build())
                        .toList());
    }

    public void addRecommendationFeedTarget(List<RecommendationTarget> recommendationTargets) {
        this.recommendationTargets = new RecommendationTargets(recommendationTargets);
        recommendationTargets.forEach(recommendationTarget -> recommendationTarget.assignRecommendationFeed(this));
    }
}
