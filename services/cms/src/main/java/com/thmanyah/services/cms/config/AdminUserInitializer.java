package com.thmanyah.services.cms.config;

import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${cms.admin.email}")
  private String adminEmail;

  @Value("${cms.admin.password}")
  private String adminPassword;

  @Value("${cms.admin.enabled:true}")
  private boolean adminEnabled;

  @Override
  public void run(String... args) throws Exception {
    if (!adminEnabled) {
      log.info("Admin user initialization is disabled");
      return;
    }

    if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
      log.info("Admin user already exists with email: {}, updating password...", adminEmail);
      User existingAdmin = userRepository.findByEmailIgnoreCase(adminEmail).get();
      existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
      userRepository.save(existingAdmin);
      log.info("Admin user password updated successfully");
      return;
    }

    log.info("Creating initial admin user with email: {}", adminEmail);

    User adminUser =
        User.builder()
            .email(adminEmail.toLowerCase())
            .password(passwordEncoder.encode(adminPassword)) // Hash the plain text password
            .role(Role.ADMIN)
            .isActive(true)
            .createdBy("SYSTEM")
            .updatedBy("SYSTEM")
            .build();

    userRepository.save(adminUser);
    log.info("Successfully created initial admin user: {}", adminEmail);
  }
}
