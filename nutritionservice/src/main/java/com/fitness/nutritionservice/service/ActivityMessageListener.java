package com.fitness.nutritionservice.service;

import com.fitness.nutritionservice.model.Activity; // You'll need to copy this model class from your activity-service
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final NutritionRecommendationService recommendationService;

    @KafkaListener(topics = "activity-events", groupId = "nutrition-processor-group")
    public void handleActivityEvent(Activity activity) {
        log.info("Received activity event: {} for user {}", activity.getActivityType(), activity.getUserId());

        // This is where the magic happens!
        // It tells the recommendation service to generate new advice.
        recommendationService.generateRecommendationForActivity(activity);
    }
}
