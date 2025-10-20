package com.fitness.nutritionservice.controller;

import com.fitness.nutritionservice.model.NutritionRecommendation;
import com.fitness.nutritionservice.service.NutritionRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionRecommendationService recommendationService;

    @GetMapping("/recommendations/{userId}")
    public List<NutritionRecommendation> getRecommendations(@PathVariable String userId) {
        return recommendationService.getRecommendationsForUser(userId);
    }
}
