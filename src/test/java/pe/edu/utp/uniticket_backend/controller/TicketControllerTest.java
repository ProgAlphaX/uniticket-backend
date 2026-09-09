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
class TicketControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String TICKET_VALIDO_JSON = """
            {
              "nId_Usuario": 1,
              "sTipo": "CERTIFICADO",
              "sAsunto": "Constancia de matrícula",
              "sDescripcion": "Necesito la constancia para trámite bancario"
            }
            """;

    /* 1° Test: Crear Ticket con datos válidos */
    @Test
    void testCrearTicketConDatosValidos() throws Exception {
        System.out.println("Ejecutando testCrearTicketConDatosValidos");
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TICKET_VALIDO_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"PENDIENTE\""));
    }

    /* 2° Test: Crear ticket con descripción vacía */
    @Test
    void testCrearTicketConDescripcionVacia() throws Exception {
        System.out.println("Ejecutando testCrearTicketConDescripcionVacia");
        String json = """
                {
                  "nId_Usuario": 1,
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de matrícula",
                  "sDescripcion": ""
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

    /* 3° Test: Crear ticket sin ID de usuario */
    @Test
    void testCrearTicketSinIdUsuario() throws Exception {
        System.out.println("Ejecutando testCrearTicketSinIdUsuario");
        String json = """
                {
                  "sTipo": "CERTIFICADO",
                  "sAsunto": "Constancia de matrícula",
                  "sDescripcion": "Descripción válida"
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
        URI uri = new URI("/api/tickets");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("SOL-0001"));
    }

    /* 5° Test: Listar tickets filtrados por estado */
    @Test
    void testListarTicketsFiltradoPorEstado() throws Exception {
        System.out.println("Ejecutando testListarTicketsFiltradoPorEstado");
        URI uri = new URI("/api/tickets?estado=RESUELTO");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"RESUELTO\""));
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
        URI uri = new URI("/api/tickets/1");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"nId_Ticket\":1"));
    }

    /* 8° Test: Obtener ticket por ID inexistente */
    @Test
    void testObtenerTicketPorIdInexistente() throws Exception {
        System.out.println("Ejecutando testObtenerTicketPorIdInexistente");
        URI uri = new URI("/api/tickets/999");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    /* 9° Test: Obtener ticket por ID sin resolución */
    @Test
    void testObtenerTicketPorIdSinResolucionAun() throws Exception {
        System.out.println("Ejecutando testObtenerTicketPorIdSinResolucionAun");
        URI uri = new URI("/api/tickets/2");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"resolucion\":null"));
    }

    /* 10° Test: Resolver ticket con datos válidos */
    @Test
    void testResolverTicketConDatosValidos() throws Exception {
        System.out.println("Ejecutando testResolverTicketConDatosValidos");
        String json = """
                {
                  "nId_Admin": 2,
                  "sTextoRespuesta": "Tu constancia ya fue generada, puedes recogerla en mesa de partes"
                }
                """;
        URI uri = new URI("/api/tickets/3/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("\"sEstado\":\"RESUELTO\""));
    }

    /* 11° Test: Resolver ticket con ticket inexistente */
    @Test
    void testResolverTicketConTicketInexistente() throws Exception {
        System.out.println("Ejecutando testResolverTicketConTicketInexistente");
        String json = """
                {
                  "nId_Admin": 2,
                  "sTextoRespuesta": "Respuesta de prueba"
                }
                """;
        URI uri = new URI("/api/tickets/999/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    /* 12° Test: Resolver ticket con texto de respuesta vacío */
    @Test
    void testResolverTicketConTextoRespuestaVacio() throws Exception {
        System.out.println("Ejecutando testResolverTicketConTextoRespuestaVacio");
        String json = """
                {
                  "nId_Admin": 2,
                  "sTextoRespuesta": ""
                }
                """;
        URI uri = new URI("/api/tickets/1/respuesta");
        MockHttpServletRequestBuilder req = MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(req).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
