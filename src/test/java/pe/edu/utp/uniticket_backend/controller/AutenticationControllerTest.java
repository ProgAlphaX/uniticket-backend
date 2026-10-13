package pe.edu.utp.uniticket_backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutenticacionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testLoginValido() throws Exception {

        System.out.println("Ejecutando testLoginValido");

        String json = """
                {
                  "sEmail": "julio.admin@utp.edu.pe",
                  "sPassword": "Clave1234"
                }
                """;

        URI uri = new URI("/api/auth/login");

        MockHttpServletRequestBuilder req =
                MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(req).andReturn();

        assertEquals(
                HttpStatus.OK.value(),
                result.getResponse().getStatus());

        String token =
                result.getResponse().getContentAsString();

        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testLoginUsuarioNoExiste() throws Exception {

        System.out.println("Ejecutando testLoginUsuarioNoExiste");

        String json = """
                {
                  "sEmail": "noexiste@utp.edu.pe",
                  "sPassword": "Clave1234"
                }
                """;

        URI uri = new URI("/api/auth/login");

        MockHttpServletRequestBuilder req =
                MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(req).andReturn();

        assertEquals(
                HttpStatus.UNAUTHORIZED.value(),
                result.getResponse().getStatus());
    }
    
    @Test
    void testLoginPasswordIncorrecta() throws Exception {

        System.out.println("Ejecutando testLoginPasswordIncorrecta");

        String json = """
                {
                  "sEmail": "julio.admin@utp.edu.pe",
                  "sPassword": "ClaveIncorrecta"
                }
                """;

        URI uri = new URI("/api/auth/login");

        MockHttpServletRequestBuilder req =
                MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(req).andReturn();

        assertEquals(
                HttpStatus.UNAUTHORIZED.value(),
                result.getResponse().getStatus());
    }

    @Test
    void testLoginUsuarioInactivo() {

        System.out.println("Ejecutando testLoginUsuarioInactivo");

        /* Pendiente */
    }
        
}
