package pe.edu.utp.uniticket_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.uniticket_backend.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findBysEmail(String sEmail);

    List<Usuario> findBysEstado(String sEstado);

    List<Usuario> findBysNombreCompletoContainingIgnoreCase(String nombre);
}
