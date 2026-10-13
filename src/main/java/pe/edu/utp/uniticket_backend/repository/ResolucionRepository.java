package pe.edu.utp.uniticket_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.uniticket_backend.model.Resolucion;
import pe.edu.utp.uniticket_backend.model.Ticket;

import java.util.Optional;

public interface ResolucionRepository extends JpaRepository<Resolucion, Long> {
    Optional<Resolucion> findByTicket(Ticket ticket);
}
