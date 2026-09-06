package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
import pe.edu.utp.uniticket_backend.model.Usuario;
import pe.edu.utp.uniticket_backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UserRepository userRepository;

    public UsuarioService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UsuarioDTO> listarUsuarios(String estado) {
        List<Usuario> usuarios;
        if (estado != null && !estado.isBlank()) {
            usuarios = userRepository.findBySEstado(estado);
        } else {
            usuarios = userRepository.findAll();
        }
        return usuarios.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarPorNombre(String nombre) {
        List<Usuario> usuarios = userRepository.findBySNombreCompletoContainingIgnoreCase(nombre);
        return usuarios.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    private UsuarioDTO convertirADto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getNId_Usuario());
        dto.setNombreCompleto(usuario.getSNombreCompleto());
        dto.setEmail(usuario.getSEmail());
        dto.setDni(usuario.getSDni());
        dto.setTelefono(usuario.getSTelefono());
        dto.setRol(usuario.getSRol());
        dto.setEstado(usuario.getSEstado());
        return dto;
    }
}
