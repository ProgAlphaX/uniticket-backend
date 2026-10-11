package pe.edu.utp.uniticket_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.uniticket_backend.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}