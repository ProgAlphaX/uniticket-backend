package pe.edu.utp.uniticket_backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UsuarioControllerTest {
    @Autowired
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    private MockMvc obtenerMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        }
        return mockMvc;
    }

    @Test
    void listarUsuarios_retornaTodos() throws Exception {
        obtenerMockMvc().perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void buscarPorNombre_conCoincidencia_retornaUsuarios() throws Exception {
        obtenerMockMvc().perform(get("/api/usuarios/buscar").param("nombre", "ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void buscarPorNombre_sinCoincidencia_retornaListaVacia() throws Exception {
        obtenerMockMvc().perform(get("/api/usuarios/buscar").param("nombre", "zzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
