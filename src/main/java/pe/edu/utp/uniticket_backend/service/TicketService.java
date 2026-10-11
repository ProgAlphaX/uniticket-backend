package pe.edu.utp.uniticket_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import pe.edu.utp.uniticket_backend.dto.TicketUpdateDTO;
import pe.edu.utp.uniticket_backend.exception.ResourceNotFoundException;
import pe.edu.utp.uniticket_backend.model.Resolucion;
import pe.edu.utp.uniticket_backend.model.Ticket;
import pe.edu.utp.uniticket_backend.model.Usuario;
import pe.edu.utp.uniticket_backend.repository.ResolucionRepository;
import pe.edu.utp.uniticket_backend.repository.TicketRepository;
import pe.edu.utp.uniticket_backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RESUELTO = "RESUELTO";
    private static final String ROL_ADMIN = "ADMIN";

    private static final List<String> ESTADOS_VALIDOS =
            List.of("PENDIENTE", "EN_PROCESO", "RESUELTO", "CANCELADO");

    private static final List<String> TIPOS_VALIDOS =
            List.of("CERTIFICADO", "CITA_TUTOR", "PENSION", "DOCUMENTOS");

    private final TicketRepository ticketRepository;
    private final ResolucionRepository resolucionRepository;
    private final UsuarioRepository usuarioRepository;

    public TicketService(TicketRepository ticketRepository, ResolucionRepository resolucionRepository,
                         UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.resolucionRepository = resolucionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TicketDTO crearTicket(TicketCreateDTO datos) {
        if (!usuarioRepository.existsById(datos.nId_Usuario())) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + datos.nId_Usuario());
        }

        Ticket ticket = new Ticket(
                null,
                null,
                datos.nId_Usuario(),
                datos.sTipo(),
                datos.sAsunto(),
                datos.sDescripcion(),
                ESTADO_PENDIENTE,
                LocalDateTime.now()
        );

        Ticket guardado = ticketRepository.save(ticket);
        guardado.setSNum_Ticket(generarNumTicket(guardado.getNId_Ticket()));
        guardado = ticketRepository.save(guardado);

        return mapearADTO(guardado);
    }

    private String generarNumTicket(Long nId_Ticket) {
        return String.format("SOL-%04d", nId_Ticket);
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

    private TicketDetalleDTO mapearADetalleDTO(Ticket ticket, Resolucion resolucion) {
        TicketDetalleDTO.ResolucionDTO resolucionDTO = null;
        if (resolucion != null) {
            resolucionDTO = new TicketDetalleDTO.ResolucionDTO(
                    resolucion.getNId_Resolucion(),
                    resolucion.getNId_Admin(),
                    resolucion.getSTexto_Respuesta(),
                    resolucion.getDFecha_Respuesta()
            );
        }

        return new TicketDetalleDTO(
                ticket.getNId_Ticket(),
                ticket.getSNum_Ticket(),
                ticket.getNId_Usuario(),
                ticket.getSTipo(),
                ticket.getSAsunto(),
                ticket.getSDescripcion(),
                ticket.getSEstado(),
                ticket.getDFecha_Creacion(),
                resolucionDTO
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

        Resolucion resolucion = resolucionRepository.findByTicket(ticket).orElse(null);

        return mapearADetalleDTO(ticket, resolucion);
    }

    @Transactional
    public TicketDetalleDTO resolverTicket(Long id, TicketUpdateDTO datos) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));

        Usuario admin = usuarioRepository.findById(datos.nId_Admin())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Administrador no encontrado con ID: " + datos.nId_Admin()));

        if (!ROL_ADMIN.equals(admin.getSRol())) {
            throw new IllegalArgumentException("El usuario indicado no es administrador");
        }

        ticket.setSEstado(ESTADO_RESUELTO);
        ticketRepository.save(ticket);

        Resolucion resolucion = resolucionRepository.findByTicket(ticket).orElse(new Resolucion());
        resolucion.setTicket(ticket);
        resolucion.setNId_Admin(datos.nId_Admin());
        resolucion.setSTexto_Respuesta(datos.sTexto_Respuesta());
        resolucion.setDFecha_Respuesta(LocalDateTime.now());
        Resolucion guardada = resolucionRepository.save(resolucion);

        return mapearADetalleDTO(ticket, guardada);
    }
}