package com.fitness.nutritionservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.nutritionservice.model.Activity;
import com.fitness.nutritionservice.model.NutritionRecommendation;
import com.fitness.nutritionservice.repository.NutritionRecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NutritionRecommendationService {


    private final GeminiService geminiService;
    private final NutritionRecommendationRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void generateRecommendationForActivity(Activity activity) {
        try {

            String prompt = createNutritionPrompt(activity);
            log.info("Sending nutrition prompt to Gemini API for user {}...", activity.getUserId());

            String aiResponse = geminiService.getAnswer(prompt);
            log.info("Received nutrition response from Gemini API.");
            log.debug("AI Response: {}", aiResponse);
            NutritionRecommendation recommendation = processAiResponse(activity, aiResponse);

            repository.save(recommendation);
            log.info("Saved new nutrition recommendation for user {}.", activity.getUserId());

        } catch (Exception e) {
            log.error("Failed to generate nutrition recommendation for user {}: {}",
                    activity.getUserId(), e.getMessage(), e);

            NutritionRecommendation defaultRec = createDefaultRecommendation(activity);
            repository.save(defaultRec);
        }
    }

    private NutritionRecommendation processAiResponse(Activity activity, String aiResponse) throws Exception {

        JsonNode rootNode = objectMapper.readTree(aiResponse);
        String jsonContent = rootNode.path("candidates")
                .get(0).path("content").path("parts")
                .get(0).path("text").asText()
                .replaceAll("```json\\n","") // Clean up markdown
                .replaceAll("\\n```", "")
                .trim();

        log.debug("Extracted JSON content: {}", jsonContent);

        // 2. Parse the extracted JSON
        JsonNode nutritionJson = objectMapper.readTree(jsonContent);

        // 3. Extract our new nutrition-focused fields
        String mealSuggestion = nutritionJson.path("mealSuggestion").asText("Eat a balanced meal.");
        String hydrationTip = nutritionJson.path("hydrationTip").asText("Stay hydrated.");
        String generalAdvice = nutritionJson.path("generalAdvice").asText("Listen to your body.");

        // 4. Combine tips into one user-friendly recommendation
        String fullRecommendation = String.format(
                "Meal Tip: %s\n\nHydration: %s\n\nAdvice: %s",
                mealSuggestion, hydrationTip, generalAdvice
        );

        // 5. Build the new model object
        NutritionRecommendation rec = new NutritionRecommendation();
        rec.setUserId(activity.getUserId());
        rec.setSourceActivityId(activity.getId());
        rec.setRecommendationText(fullRecommendation);
        rec.setCreatedAt(LocalDateTime.now());

        return rec;
    }

    private NutritionRecommendation createDefaultRecommendation(Activity activity) {
        NutritionRecommendation rec = new NutritionRecommendation();
        rec.setUserId(activity.getUserId());
        rec.setSourceActivityId(activity.getId());
        rec.setRecommendationText(
                "Great job on your " + activity.getActivityType() + "! " +
                        "Remember to rehydrate with plenty of water and eat a balanced meal " +
                        "with protein and carbs to help your muscles recover."
        );
        rec.setCreatedAt(LocalDateTime.now());
        return rec;
    }

    private String createNutritionPrompt(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide simple, actionable nutrition advice in the following EXACT JSON format:
        {
          "mealSuggestion": "A brief post-workout meal suggestion (e.g., 'A chicken breast sandwich' or 'A protein shake with a banana').",
          "hydrationTip": "A simple hydration tip (e.g., 'Drink at least 16oz of water in the next hour.')",
          "generalAdvice": "One other general nutrition tip related to this workout."
        }
        
        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        
        Provide advice that is simple, encouraging, and focused on recovery.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned()
        );
    }

    public List<NutritionRecommendation> getRecommendationsForUser(String userId) {
        return repository.findByUserId(userId);
    }
}