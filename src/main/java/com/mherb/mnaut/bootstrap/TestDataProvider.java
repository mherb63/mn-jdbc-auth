package com.mherb.mnaut.bootstrap;

import com.mherb.mnaut.domain.User;
import com.mherb.mnaut.repository.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TestDataProvider {
    private final UserRepository userRepository;

    public TestDataProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        if (userRepository.findByEmail("jill@example.com").isEmpty()) {
            User user = User.builder().email("jill@example.com").password("secret").build();
            userRepository.save(user);

            log.debug("added user: " + user);
        }
    }
}
