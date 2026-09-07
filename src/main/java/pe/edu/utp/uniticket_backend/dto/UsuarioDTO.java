package pe.edu.utp.uniticket_backend.dto;

public record UsuarioDTO(
        Long nId_Usuario,
        String sNombreCompleto,
        String sEmail,
        String sDni,
        String sTelefono,
        String sRol,
        String sEstado
) {
}
