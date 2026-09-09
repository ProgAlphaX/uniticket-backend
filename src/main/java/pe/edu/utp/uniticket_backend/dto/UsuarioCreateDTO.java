package pe.edu.utp.uniticket_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCreateDTO(
        @NotBlank(message = "El nombre completo es obligatorio") String sNombreCompleto,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email no tiene un formato válido") String sEmail,
        @NotBlank(message = "El dni es obligatorio") String sDni,
        String sTelefono,
        @NotBlank(message = "El rol es obligatorio") String sRol
) {
}
