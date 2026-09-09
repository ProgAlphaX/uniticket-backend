package pe.edu.utp.uniticket_backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import pe.edu.utp.uniticket_backend.dto.TicketUpdateDTO;
import pe.edu.utp.uniticket_backend.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /* 1° Rest: Crear Ticket */
    @PostMapping
    public ResponseEntity<TicketDTO> crearTicket(@Valid @RequestBody TicketCreateDTO datos) {
        TicketDTO ticketCreado = ticketService.crearTicket(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCreado);
    }

    /* 2° Rest: Listar Tickets */
    @GetMapping
    public ResponseEntity<List<TicketDTO>> listarTickets(
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "tipo", required = false) String tipo) {
        List<TicketDTO> lista = ticketService.listarTickets(estado, tipo);
        return ResponseEntity.ok(lista);
    }

    /* 3° Rest: Obtener Ticket por ID */
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetalleDTO> obtenerTicketPorId(@PathVariable("id") Long id) {
        TicketDetalleDTO ticket = ticketService.obtenerTicketPorId(id);
        return ResponseEntity.ok(ticket);
    }

    /* 4° Rest: Resolver Ticket */
    @PutMapping("/{id}/respuesta")
    public ResponseEntity<TicketDetalleDTO> resolverTicket(
            @PathVariable("id") Long id,
            @Valid @RequestBody TicketUpdateDTO datos) {
        TicketDetalleDTO ticketActualizado = ticketService.resolverTicket(id, datos);
        return ResponseEntity.ok(ticketActualizado);
    }
}
