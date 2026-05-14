package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.payload.UserDTO;
import emanuelepiemonte.Trackfolio.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/me")
    public User getOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return currentAuthenticatedUser;
    }

    @PutMapping("/me")
    public User updateOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody UserDTO body) {
        return this.userService.findByIdAndUpdate(currentAuthenticatedUser.getUserId(), body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.userService.findByIdAndDelete(currentAuthenticatedUser.getUserId());
    }

    @PatchMapping("/me/avatar")
    public void uploadAvatar(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestParam("avatar") MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        System.out.println(file.getContentType());

        this.userService.avatarUpload(file, currentAuthenticatedUser.getUserId());
    }


//   --------------> ADMIN <----------

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User getById(@PathVariable UUID userId) {
        return this.userService.findById(userId);
    }


    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User getByIdAndUpdate(@PathVariable UUID userId, @RequestBody UserDTO body) {
        return this.userService.findByIdAndUpdate(userId, body);
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void getByIdAndDelete(@PathVariable UUID userId) {
        this.userService.findByIdAndDelete(userId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "userId") String sortBy) {
        return this.userService.findAll(page, size, sortBy);
    }

}
