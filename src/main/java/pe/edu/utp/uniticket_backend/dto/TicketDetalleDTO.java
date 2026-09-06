package pe.edu.utp.uniticket_backend.dto;

import java.time.LocalDateTime;

public record TicketDetalleDTO(
        Long nId_Ticket,
        String sNum_Ticket,
        Long nId_Usuario,
        String sTipo,
        String sAsunto,
        String sDescripcion,
        String sEstado,
        LocalDateTime dFecha_Creacion,
        ResolucionDTO resolucion
) {
    public record ResolucionDTO(
            Long nId_Resolucion,
            Long nId_Admin,
            String sTextoRespuesta,
            LocalDateTime dFechaRespuesta
    ) {}
}