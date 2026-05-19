package org.modelarium.user.dto.request;

import org.modelarium.user.dto.DataObject;

import java.time.Instant;
import java.util.UUID;

public record TopUserGetRequest(
        UUID eventId,
        Instant createdAt,
        int sequenceId,
        int size
) implements DataObject {
}