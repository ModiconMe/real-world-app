package edu.popov.domain.account.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
//@JsonSubTypes(
//        {
//                @JsonSubTypes.Type(value = AccountDTO.Registration.class, name = "user_registration"),
//                @JsonSubTypes.Type(value = AccountDTO.Auth.class, name = "user_auth"),
//                @JsonSubTypes.Type(value = AccountDTO.Update.class, name = "user_update")
//        }
//)
@JsonTypeName("user")
//@JsonRootName("user")
public class AccountDTO {

    @JsonProperty(value = "username", access = JsonProperty.Access.READ_WRITE)
    private String username;

    @JsonProperty(value = "token", access = JsonProperty.Access.READ_WRITE)
    private String token;

    @JsonProperty(value = "email", access = JsonProperty.Access.READ_WRITE)
    private String email;

    @JsonProperty(value = "bio", access = JsonProperty.Access.READ_WRITE)
    private String bio;

    @JsonProperty(value = "image", access = JsonProperty.Access.READ_WRITE)
    private String image;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("user")
//    @JsonRootName("user")
    public static class Registration {
        @NotBlank(message = "User name should be not blank")
        @Pattern(regexp = "[\\w\\d]{1,30}", message = "string contains alphabet or digit with length 1 to 30")
        @JsonProperty(value = "username", access = JsonProperty.Access.READ_WRITE) // json object name
        private String username;

        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        @JsonProperty(value = "email", access = JsonProperty.Access.READ_WRITE)
        private String email;

        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password", access = JsonProperty.Access.READ_WRITE)
        private String password;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("user")
    public static class Auth {
        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        @JsonProperty(value = "email", access = JsonProperty.Access.READ_WRITE)
        private String email;

        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password", access = JsonProperty.Access.READ_WRITE)
        private String password;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("user")
    public static class Update {
//        @NotBlank(message = "User name should be not blank")
//        @Pattern(regexp = "[\\w\\d]{1,30}", message = "string contains alphabet or digit with length 1 to 30")
        @JsonProperty(value = "username") // json object name
        private String username;

//        @NotBlank(message = "Email should be not blank")
//        @Email(message = "Email should be valid")
        @JsonProperty(value = "email")
        private String email;

//        @NotBlank(message = "Password should be not blank")
        @JsonProperty(value = "password")
        private String password;

        @JsonProperty(value = "bio")
        private String bio;

        @JsonProperty("image")
        private String image;
    }
}


