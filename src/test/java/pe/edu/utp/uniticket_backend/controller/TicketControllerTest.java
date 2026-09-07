package pe.edu.utp.uniticket_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.utp.uniticket_backend.exception.GlobalExceptionHandler;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest
class TicketControllerTest {
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
        String json = """
                {
                  "nId_Usuario": 1,
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de matrícula",
                  "sDescripcion": "Necesito la constancia para trámite bancario"
                }
                """;
        obtenerMockMvc().perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(json))
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
    void listarTickets_conFiltrosValidos_retorna200YListaFiltrada() throws Exception {
        
        crearTicket_conDatosValidos_retornaCreated();

        obtenerMockMvc().perform(get("/api/tickets")
                        .param("estado", "PENDIENTE")
                        .param("tipo", "CERTIFICADO"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTickets_conTipoInexistente_retornaBadRequest() throws Exception {
        obtenerMockMvc().perform(get("/api/tickets")
                        .param("tipo", "NOEXISTE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTickets_porUsuarioId_retorna200YListaDeUsuario() throws Exception {
        crearTicket_conDatosValidos_retornaCreated();

        obtenerMockMvc().perform(get("/api/tickets")
                        .param("usuarioId", "1"))
                .andExpect(status().isOk());
    }

    
    @Test
    void obtenerTicketPorId_existente_retorna200YDetalleDTO() throws Exception {
        crearTicket_conDatosValidos_retornaCreated();

        obtenerMockMvc().perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nId_Ticket").value(1));
    }

    @Test
    void obtenerTicketPorId_inexistente_retorna404NotFound() throws Exception {
        obtenerMockMvc().perform(get("/api/tickets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerTicketPorId_recienCreado_retornaResolucionNull() throws Exception {
        crearTicket_conDatosValidos_retornaCreated();

        obtenerMockMvc().perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolucion").value((Object) null));
    }


}
