package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.ValidationException;
import emanuelepiemonte.Trackfolio.payload.LoginDTO;
import emanuelepiemonte.Trackfolio.payload.LoginRespDTO;
import emanuelepiemonte.Trackfolio.payload.NewUserResp;
import emanuelepiemonte.Trackfolio.payload.UserDTO;
import emanuelepiemonte.Trackfolio.services.AuthService;
import emanuelepiemonte.Trackfolio.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody LoginDTO body) {
        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResp createUser(@RequestBody @Validated UserDTO body, BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            validationResult.getFieldErrors().forEach(fieldError -> System.out.println(fieldError.getDefaultMessage()));

            List<String> errors = validationResult.getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList();

            throw new ValidationException(errors);
        }
        User newUser = this.userService.saveUser(body);
        return new NewUserResp(newUser.getUserId());
    }

}
