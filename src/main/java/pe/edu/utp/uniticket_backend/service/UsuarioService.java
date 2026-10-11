package pe.edu.utp.uniticket_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.UsuarioCreateDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioUpdateDTO;
import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.model.Usuario;
import pe.edu.utp.uniticket_backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {
    private static final List<String> ESTADOS_VALIDOS = List.of("ACTIVO", "INACTIVO");
    private static final List<String> ROLES_VALIDOS = List.of("ADMIN", "ESTANDAR");
    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDTO> listarUsuarios(String estado) {
        if (estado != null && !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
        List<Usuario> usuarios = estado == null
                ? usuarioRepository.findAll()
                : usuarioRepository.findBysEstado(estado.toUpperCase());
        return usuarios.stream()
                .map(this::mapearADTO)
                .toList();
    }

    public List<UsuarioDTO> buscarPorNombre(String nombre) {
        return usuarioRepository.findBysNombreCompletoContainingIgnoreCase(nombre).stream()
                .map(this::mapearADTO)
                .toList();
    }

    public UsuarioDTO crearUsuario(UsuarioCreateDTO datos) {
        if (!ROLES_VALIDOS.contains(datos.sRol().toUpperCase())) {
            throw new IllegalArgumentException("Rol no válido: " + datos.sRol());
        }
        if (usuarioRepository.findBysEmail(datos.sEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + datos.sEmail());
        }

        Usuario usuario = new Usuario(
                null,
                datos.sNombreCompleto(),
                datos.sEmail(),
                datos.sDni(),
                datos.sTelefono(),
                passwordEncoder.encode(datos.sPassword()),
                datos.sRol().toUpperCase(),
                ESTADO_ACTIVO,
                LocalDateTime.now()
        );

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearADTO(guardado);
    }

    public UsuarioDTO actualizarUsuario(Long id, UsuarioUpdateDTO datos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (!ESTADOS_VALIDOS.contains(datos.sEstado().toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + datos.sEstado());
        }

        usuario.setSNombreCompleto(datos.sNombreCompleto());
        usuario.setSEmail(datos.sEmail());
        usuario.setSEstado(datos.sEstado().toUpperCase());

        Usuario actualizado = usuarioRepository.save(usuario);
        return mapearADTO(actualizado);
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