package com.fitness.nutritionservice.repository;

import com.fitness.nutritionservice.model.NutritionRecommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NutritionRecommendationRepository extends MongoRepository<NutritionRecommendation, String> {

    List<NutritionRecommendation> findByUserId(String userId);
}
