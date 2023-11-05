package kr.mybrary.bookservice.recommend.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import kr.mybrary.bookservice.global.BaseEntity;
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
@SQLDelete(sql = "UPDATE recommendation_target SET deleted = true WHERE id = ?")
public class RecommendationTarget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RecommendationFeed recommendationFeed;

    private String targetName;

    private boolean deleted;

    public static RecommendationTarget of(String targetName) {
        return RecommendationTarget.builder()
                .targetName(targetName)
                .build();
    }

    public void assignRecommendationFeed(RecommendationFeed recommendationFeed) {
        this.recommendationFeed = recommendationFeed;
    }
}
