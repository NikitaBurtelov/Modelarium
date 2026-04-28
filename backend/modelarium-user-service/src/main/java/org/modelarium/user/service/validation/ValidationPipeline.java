package org.modelarium.user.service.validation;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.DataObject;
import org.modelarium.user.service.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationPipeline<T extends DataObject> {
    private final List<ValidationRule<T>> rules;

    public void validate(T target) {
        for (ValidationRule<T> rule : rules) {
            rule.validate(target);
        }
    }
}
