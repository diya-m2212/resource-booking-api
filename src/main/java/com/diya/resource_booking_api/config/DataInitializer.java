package com.diya.resource_booking_api.config;

import com.diya.resource_booking_api.model.AppUser;
import com.diya.resource_booking_api.model.Role;
import com.diya.resource_booking_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create second USER only if it does not already exist
            if (userRepository.findByUsername("user2").isEmpty()) {

                AppUser user2 = new AppUser();

                user2.setUsername("user2");
                user2.setPassword(
                        passwordEncoder.encode("User2@123")
                );
                user2.setRole(Role.USER);

                userRepository.save(user2);

                System.out.println("USER2 created successfully.");
            }
        };
    }
}