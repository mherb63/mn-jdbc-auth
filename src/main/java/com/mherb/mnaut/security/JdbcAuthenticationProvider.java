package com.mherb.mnaut.security;

import com.mherb.mnaut.domain.User;
import com.mherb.mnaut.repository.UserRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
@Slf4j
public class JdbcAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    public JdbcAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable final HttpRequest<?> httpRequest,
                                                          final AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {
            String identity = authenticationRequest.getIdentity().toString();
            log.debug("User tries to login...user: " + identity);

            Optional<User> user = userRepository.findByEmail(identity);
            if (user.isPresent()) {
                log.debug("found user: " + user);
                String secret = authenticationRequest.getSecret().toString();
                if (user.get().getPassword().equals(secret)) {
                    log.debug("User logged in");

                    final Map<String, Object> attributes = new HashMap<>();
                    attributes.put("hair_color", "brown");
                    attributes.put("language", "en");
                    var userDetails = AuthenticationResponse.success(
                            identity,
                            Collections.singletonList("ROLE_USER"),
                            attributes
                    );

                    emitter.onNext(userDetails);
                    emitter.onComplete();
                    return;
                }
                else {
                    log.error("Wrong password supplied for user: " + user);
                }
            }
            else {
                log.error("no user found with email: " + identity);
            }

            emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong user or password")));
        }, BackpressureStrategy.ERROR);
    }
}
