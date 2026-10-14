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

    private static final String EMAIL_ADMIN = "julio.admin@utp.edu.pe";
    private static final String PASSWORD_ADMIN = "123456";

    private String loginJson(String email, String password) {
        return """
                {
                  "sEmail": "%s",
                  "sPassword": "%s"
                }
                """.formatted(email, password);
    }

    private MvcResult login(String email, String password) throws Exception {
        URI uri = new URI("/api/auth/login");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson(email, password))
                .accept(MediaType.APPLICATION_JSON);
        return mockMvc.perform(req).andReturn();
    }

    /* 1° Test: Login con credenciales válidas */
    @Test
    void testLoginConCredencialesValidas() throws Exception {
        System.out.println("Ejecutando testLoginConCredencialesValidas");
        MvcResult result = login(EMAIL_ADMIN, PASSWORD_ADMIN);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        String token = result.getResponse().getContentAsString();
        assertEquals(3, token.split("\\.").length);
    }

    /* 2° Test: Login con password incorrecta */
    @Test
    void testLoginConPasswordIncorrecta() throws Exception {
        System.out.println("Ejecutando testLoginConPasswordIncorrecta");
        MvcResult result = login(EMAIL_ADMIN, "incorrecta");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
    }

    /* 3° Test: Login con un usuario inactivo */
    @Test
    void testLoginConUsuarioInactivo() throws Exception {
        System.out.println("Ejecutando testLoginConUsuarioInactivo");
        String token = login(EMAIL_ADMIN, PASSWORD_ADMIN).getResponse().getContentAsString();

        String usuarioJson = """
                {
                  "sNombreCompleto": "Usuario Inactivo Prueba",
                  "sEmail": "inactivo.prueba@utp.edu.pe",
                  "sDni": "70055667",
                  "sTelefono": "987654323",
                  "sRol": "ESTANDAR",
                  "sPassword": "clave123"
                }
                """;
        MockHttpServletRequestBuilder reqCrear = MockMvcRequestBuilders.post(new URI("/api/usuarios"))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(usuarioJson)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult creado = mockMvc.perform(reqCrear).andReturn();
        assertEquals(HttpStatus.CREATED.value(), creado.getResponse().getStatus());
        String body = creado.getResponse().getContentAsString();
        String desdeId = body.substring(body.indexOf("\"nId_Usuario\":") + "\"nId_Usuario\":".length());
        String id = desdeId.substring(0, desdeId.indexOf(","));

        String inactivarJson = """
                {
                  "sNombreCompleto": "Usuario Inactivo Prueba",
                  "sEmail": "inactivo.prueba@utp.edu.pe",
                  "sEstado": "INACTIVO"
                }
                """;
        MockHttpServletRequestBuilder reqInactivar = MockMvcRequestBuilders.put(new URI("/api/usuarios/" + id))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inactivarJson)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult inactivado = mockMvc.perform(reqInactivar).andReturn();
        assertEquals(HttpStatus.OK.value(), inactivado.getResponse().getStatus());

        MvcResult result = login("inactivo.prueba@utp.edu.pe", "clave123");
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("inactivo"));
    }
}
