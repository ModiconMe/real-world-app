package edu.popov.domain.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName("profile")
public class ProfileDTO {

    @JsonProperty(value = "username", access = JsonProperty.Access.READ_ONLY)
    private String username;

    @JsonProperty(value = "bio", access = JsonProperty.Access.READ_ONLY)
    private String bio;

    @JsonProperty(value = "image", access = JsonProperty.Access.READ_ONLY)
    private String image;

    @JsonProperty(value = "following", access = JsonProperty.Access.READ_ONLY)
    private boolean following;

}