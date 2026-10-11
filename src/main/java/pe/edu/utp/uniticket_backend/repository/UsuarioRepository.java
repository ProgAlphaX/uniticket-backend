package pe.edu.utp.uniticket_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.utp.uniticket_backend.model.Usuario;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findBysEmail(String email);
    boolean existsBysEmail(String email);

    List<Usuario> findBysEstado(String estado);
    List<Usuario> findBysNombreCompletoContainingIgnoreCase(String nombre);
}
