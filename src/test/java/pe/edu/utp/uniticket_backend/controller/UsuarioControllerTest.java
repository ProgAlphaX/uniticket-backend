package pe.edu.utp.uniticket_backend.controller;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.uniticket_backend.service.UsuarioService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    public void testListarUsuarios() throws Exception {
        when(usuarioService.listarUsuarios(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBuscarPorNombre() throws Exception {
        when(usuarioService.buscarPorNombre("selia")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("nombre", "selia"))
                .andExpect(status().isOk());
    }
}
