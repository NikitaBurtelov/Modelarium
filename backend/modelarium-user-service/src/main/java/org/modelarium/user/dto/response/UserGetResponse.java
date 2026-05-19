package org.modelarium.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelarium.user.dto.UserData;

import java.util.List;

@Builder
@Getter
@Setter
public class UserGetResponse {
    private List<UserData> userData;
}
