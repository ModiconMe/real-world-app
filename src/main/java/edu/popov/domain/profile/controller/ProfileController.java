package edu.popov.domain.profile.controller;

import edu.popov.domain.profile.dto.ProfileDTO;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ProfileDTO getProfile(
            @PathVariable("username") String username,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {

        Long id = null;
        if (!Objects.isNull(accountDetails))
            id = accountDetails.id();

        return profileService.getProfile(username, id);
    }

    @PostMapping("/{username}/follow")
    public ProfileDTO followProfile(
            @PathVariable("username") String username,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return profileService.followProfile(username, accountDetails.id());
    }

    @DeleteMapping("/{username}/follow")
    public ProfileDTO unfollowProfile(
            @PathVariable("username") String username,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return profileService.unfollowProfile(username, accountDetails.id());
    }

    @GetMapping("/followers")
    public List<ProfileDTO> getFollowers(
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return profileService.getFollowers(accountDetails.id());
    }

    @GetMapping("/followings")
    public List<ProfileDTO> getFollowings(
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return profileService.getFollowings(accountDetails.id());
    }

}