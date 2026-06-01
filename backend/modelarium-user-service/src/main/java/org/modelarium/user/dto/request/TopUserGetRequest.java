package org.modelarium.user.dto.request;

import jakarta.validation.constraints.Max;
import org.modelarium.user.dto.DataObject;

import java.time.Instant;
import java.util.UUID;

public record TopUserGetRequest(
        UUID eventId,
        Instant createdAt,
        int sequenceId,
        @Max(100)
        int size
) implements DataObject {
}