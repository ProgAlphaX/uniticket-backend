package pe.edu.utp.uniticket_backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String USUARIO_VALIDO_JSON = """
            {
              "sNombreCompleto": "Jorge Vidal Prueba",
              "sEmail": "jorge.vidal.prueba@utp.edu.pe",
              "sDni": "70099887",
              "sTelefono": "987000111",
              "sRol": "ESTANDAR",
              "sPassword": "clave123"
            }
            """;

    /* Registra en la BD el usuario de prueba y devuelve su id (se revierte al terminar cada test) */
    private String crearUsuarioDePrueba() throws Exception {
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(USUARIO_VALIDO_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        String desdeId = body.substring(body.indexOf("\"nId_Usuario\":") + "\"nId_Usuario\":".length());
        return desdeId.substring(0, desdeId.indexOf(","));
    }

    /* 1° Test: Listar usuarios */
    @Test
    void testListarUsuarios() throws Exception {
        System.out.println("Ejecutando testListarUsuarios");
        crearUsuarioDePrueba();
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Jorge Vidal Prueba"));
    }

    /* 2° Test: Listar usuarios filtrados por estado */
    @Test
    void testListarUsuariosFiltradoPorEstado() throws Exception {
        System.out.println("Ejecutando testListarUsuariosFiltradoPorEstado");
        crearUsuarioDePrueba();
        URI uri = new URI("/api/usuarios?estado=ACTIVO");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Jorge Vidal Prueba"));
        assertFalse(result.getResponse().getContentAsString().contains("\"sEstado\":\"INACTIVO\""));
    }

    /* 3° Test: Listar usuarios con estado inválido */
    @Test
    void testListarUsuariosConEstadoInvalido() throws Exception {
        System.out.println("Ejecutando testListarUsuariosConEstadoInvalido");
        URI uri = new URI("/api/usuarios?estado=NOEXISTE");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    /* 4° Test: Buscar usuario por nombre con coincidencia */
    @Test
    void testBuscarPorNombreConCoincidencia() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreConCoincidencia");
        crearUsuarioDePrueba();
        URI uri = new URI("/api/usuarios/buscar?nombre=Vidal");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Jorge Vidal Prueba"));
    }

    /* 5° Test: Buscar usuario por nombre sin coincidencia */
    @Test
    void testBuscarPorNombreSinCoincidencia() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreSinCoincidencia");
        URI uri = new URI("/api/usuarios/buscar?nombre=zzzqqqxxx");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    /* 6° Test: Buscar usuario por nombre de forma insensible a mayúsculas */
    @Test
    void testBuscarPorNombreEsInsensibleAMayusculas() throws Exception {
        System.out.println("Ejecutando testBuscarPorNombreEsInsensibleAMayusculas");
        crearUsuarioDePrueba();
        URI uri = new URI("/api/usuarios/buscar?nombre=VIDAL");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Jorge Vidal Prueba"));
    }

    /* 7° Test: Crear usuario con datos válidos */
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

    /* 8° Test: Crear usuario con email inválido */
    @Test
    void testCrearUsuarioConEmailInvalido() throws Exception {
        System.out.println("Ejecutando testCrearUsuarioConEmailInvalido");
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal Prueba",
                  "sEmail": "no-es-un-email",
                  "sDni": "70099887",
                  "sTelefono": "987000111",
                  "sRol": "ESTANDAR",
                  "sPassword": "clave123"
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

    /* 9° Test: Crear usuario con rol inválido */
    @Test
    void testCrearUsuarioConRolInvalido() throws Exception {
        System.out.println("Ejecutando testCrearUsuarioConRolInvalido");
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal Prueba",
                  "sEmail": "jorge.vidal.prueba@utp.edu.pe",
                  "sDni": "70099887",
                  "sTelefono": "987000111",
                  "sRol": "SUPERADMIN",
                  "sPassword": "clave123"
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

    /* 10° Test: Actualizar usuario con datos válidos */
    @Test
    void testActualizarUsuarioConDatosValidos() throws Exception {
        System.out.println("Ejecutando testActualizarUsuarioConDatosValidos");
        String id = crearUsuarioDePrueba();
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal Actualizado",
                  "sEmail": "jorge.vidal.prueba@utp.edu.pe",
                  "sEstado": "INACTIVO"
                }
                """;
        URI uri = new URI("/api/usuarios/" + id);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Jorge Vidal Actualizado"));
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"INACTIVO\""));
    }

    /* 11° Test: Actualizar usuario inexistente */
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
        URI uri = new URI("/api/usuarios/999999999");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    /* 12° Test: Actualizar usuario con estado inválido */
    @Test
    void testActualizarUsuarioConEstadoInvalido() throws Exception {
        System.out.println("Ejecutando testActualizarUsuarioConEstadoInvalido");
        String id = crearUsuarioDePrueba();
        String json = """
                {
                  "sNombreCompleto": "Jorge Vidal Prueba",
                  "sEmail": "jorge.vidal.prueba@utp.edu.pe",
                  "sEstado": "SUSPENDIDO"
                }
                """;
        URI uri = new URI("/api/usuarios/" + id);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
