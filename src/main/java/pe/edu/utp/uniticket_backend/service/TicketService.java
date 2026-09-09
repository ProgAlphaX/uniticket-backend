package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import pe.edu.utp.uniticket_backend.dto.TicketUpdateDTO;
import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketService {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RESUELTO = "RESUELTO";

    private static final List<String> ESTADOS_VALIDOS =
            List.of("PENDIENTE", "EN_PROCESO", "RESUELTO", "CANCELADO");

    private static final List<String> TIPOS_VALIDOS =
            List.of("CERTIFICADO", "CITA_TUTOR", "PENSION", "DOCUMENTOS");

    private final Map<Long, Ticket> ticketsPorId = new ConcurrentHashMap<>(Map.of(
            1L, new Ticket(1L, "SOL-0001", 1L, "CERTIFICADO", "Constancia de matrícula",
                    "Necesito la constancia para un trámite bancario", "PENDIENTE", LocalDateTime.now()),
            2L, new Ticket(2L, "SOL-0002", 2L, "CITA_TUTOR", "Cita con tutor de ciclo",
                    "Quisiera coordinar una cita para revisar mi avance académico", "EN_PROCESO", LocalDateTime.now()),
            3L, new Ticket(3L, "SOL-0003", 3L, "PENSION", "Consulta de pensión",
                    "Necesito el detalle de mi pensión del ciclo actual", "RESUELTO", LocalDateTime.now())
    ));
    private final AtomicLong secuenciaId = new AtomicLong(3);
    private final AtomicLong secuenciaNumTicket = new AtomicLong(3);

    private final Map<Long, TicketDetalleDTO.ResolucionDTO> resolucionesPorTicket = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaResolucion = new AtomicLong(0);

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

    public List<TicketDTO> listarTickets(String estado, String tipo) {
        if (estado != null && !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
        if (tipo != null && !TIPOS_VALIDOS.contains(tipo.toUpperCase())) {
            throw new IllegalArgumentException("Tipo de ticket no válido: " + tipo);
        }
        return ticketsPorId.values().stream()
                .filter(t -> estado == null || t.getSEstado().equalsIgnoreCase(estado))
                .filter(t -> tipo == null || t.getSTipo().equalsIgnoreCase(tipo))
                .map(this::mapearADTO)
                .toList();
    }

    public TicketDetalleDTO obtenerTicketPorId(Long id) {
        Ticket ticket = ticketsPorId.get(id);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket no encontrado con ID: " + id);
        }

        TicketDetalleDTO.ResolucionDTO resolucion = resolucionesPorTicket.get(id);

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

    public TicketDetalleDTO resolverTicket(Long id, TicketUpdateDTO datos) {
        Ticket ticket = ticketsPorId.get(id);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket no encontrado con ID: " + id);
        }

        ticket.setSEstado(ESTADO_RESUELTO);

        TicketDetalleDTO.ResolucionDTO resolucion = new TicketDetalleDTO.ResolucionDTO(
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
}
