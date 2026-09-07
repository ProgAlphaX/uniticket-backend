package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.model.Ticket;

import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import pe.edu.utp.uniticket_backend.dto.TicketUpdateDTO;
@Service
public class TicketService {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RESUELTO = "RESUELTO";

    private final Map<Long, Ticket> ticketsPorId = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(0);
    private final AtomicLong secuenciaNumTicket = new AtomicLong(0);
    // Para el PUT de Tickets
    private final Map<Long, TicketDetalleDTO.ResolucionDTO> resolucionesPorTicket =
            new ConcurrentHashMap<>();

    private final AtomicLong secuenciaResolucion = new AtomicLong(0);
    // Cierre
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


    public List<TicketDTO> listarTickets(String estado, String tipo, Long usuarioId) {
        return ticketsPorId.values().stream()
                .filter(t -> estado == null || t.getSEstado().equalsIgnoreCase(estado))
                .filter(t -> tipo == null || t.getSTipo().equalsIgnoreCase(tipo))
                .filter(t -> usuarioId == null || t.getNId_Usuario().equals(usuarioId))
                .map(this::mapearADTO)
                .toList();
    }

    public TicketDetalleDTO obtenerTicketPorId(Long id) {
        Ticket ticket = ticketsPorId.get(id);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket no encontrado con ID: " + id);
        }
        // De momento la resolución va como null hasta que se implemente el PUT de resolución
        
        // Implementando resolución para el proceso de PUT
        TicketDetalleDTO.ResolucionDTO resolucion =
        		resolucionesPorTicket.get(id);
        
        return new TicketDetalleDTO(
                ticket.getNId_Ticket(),
                ticket.getSNum_Ticket(),
                ticket.getNId_Usuario(),
                ticket.getSTipo(),
                ticket.getSAsunto(),
                ticket.getSDescripcion(),
                ticket.getSEstado(),
                ticket.getDFecha_Creacion(),
                null
        );
    }
    
    // Para el PUT de Tickets
    public TicketDetalleDTO resolverTicket(Long id, TicketUpdateDTO datos) {

        Ticket ticket = ticketsPorId.get(id);

        if (ticket == null) {
            throw new ResourceNotFoundException(
                    "Ticket no encontrado con ID: " + id);
        }

        ticket.setSEstado(ESTADO_RESUELTO);

        TicketDetalleDTO.ResolucionDTO resolucion =
                new TicketDetalleDTO.ResolucionDTO(
                        secuenciaResolucion.incrementAndGet(),
                        datos.nId_Admin(),
                        datos.sTextoRespuesta(),
                        LocalDateTime.now()
                );

        resolucionesPorTicket.put(id, resolucion);

        return new TicketDetalleDTO(
                ticket.getNId_Ticket(),
                ticket.getSNum_Ticket(),
                ticket.getNId_Usuario(),
                ticket.getSTipo(),
                ticket.getSAsunto(),
                ticket.getSDescripcion(),
                ticket.getSEstado(),
                ticket.getDFecha_Creacion(),
                resolucion
        );
    }
    // CIERRE
}
