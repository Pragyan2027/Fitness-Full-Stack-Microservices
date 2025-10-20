package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    @KafkaListener(topics = "activity-events", groupId = "activity-processor-group")
    public void processActivity(Activity activity) {
        try {
            // The 'activity' object is now ready to use directly!
            log.info("Received activity for user: {}", activity.getUserId());

            Recommendation recommendation = activityAIService.generateRecommendation(activity);
            recommendationRepository.save(recommendation);
            log.info("Successfully processed and saved recommendation for activity: {}", activity.getId());

        } catch (Exception e) {
            log.error("Failed to process activity with ID: {}", activity.getId(), e);
        }
    }
}