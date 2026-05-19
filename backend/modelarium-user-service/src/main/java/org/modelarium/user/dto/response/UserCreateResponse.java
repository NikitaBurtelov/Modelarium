package org.modelarium.user.dto.response;

import lombok.Builder;
import org.modelarium.user.dto.DataObject;

@Builder
public record UserCreateResponse() implements DataObject {
}
