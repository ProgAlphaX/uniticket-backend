package pe.edu.utp.uniticket_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketUpdateDTO(

        @NotNull(message = "El id del administrador es obligatorio")
        Long nId_Admin,

        @NotBlank(message = "La respuesta es obligatoria")
        String sTextoRespuesta

) {
}