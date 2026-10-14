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
class TicketControllerTest {
    @Autowired
    MockMvc mockMvc;

    private static final String ID_INEXISTENTE = "999999999";

    /* Extrae el valor numérico de un campo id del JSON de respuesta */
    private String extraerId(String body, String campo) {
        String clave = "\"" + campo + "\":";
        String desdeId = body.substring(body.indexOf(clave) + clave.length());
        return desdeId.substring(0, desdeId.indexOf(","));
    }

    /* Registra en la BD un usuario de prueba con el rol indicado y devuelve su id */
    private String crearUsuarioDePrueba(String rol) throws Exception {
        String json = """
                {
                  "sNombreCompleto": "Usuario Prueba %s",
                  "sEmail": "prueba.%s@utp.edu.pe",
                  "sDni": "70011122",
                  "sTelefono": "987654321",
                  "sRol": "%s",
                  "sPassword": "clave123"
                }
                """.formatted(rol, rol.toLowerCase(), rol);
        URI uri = new URI("/api/usuarios");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        return extraerId(result.getResponse().getContentAsString(), "nId_Usuario");
    }

    private String ticketValidoJson(String idUsuario) {
        return """
                {
                  "nId_Usuario": %s,
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de prueba",
                  "sDescripcion": "Necesito la constancia para un tramite bancario"
                }
                """.formatted(idUsuario);
    }

