package pe.edu.utp.uniticket_backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import pe.edu.utp.uniticket_backend.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) { this.ticketService = ticketService; }

    @PostMapping
    public ResponseEntity<TicketDTO> crearTicket(@Valid @RequestBody TicketCreateDTO datos) {
        TicketDTO ticketCreado = ticketService.crearTicket(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCreado);
    }

    @GetMapping
    public ResponseEntity<List<TicketDTO>> listarTickets() {
        List<TicketDTO> lista = ticketService.listarTickets();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetalleDTO> obtenerTicketPorId(@PathVariable Long id) {
        TicketDetalleDTO ticket = ticketService.obtenerTicketPorId(id);
        return ResponseEntity.ok(ticket);
    }
}
