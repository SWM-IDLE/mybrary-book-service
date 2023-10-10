package kr.mybrary.bookservice.recommend.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.List;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetDuplicateException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetSizeExceededException;

@Embeddable
public class RecommendationTargets {

    private static final int TARGET_MAX_SIZE = 5;

    @OneToMany(
            mappedBy = "recommendationFeed",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<RecommendationTarget> feedRecommendationTargets;

    protected RecommendationTargets() {
    }

    public RecommendationTargets(List<RecommendationTarget> feedRecommendationTargets) {
        validateSize(feedRecommendationTargets);
        validateDuplicate(feedRecommendationTargets);
        this.feedRecommendationTargets = feedRecommendationTargets;
    }

    public int getSize() {
        return feedRecommendationTargets.size();
    }

    private void validateSize(List<RecommendationTarget> feedRecommendationTargets) {
        if (feedRecommendationTargets.size() > TARGET_MAX_SIZE) {
            throw new RecommendationTargetSizeExceededException();
        }
    }

    private void validateDuplicate(List<RecommendationTarget> feedRecommendationTargets) {
        if (feedRecommendationTargets.size() != feedRecommendationTargets.stream()
                .map(RecommendationTarget::getTargetName)
                .distinct()
                .count()) {

            throw new RecommendationTargetDuplicateException();
        }
    }
}
