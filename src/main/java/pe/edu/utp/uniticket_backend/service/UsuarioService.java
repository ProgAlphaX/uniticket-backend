package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
import pe.edu.utp.uniticket_backend.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {
    private static final List<String> ESTADOS_VALIDOS = List.of("ACTIVO", "INACTIVO");

    private final Map<Long, Usuario> usuariosPorId = Map.of(
            1L, new Usuario(1L, "Ana Torres", "ana.torres@utp.edu.pe", "70011122",
                    "987654321", null, "ESTANDAR", "ACTIVO", LocalDateTime.now()),
            2L, new Usuario(2L, "Luis Ramírez", "luis.ramirez@utp.edu.pe", "70033445",
                    "987654322", null, "ADMIN", "ACTIVO", LocalDateTime.now()),
            3L, new Usuario(3L, "Selia Quispe", "selia.quispe@utp.edu.pe", "70055667",
                    "987654323", null, "ESTANDAR", "INACTIVO", LocalDateTime.now())
    );

    public List<UsuarioDTO> listarUsuarios(String estado) {
        if (estado != null && !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
        return usuariosPorId.values().stream()
                .filter(u -> estado == null || u.getSEstado().equalsIgnoreCase(estado))
                .map(this::mapearADTO)
                .toList();
    }

    public List<UsuarioDTO> buscarPorNombre(String nombre) {
        return usuariosPorId.values().stream()
                .filter(u -> u.getSNombreCompleto() != null
                        && u.getSNombreCompleto().toLowerCase().contains(nombre.toLowerCase()))
                .map(this::mapearADTO)
                .toList();
    }

    private UsuarioDTO mapearADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getNId_Usuario(),
                usuario.getSNombreCompleto(),
                usuario.getSEmail(),
                usuario.getSDni(),
                usuario.getSTelefono(),
                usuario.getSRol(),
                usuario.getSEstado()
        );
    }
}
