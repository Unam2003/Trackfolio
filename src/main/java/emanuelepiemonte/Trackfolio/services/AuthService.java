package emanuelepiemonte.Trackfolio.services;

import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.NotFoundException;
import emanuelepiemonte.Trackfolio.exceptions.UnauthorizedException;
import emanuelepiemonte.Trackfolio.payload.LoginDTO;
import emanuelepiemonte.Trackfolio.security.TokenTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public final UserService userService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bcrypt;

    public AuthService(UserService userService, TokenTools tokenTools, PasswordEncoder bcrypt) {
        this.userService = userService;
        this.tokenTools = tokenTools;
        this.bcrypt = bcrypt;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        try {
            User found = this.userService.findByEmail(body.email());
            if (this.bcrypt.matches(body.password(), found.getPassword())) {
                return this.tokenTools.generateToken(found);
            } else {
                throw new UnauthorizedException("Credenziali errate!");
            }
        } catch (NotFoundException ex) {
            throw new UnauthorizedException("Credenziali errate!");
        }
    }
}
