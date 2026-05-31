package emanuelepiemonte.Trackfolio.payload;

import emanuelepiemonte.Trackfolio.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "Il nome proprio è obbligatorio e non può essere una stringa vuota")
        @Size(min = 2, max = 30, message = "Il nome proprio deve essere compreso tra i 2 e i 30 caratteri")
        String name,
        @NotBlank(message = "Il cognome è obbligatorio e non può essere una stringa vuota")
        @Size(min = 2, max = 30, message = "Il cognome deve essere compreso tra i 2 e i 30 caratteri")
        String surname,
        @NotBlank(message = "L'email è obbligatorio e non può essere una stringa vuota")
        @Email(message = "L'email inserita non è del formato corretto")
        String email,
        @Size(min = 4, message = "La password deve avere almeno 4 caratteri")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,}$", message = "La password deve contenere almeno una lettera maiuscola, una lettera minuscola, almeno un numero e una lunghezza minima di 4 caratteri")
        String password,
        String avatar,
        Role role

) {
}
