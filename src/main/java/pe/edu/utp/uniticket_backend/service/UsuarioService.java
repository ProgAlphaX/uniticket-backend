package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.UsuarioCreateDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioUpdateDTO;
import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {
    private static final List<String> ESTADOS_VALIDOS = List.of("ACTIVO", "INACTIVO");
    private static final List<String> ROLES_VALIDOS = List.of("ADMIN", "ESTANDAR");
    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final Map<Long, Usuario> usuariosPorId = new ConcurrentHashMap<>(Map.of(
            1L, new Usuario(1L, "Ana Torres", "ana.torres@utp.edu.pe", "70011122",
                    "987654321", null, "ESTANDAR", "ACTIVO", LocalDateTime.now()),
            2L, new Usuario(2L, "Luis Ramírez", "luis.ramirez@utp.edu.pe", "70033445",
                    "987654322", null, "ADMIN", "ACTIVO", LocalDateTime.now()),
            3L, new Usuario(3L, "Selia Quispe", "selia.quispe@utp.edu.pe", "70055667",
                    "987654323", null, "ESTANDAR", "INACTIVO", LocalDateTime.now())
    ));
    private final AtomicLong secuenciaId = new AtomicLong(3);

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

    public UsuarioDTO crearUsuario(UsuarioCreateDTO datos) {
        if (!ROLES_VALIDOS.contains(datos.sRol().toUpperCase())) {
            throw new IllegalArgumentException("Rol no válido: " + datos.sRol());
        }

        long nId_Usuario = secuenciaId.incrementAndGet();
        Usuario usuario = new Usuario(
                nId_Usuario,
                datos.sNombreCompleto(),
                datos.sEmail(),
                datos.sDni(),
                datos.sTelefono(),
                null,
                datos.sRol().toUpperCase(),
                ESTADO_ACTIVO,
                LocalDateTime.now()
        );

        usuariosPorId.put(nId_Usuario, usuario);
        return mapearADTO(usuario);
    }

    public UsuarioDTO actualizarUsuario(Long id, UsuarioUpdateDTO datos) {
        Usuario usuario = usuariosPorId.get(id);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }

        if (!ESTADOS_VALIDOS.contains(datos.sEstado().toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + datos.sEstado());
        }

        usuario.setSNombreCompleto(datos.sNombreCompleto());
        usuario.setSEmail(datos.sEmail());
        usuario.setSEstado(datos.sEstado().toUpperCase());

        return mapearADTO(usuario);
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
