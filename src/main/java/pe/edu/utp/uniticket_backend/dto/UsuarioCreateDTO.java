package pe.edu.utp.uniticket_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
        @NotBlank(message = "El nombre completo es obligatorio") String sNombreCompleto,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email no tiene un formato válido") String sEmail,
        @NotBlank(message = "El dni es obligatorio") String sDni,
        String sTelefono,
        @NotBlank(message = "El rol es obligatorio") String sRol,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String sPassword
) {
}
