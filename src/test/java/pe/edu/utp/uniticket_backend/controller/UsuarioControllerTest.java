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

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String USUARIO_VALIDO_JSON = """
            {
              "sNombreCompleto": "Jorge Vidal",
              "sEmail": "jorge.vidal@utp.edu.pe",
              "sDni": "70099887",
              "sTelefono": "987000111",
              "sRol": "ESTANDAR"
            }
            """;

    @Test
    void testListarUsuarios() throws Exception {
        System.out.println("Ejecutando testListarUsuarios");
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Ana Torres"));
    }

    @Test
    void testListarUsuariosFiltradoPorEstado() throws Exception {
        System.out.println("Ejecutando testListarUsuariosFiltradoPorEstado");
        URI uri = new URI("/api/usuarios?estado=INACTIVO");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"INACTIVO\""));
    }

    @Test
    void testListarUsuariosConEstadoInvalido() throws Exception {
        System.out.println("Ejecutando testListarUsuariosConEstadoInvalido");
        URI uri = new URI("/api/usuarios?estado=NOEXISTE");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void testBuscarPorNombreConCoincidencia() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreConCoincidencia");
        URI uri = new URI("/api/usuarios/buscar?nombre=ana");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Ana Torres"));
    }

    @Test
    void testBuscarPorNombreSinCoincidencia() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreSinCoincidencia");
        URI uri = new URI("/api/usuarios/buscar?nombre=zzz");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    void testBuscarPorNombreEsInsensibleAMayusculas() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreEsInsensibleAMayusculas");
        URI uri = new URI("/api/usuarios/buscar?nombre=TORRES");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Ana Torres"));
    }

    @Test
    void testCrearUsuarioConDatosValidos() throws Exception {
        System.out.println("Ejecutando testCrearUsuarioConDatosValidos");
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(USUARIO_VALIDO_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"ACTIVO\""));
    }

    @Test
    void testCrearUsuarioConEmailInvalido() throws Exception {
        System.out.println("Ejecutando testCrearUsuarioConEmailInvalido");
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal",
                  "sEmail": "no-es-un-email",
                  "sDni": "70099887",
                  "sTelefono": "987000111",
                  "sRol": "ESTANDAR"
                }
                """;
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void testCrearUsuarioConRolInvalido() throws Exception {
        System.out.println("Ejecutando testCrearUsuarioConRolInvalido");
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal",
                  "sEmail": "jorge.vidal@utp.edu.pe",
                  "sDni": "70099887",
                  "sTelefono": "987000111",
                  "sRol": "SUPERADMIN"
                }
                """;
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void testActualizarUsuarioConDatosValidos() throws Exception {
        System.out.println("Ejecutando testActualizarUsuarioConDatosValidos");
        String json = """
                {
                  "sNombreCompleto": "Luis Ramírez Paredes",
                  "sEmail": "luis.ramirez@utp.edu.pe",
                  "sEstado": "INACTIVO"
                }
                """;
        URI uri = new URI("/api/usuarios/2");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Luis Ramírez Paredes"));
    }

    @Test
    void testActualizarUsuarioInexistente() throws Exception {
        System.out.println("Ejecutando testActualizarUsuarioInexistente");
        String json = """
                {
                  "sNombreCompleto": "Nombre Cualquiera",
                  "sEmail": "cualquiera@utp.edu.pe",
                  "sEstado": "ACTIVO"
                }
                """;
        URI uri = new URI("/api/usuarios/999");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void testActualizarUsuarioConEstadoInvalido() throws Exception {
        System.out.println("Ejecutando testActualizarUsuarioConEstadoInvalido");
        String json = """
                {
                  "sNombreCompleto": "Selia Quispe",
                  "sEmail": "selia.quispe@utp.edu.pe",
                  "sEstado": "SUSPENDIDO"
                }
                """;
        URI uri = new URI("/api/usuarios/3");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
