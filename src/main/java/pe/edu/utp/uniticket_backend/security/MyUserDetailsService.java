package pe.edu.utp.uniticket_backend.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.model.Usuario;
import pe.edu.utp.uniticket_backend.repository.UsuarioRepository;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public MyUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findBysEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        boolean habilitado = "ACTIVO".equals(usuario.getSEstado());

        return new org.springframework.security.core.userdetails.User(
                usuario.getSEmail(),
                usuario.getSPasswordHash(),
                habilitado,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(usuario.getSRol()))
        );
    }
}
