package org.modelarium.user.dto.response;

import lombok.Builder;
import org.modelarium.user.dto.UserData;

import java.util.List;
import java.util.UUID;

@Builder
public record TopUserGetResponse(
        UUID eventId,
        int sequenceId,
        List<UserData> userData
) {
}
