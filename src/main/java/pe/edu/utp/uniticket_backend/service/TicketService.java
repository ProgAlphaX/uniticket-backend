package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketService {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private final Map<Long, Ticket> ticketsPorId = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(0);
    private final AtomicLong secuenciaNumTicket = new AtomicLong(0);

    public TicketDTO crearTicket(TicketCreateDTO datos) {
        long nId_Ticket = secuenciaId.incrementAndGet();
        String sNum_Ticket = generarNumTicket();
        Ticket ticket = new Ticket(
                nId_Ticket,
                sNum_Ticket,
                datos.nId_Usuario(),
                datos.sTipo(),
                datos.sAsunto(),
                datos.sDescripcion(),
                ESTADO_PENDIENTE,
                LocalDateTime.now()
        );
        ticketsPorId.put(nId_Ticket, ticket);
        return mapearADTO(ticket);
    }

    private String generarNumTicket() {
        long correlativo = secuenciaNumTicket.incrementAndGet();
        return String.format("SOL-%04d", correlativo);
    }

    private TicketDTO mapearADTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getNId_Ticket(),
                ticket.getSNum_Ticket(),
                ticket.getNId_Usuario(),
                ticket.getSTipo(),
                ticket.getSAsunto(),
                ticket.getSDescripcion(),
                ticket.getSEstado(),
                ticket.getDFecha_Creacion()
        );
    }
}
