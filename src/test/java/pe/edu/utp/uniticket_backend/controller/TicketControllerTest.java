package pe.edu.utp.uniticket_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TicketControllerTest {
    @Autowired
    private TicketController ticketController;

    private MockMvc mockMvc;

    private MockMvc obtenerMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
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
}
