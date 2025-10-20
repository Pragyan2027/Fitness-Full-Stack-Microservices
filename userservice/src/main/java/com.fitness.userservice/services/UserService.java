package com.fitness.userservice.services;

import com.fitness.userservice.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional // Add this to ensure the update or save happens in one transaction
    public UserResponse register(RegisterRequest request) {

        // 1. Find the user by email (since it's your unique field)
        User user = userRepository.findByEmail(request.getEmail());

        // 2. If the user does NOT exist, create a new one
        if (user == null) {
            user = new User();
            user.setEmail(request.getEmail());
        }

        // 3. Update the user object with all details from the request.
        //    This works for both new and existing users.
        user.setPassword(request.getPassword()); // You may want to hash this
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeycloakId(request.getKeycloakId()); // <-- THE MOST IMPORTANT FIX

        // 4. Save the user (this will be an UPDATE or an INSERT)
        User savedUser = userRepository.save(user);

        // 5. Build and return the response
        return mapUserToResponse(savedUser);
    }

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToResponse(user);
    }

    public Boolean existsByUserId(String userId) {
        // This is fine, but your gateway filter should use it correctly
        return userRepository.existsByKeycloakId(userId);
    }

    // --- Helper Method ---
    // Added this helper method to avoid repeating code
    private UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}