package kr.mybrary.bookservice.recommend.domain;

import java.util.List;
import kr.mybrary.bookservice.mybook.domain.MyBookReadService;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedDeleteServiceRequest;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationFeedAccessDeniedException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationFeedAlreadyExistException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationFeedNotFoundException;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTargets;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendationFeedWriteService {
    
    private final RecommendationFeedRepository recommendationFeedRepository;
    private final MyBookReadService myBookReadService;
    
    public void create(RecommendationFeedCreateServiceRequest request) {
        checkExistRecommendationFeed(request.getMyBookId());

        MyBook myBook = myBookReadService.findMyBookById(request.getMyBookId());
        RecommendationTargets recommendationTargets = new RecommendationTargets(createRecommendationTargets(request));

        RecommendationFeed recommendationFeed = RecommendationFeed.of(request, myBook, recommendationTargets);
        recommendationFeedRepository.save(recommendationFeed);
    }

    public void deleteRecommendationFeed(RecommendationFeedDeleteServiceRequest request) {

        RecommendationFeed recommendationFeed = recommendationFeedRepository.findById(request.getRecommendationFeedId())
                .orElseThrow(RecommendationFeedNotFoundException::new);

        validateRecommendationFeedAccess(request, recommendationFeed);
        recommendationFeedRepository.delete(recommendationFeed);
    }

    private void validateRecommendationFeedAccess(RecommendationFeedDeleteServiceRequest request, RecommendationFeed recommendationFeed) {
        if (isOwnerSameAsRequester(recommendationFeed.getUserId(), request.getLoginId())) {
            throw new RecommendationFeedAccessDeniedException();
        }
    }

    private void checkExistRecommendationFeed(Long myBookId) {
        if (recommendationFeedRepository.existsByMyBookId(myBookId)) {
            throw new RecommendationFeedAlreadyExistException();
        }
    }

    private List<RecommendationTarget> createRecommendationTargets(RecommendationFeedCreateServiceRequest request) {
        return request.getRecommendationTargetNames()
                .stream().map(RecommendationTarget::of)
                .toList();
    }

    private boolean isOwnerSameAsRequester(String ownerId, String requesterId) {
        return !ownerId.equals(requesterId);
    }
}
