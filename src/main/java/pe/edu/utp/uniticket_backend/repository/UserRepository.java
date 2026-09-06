package pe.edu.utp.uniticket_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.uniticket_backend.model.Usuario;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.sEstado = :sEstado")
    List<Usuario> findBySEstado(@Param("sEstado") String sEstado);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.sNombreCompleto) LIKE LOWER(CONCAT('%', :sNombreCompleto, '%'))")
    List<Usuario> findBySNombreCompletoContainingIgnoreCase(@Param("sNombreCompleto") String sNombreCompleto);
}
