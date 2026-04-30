package org.modelarium.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class UserGetResponse {
    private List<UserData> userData;

    public UserGetResponse() {}
}
