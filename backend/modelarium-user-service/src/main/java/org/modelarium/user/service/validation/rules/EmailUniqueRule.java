package org.modelarium.user.service.validation.rules;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.exceptions.EmailAlreadyExistsException;
import org.modelarium.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUniqueRule implements ValidationRule<UserCreateRequest> {
    private final UserRepository userRepository;

    @Override
    public void validate(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
    }
}
