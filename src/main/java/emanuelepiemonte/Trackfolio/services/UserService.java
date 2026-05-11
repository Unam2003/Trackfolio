package emanuelepiemonte.Trackfolio.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import emanuelepiemonte.Trackfolio.entities.Role;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.BadRequestException;
import emanuelepiemonte.Trackfolio.exceptions.NotFoundException;
import emanuelepiemonte.Trackfolio.payload.UserDTO;
import emanuelepiemonte.Trackfolio.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    public final UserRepository userRepository;
    private final PasswordEncoder bcrypt;
    private final Cloudinary cloudinaryUploader;


    public UserService(UserRepository userRepository, PasswordEncoder bcrypt, Cloudinary cloudinaryUploader) {
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
        this.cloudinaryUploader = cloudinaryUploader;
    }

    public User saveUser(UserDTO body) {
        if (this.userRepository.existsByEmail(body.email()))
            throw new BadRequestException("L'indirizzo email " + body.email() + " è già in uso!");

        User newUser = new User(body.name(), body.surname(), body.email(), this.bcrypt.encode(body.password()), Role.USER);
        User saveUser = this.userRepository.save(newUser);

        log.info("L'utente con id " + saveUser.getUserId() + " è stato creato correttamente!");

        return saveUser;
    }

    public User findById(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }

    public User findByIdAndUpdate(UUID userId, UserDTO body) {
        User found = this.findById(userId);

        if (!found.getEmail().equals(body.email())) {
            if (this.userRepository.existsByEmail(body.email()))
                throw new BadRequestException("L'indirizzo email " + body.email() + " è già in uso!");
        }
        found.setName(body.name());
        found.setSurname(body.surname());
        found.setEmail(body.email());
        found.setPassword(this.bcrypt.encode(body.password()));

        User updatedUser = this.userRepository.save(found);
        log.info("L'utente con id " + updatedUser.getUserId() + " è stato modificato correttamente!");
        return updatedUser;
    }

    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.userRepository.delete(found);
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("L'utente con email " + email + " non è stato trovato!"));
    }

    public void avatarUpload(MultipartFile file, UUID userId) {
        try {
            User found = this.findById(userId);
            Map result = cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) result.get("secure_url");
            System.out.println(url);
            found.setAvatarURL(url);
            this.userRepository.save(found);

            log.info("Avatar aggiornato per l'utente: " + userId);
        } catch (IOException e) {
            log.error("Errore upload Cloudinary", e);
            throw new RuntimeException(e);
        }

    }
}
