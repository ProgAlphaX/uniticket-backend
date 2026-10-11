package pe.edu.utp.uniticket_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.uniticket_backend.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}