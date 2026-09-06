package pe.edu.utp.uniticket_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCreateDTO(
        @NotNull(message = "El id de usuario es obligatorio") Long nId_Usuario,
        @NotBlank(message = "El tipo de ticket es obligatorio") String sTipo,
        @NotBlank(message = "El asunto es obligatorio") String sAsunto,
        @NotBlank(message = "La descripción es obligatoria") String sDescripcion
) {
}
