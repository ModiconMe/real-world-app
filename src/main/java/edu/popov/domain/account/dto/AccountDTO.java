package edu.popov.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type") @JsonSubTypes({
        @JsonSubTypes.Type(value = AccountDTO.Registration.class, name = "user_registration"),
        @JsonSubTypes.Type(value = AccountDTO.Auth.class, name = "user_auth"),
        @JsonSubTypes.Type(value = AccountDTO.Update.class, name = "user_update")
})
public class AccountDTO {

    private String username;
    private String email;
    private String password;
    private String bio;
    private String image;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    @JsonTypeName("user_registration")
    public static class Registration {
        @NotBlank(message = "User name should be not blank")
        @Pattern(regexp = "[\\w\\d]{1,30}", message = "string contains alphabet or digit with length 1 to 30")
        @JsonProperty(value = "username", access = JsonProperty.Access.READ_WRITE) // json object name
        private String username;

        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        @JsonProperty(value = "email",  access = JsonProperty.Access.READ_WRITE)
        private String email;

        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password", access = JsonProperty.Access.READ_WRITE)
        private String password;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonTypeName("user_auth")
    public static class Auth {
        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        @JsonProperty(value = "email",  access = JsonProperty.Access.READ_WRITE)
        private String email;

        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password", access = JsonProperty.Access.READ_WRITE)
        private String password;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonTypeName("user_update")
    public static class Update {
        @NotBlank(message = "User name should be not blank")
        @Pattern(regexp = "[\\w\\d]{1,30}", message = "string contains alphabet or digit with length 1 to 30")
        @JsonProperty(value = "username", access = JsonProperty.Access.READ_WRITE) // json object name
        private String username;

        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        @JsonProperty(value = "email",  access = JsonProperty.Access.READ_WRITE)
        private String email;

        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password", access = JsonProperty.Access.READ_WRITE)
        private String password;

        @JsonProperty(value = "bio", access = JsonProperty.Access.READ_WRITE)
        private String bio;

        @JsonProperty("image")
        private String image;
    }
}