    /* Registra en la BD un ticket de prueba (con su usuario) y devuelve el id del ticket */
    private String crearTicketDePrueba() throws Exception {
        String idUsuario = crearUsuarioDePrueba("ESTANDAR");
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ticketValidoJson(idUsuario))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        return extraerId(result.getResponse().getContentAsString(), "nId_Ticket");
    }

    private String respuestaJson(String idAdmin, String texto) {
        return """
                {
                  "nId_Admin": %s,
                  "sTexto_Respuesta": "%s"
                }
                """.formatted(idAdmin, texto);
    }

    /* 1° Test: Crear Ticket con datos válidos */
    @Test
    void testCrearTicketConDatosValidos() throws Exception {
        System.out.println("Ejecutando testCrearTicketConDatosValidos");
        String idUsuario = crearUsuarioDePrueba("ESTANDAR");
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ticketValidoJson(idUsuario))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"PENDIENTE\""));
        assertTrue(result.getResponse().getContentAsString().contains("\"sNum_Ticket\":\"SOL-"));
    }

    /* 2° Test: Crear ticket con descripción vacía */
    @Test
    void testCrearTicketConDescripcionVacia() throws Exception {
        System.out.println("Ejecutando testCrearTicketConDescripcionVacia");
        String idUsuario = crearUsuarioDePrueba("ESTANDAR");
        String json = """
                {
                  "nId_Usuario": %s,
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de prueba",
                  "sDescripcion": ""
                }
                """.formatted(idUsuario);
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    /* 3° Test: Crear ticket sin ID de usuario */
    @Test
    void testCrearTicketSinIdUsuario() throws Exception {
        System.out.println("Ejecutando testCrearTicketSinIdUsuario");
        String json = """
                {
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de prueba",
                  "sDescripcion": "Descripcion valida"
                }
                """;
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    /* 4° Test: Listar tickets */
    @Test
    void testListarTickets() throws Exception {
        System.out.println("Ejecutando testListarTickets");
        String idTicket = crearTicketDePrueba();
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"nId_Ticket\":" + idTicket + ","));
    }

    /* 5° Test: Listar tickets filtrados por estado */
    @Test
    void testListarTicketsFiltradoPorEstado() throws Exception {
        System.out.println("Ejecutando testListarTicketsFiltradoPorEstado");
        String idTicket = crearTicketDePrueba();
        URI uri = new URI("/api/tickets?estado=PENDIENTE");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"nId_Ticket\":" + idTicket + ","));
        assertFalse(result.getResponse().getContentAsString().contains("\"sEstado\":\"RESUELTO\""));
    }

    /* 6° Test: Listar tickets con estado inválido */
    @Test
    void testListarTicketsConEstadoInvalido() throws Exception {
        System.out.println("Ejecutando testListarTicketsConEstadoInvalido");
        URI uri = new URI("/api/tickets?estado=NOEXISTE");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    /* 7° Test: Obtener ticket por ID existente */
    @Test
    void testObtenerTicketPorIdExistente() throws Exception {
        System.out.println("Ejecutando testObtenerTicketPorIdExistente");
        String idTicket = crearTicketDePrueba();
        URI uri = new URI("/api/tickets/" + idTicket);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"nId_Ticket\":" + idTicket + ","));
        assertTrue(result.getResponse().getContentAsString().contains("Constancia de prueba"));
    }

    /* 8° Test: Obtener ticket por ID inexistente */
    @Test
    void testObtenerTicketPorIdInexistente() throws Exception {
        System.out.println("Ejecutando testObtenerTicketPorIdInexistente");
        URI uri = new URI("/api/tickets/" + ID_INEXISTENTE);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    /* 9° Test: Obtener ticket por ID sin resolución */
    @Test
    void testObtenerTicketPorIdSinResolucionAun() throws Exception {
        System.out.println("Ejecutando testObtenerTicketPorIdSinResolucionAun");
        String idTicket = crearTicketDePrueba();
        URI uri = new URI("/api/tickets/" + idTicket);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"resolucion\":null"));
    }

    /* 10° Test: Resolver ticket con datos válidos */
    @Test
    void testResolverTicketConDatosValidos() throws Exception {
        System.out.println("Ejecutando testResolverTicketConDatosValidos");
        String idTicket = crearTicketDePrueba();
        String idAdmin = crearUsuarioDePrueba("ADMIN");
        URI uri = new URI("/api/tickets/" + idTicket + "/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(respuestaJson(idAdmin, "Tu constancia ya fue generada"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"RESUELTO\""));
        assertTrue(result.getResponse().getContentAsString().contains("Tu constancia ya fue generada"));
    }

    /* 11° Test: Resolver ticket con ticket inexistente */
    @Test
    void testResolverTicketConTicketInexistente() throws Exception {
        System.out.println("Ejecutando testResolverTicketConTicketInexistente");
        String idAdmin = crearUsuarioDePrueba("ADMIN");
        URI uri = new URI("/api/tickets/" + ID_INEXISTENTE + "/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(respuestaJson(idAdmin, "Respuesta de prueba"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    /* 12° Test: Resolver ticket con texto de respuesta vacío */
    @Test
    void testResolverTicketConTextoRespuestaVacio() throws Exception {
        System.out.println("Ejecutando testResolverTicketConTextoRespuestaVacio");
        String idTicket = crearTicketDePrueba();
        String idAdmin = crearUsuarioDePrueba("ADMIN");
        URI uri = new URI("/api/tickets/" + idTicket + "/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(respuestaJson(idAdmin, ""))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    /* 13° Test: Resolver ticket con administrador inexistente */
    @Test
    void testResolverTicketConAdminInexistente() throws Exception {
        System.out.println("Ejecutando testResolverTicketConAdminInexistente");
        String idTicket = crearTicketDePrueba();
        URI uri = new URI("/api/tickets/" + idTicket + "/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(respuestaJson(ID_INEXISTENTE, "Respuesta de prueba"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Administrador no encontrado"));
    }

    /* 14° Test: Resolver ticket con un usuario que no es administrador */
    @Test
    void testResolverTicketConUsuarioSinRolAdmin() throws Exception {
        System.out.println("Ejecutando testResolverTicketConUsuarioSinRolAdmin");
        String idTicket = crearTicketDePrueba();
        String idNoAdmin = extraerIdUsuarioDelTicket(idTicket);
        URI uri = new URI("/api/tickets/" + idTicket + "/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(respuestaJson(idNoAdmin, "Respuesta de prueba"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("no es administrador"));
    }

    /* Devuelve el id del usuario (rol ESTANDAR) dueño del ticket indicado */
    private String extraerIdUsuarioDelTicket(String idTicket) throws Exception {
        URI uri = new URI("/api/tickets/" + idTicket);
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        return extraerId(result.getResponse().getContentAsString(), "nId_Usuario");
    }
}
