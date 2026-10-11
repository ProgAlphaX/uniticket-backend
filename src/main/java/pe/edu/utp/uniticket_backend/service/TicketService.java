package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.model.Ticket;
import pe.edu.utp.uniticket_backend.repository.TicketRepository;
import pe.edu.utp.uniticket_backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private static final List<String> ESTADOS_VALIDOS =
            List.of("PENDIENTE", "EN_PROCESO", "RESUELTO", "CANCELADO");

    private static final List<String> TIPOS_VALIDOS =
            List.of("CERTIFICADO", "CITA_TUTOR", "PENSION", "DOCUMENTOS");

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    public TicketService(TicketRepository ticketRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TicketDTO crearTicket(TicketCreateDTO datos) {
        // Verificar que el usuario exista en la BD
        if (!usuarioRepository.existsById(datos.nId_Usuario())) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + datos.nId_Usuario());
        }

        Ticket ticket = new Ticket();
        ticket.setNId_Usuario(datos.nId_Usuario());
        ticket.setSTipo(datos.sTipo());
        ticket.setSAsunto(datos.sAsunto());
        ticket.setSDescripcion(datos.sDescripcion());
        ticket.setSEstado(ESTADO_PENDIENTE);
        ticket.setDFecha_Creacion(LocalDateTime.now());

        // Guardar primero para generar el ID en la BD
        Ticket ticketGuardado = ticketRepository.save(ticket);

        // Generar el código SOL-000X usando el ID real de la base de datos
        String sNum_Ticket = String.format("SOL-%04d", ticketGuardado.getNId_Ticket());
        ticketGuardado.setSNum_Ticket(sNum_Ticket);
        
        // Actualizar con el número de ticket definitivo
        ticketGuardado = ticketRepository.save(ticketGuardado);

        return mapearADTO(ticketGuardado);
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
        return ticketRepository.findAll().stream()
                .filter(t -> estado == null || t.getSEstado().equalsIgnoreCase(estado))
                .filter(t -> tipo == null || t.getSTipo().equalsIgnoreCase(tipo))
                .map(this::mapearADTO)
                .toList();
    }

    public TicketDetalleDTO obtenerTicketPorId(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));

        // La resolución se integrará con la entidad de Alexander próximamente
        TicketDetalleDTO.ResolucionDTO resolucion = null;

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