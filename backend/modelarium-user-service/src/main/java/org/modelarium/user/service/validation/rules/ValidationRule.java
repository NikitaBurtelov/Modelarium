package org.modelarium.user.service.validation.rules;

import org.modelarium.user.dto.DataObject;

public interface ValidationRule<T extends DataObject> {
    void validate(T target);
}
