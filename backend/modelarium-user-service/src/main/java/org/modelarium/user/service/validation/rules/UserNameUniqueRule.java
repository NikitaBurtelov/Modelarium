package org.modelarium.user.service.validation.rules;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.exceptions.UserNameAlreadyExistsException;
import org.modelarium.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNameUniqueRule implements ValidationRule<UserCreateRequest> {
    private final UserRepository userRepository;
    @Override
    public void validate(UserCreateRequest request) {
        var userName = request.userName();

        if (userRepository.existsByUserName(userName)) {
            throw new UserNameAlreadyExistsException(userName);
        }
    }
}
