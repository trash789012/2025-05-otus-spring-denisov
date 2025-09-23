package ru.otus.hw.healthIndicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    public static final String ROOT_USER = "admin";

    private final UserRepository userRepository;

    @Override
    public Health health() {

        try {
            Optional<User> user = userRepository.findByUsername(ROOT_USER);
            if (user.isEmpty()) {
                throw new EntityNotFoundException("User not found");
            }
            return Health.up()
                    .withDetail("username", user.get().getUsername())
                    .withDetail("status", "exists")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("username", ROOT_USER)
                    .withDetail("status", "not exists")
                    .build();
        }
    }
}
