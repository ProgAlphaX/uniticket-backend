package pe.edu.utp.uniticket_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.utp.uniticket_backend.exception.GlobalExceptionHandler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TicketControllerTest {
    private static final String TICKET_VALIDO_JSON = """
            {
              "nId_Usuario": 1,
              "sTipo": "CERTIFICADO",
              "sAsunto": "Constancia de matrícula",
              "sDescripcion": "Necesito la constancia para trámite bancario"
            }
            """;

    @Autowired
    private TicketController ticketController;

    private MockMvc mockMvc;

    private MockMvc obtenerMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.standaloneSetup(ticketController)
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();
        }
        return mockMvc;
    }

    @Test
    void crearTicket_conDatosValidos_retornaCreated() throws Exception {
        obtenerMockMvc().perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(TICKET_VALIDO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sEstado").value("PENDIENTE"));
    }

    @Test
    void crearTicket_conDescripcionVacia_retornaBadRequest() throws Exception {
        String json = """
                {
                  "nId_Usuario": 1,
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de matrícula",
                  "sDescripcion": ""
                }
                """;
        obtenerMockMvc().perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearTicket_sinIdUsuario_retornaBadRequest() throws Exception {
        String json = """
                {
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de matrícula",
                  "sDescripcion": "Descripción válida"
                }
                """;
        obtenerMockMvc().perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTickets_retornaListaConTickets() throws Exception {
        obtenerMockMvc().perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    void obtenerTicketPorId_existente_retorna200YDetalleDTO() throws Exception {
        obtenerMockMvc().perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nId_Ticket").value(1));
    }

    @Test
    void obtenerTicketPorId_inexistente_retorna404NotFound() throws Exception {
        obtenerMockMvc().perform(get("/api/tickets/999"))
                .andExpect(status().isNotFound());
    }
}
