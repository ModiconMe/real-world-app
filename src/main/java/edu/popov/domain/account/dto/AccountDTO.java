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
@JsonTypeName("user")
public class AccountDTO {

    private String username;
    private String token;
    private String email;
    private String bio;
    private String image;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("user")
    public static class Registration {

        @NotBlank(message = "User name should be not blank")
        @Pattern(regexp = "[\\w\\d]{1,30}", message = "string contains alphabet or digit with length 1 to 30")
        private String username;

        @NotBlank(message = "Email should be not blank")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password should be not blank")
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
        private String email;

        @NotBlank(message = "Password should be not blank")
        private String password;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("user")
    public static class Update {

        private String username;
        private String email;
        private String password;
        private String bio;
        private String image;

    }
}


