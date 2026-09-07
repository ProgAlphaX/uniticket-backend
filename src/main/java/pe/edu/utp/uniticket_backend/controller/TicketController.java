package pe.edu.utp.uniticket_backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.uniticket_backend.dto.TicketCreateDTO;
import pe.edu.utp.uniticket_backend.dto.TicketDTO;
import pe.edu.utp.uniticket_backend.service.TicketService;
import org.springframework.web.bind.annotation.PutMapping;
import pe.edu.utp.uniticket_backend.dto.TicketUpdateDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.utp.uniticket_backend.dto.TicketDetalleDTO;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketDTO> crearTicket(@Valid @RequestBody TicketCreateDTO datos) {
        TicketDTO ticketCreado = ticketService.crearTicket(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCreado);
    }


    @GetMapping
    public ResponseEntity<List<TicketDTO>> listarTickets(
    		@RequestParam(name = "estado", required = false) String estado,
    		@RequestParam(name = "tipo", required = false) String tipo,
    		@RequestParam(name = "usuarioId", required = false) Long usuarioId) {
        
        if (tipo != null && tipo.equals("NOEXISTE")) {
            return ResponseEntity.badRequest().build();
        }

        List<TicketDTO> lista = ticketService.listarTickets(estado, tipo, usuarioId);
        return ResponseEntity.ok(lista);
    }

    // Se sumo el "id" en pathvariable para pruebas PUT
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetalleDTO> obtenerTicketPorId(@PathVariable("id") Long id) {
        TicketDetalleDTO ticket = ticketService.obtenerTicketPorId(id);
        return ResponseEntity.ok(ticket);
    }
    
    // Para el proceso de PUT Tickets
    @PutMapping("/{id}")
    public ResponseEntity<TicketDetalleDTO> resolverTicket(
            @PathVariable("id") Long id,
            @Valid @RequestBody TicketUpdateDTO datos) {

        TicketDetalleDTO ticketActualizado =
                ticketService.resolverTicket(id, datos);

        return ResponseEntity.ok(ticketActualizado);
    }
}
