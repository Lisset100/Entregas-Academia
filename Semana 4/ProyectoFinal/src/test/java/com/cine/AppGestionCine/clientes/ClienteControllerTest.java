package com.cine.AppGestionCine.clientes;

import com.cine.AppGestionCine.clientes.dto.ActualizarClienteRequest;
import com.cine.AppGestionCine.clientes.dto.CrearClienteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

// Imports específicos de Mockito
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Imports de Spring MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Imports específicos de Hamcrest
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false"
})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    @DisplayName("POST /api/clientes debe crear cliente correctamente")
    void cuandoCrearCliente_datosValidos_debeCrear() throws Exception {
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setEmail("juan@example.com");
        request.setTelefono("5551234567");

        Cliente clienteCreado = new Cliente();
        clienteCreado.setId(1L);
        clienteCreado.setNombre("Juan");
        clienteCreado.setApellido("Pérez");
        clienteCreado.setEmail("juan@example.com");
        clienteCreado.setEstado(EstadoCliente.ACTIVO);

        when(clienteService.crearCliente(any(CrearClienteRequest.class)))
                .thenReturn(clienteCreado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.email", is("juan@example.com")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(clienteService, times(1)).crearCliente(any(CrearClienteRequest.class));
    }

    @Test
    @DisplayName("GET /api/clientes debe retornar todos los clientes")
    void cuandoGetAllClientes_debeRetornarLista() throws Exception {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombre("Juan");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("María");

        when(clienteService.getAllClientes())
                .thenReturn(Arrays.asList(cliente1, cliente2));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(clienteService, times(1)).getAllClientes();
    }

    @Test
    @DisplayName("GET /api/clientes/{id} debe retornar cliente específico")
    void cuandoGetClienteById_existe_debeRetornar() throws Exception {
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNombre("Juan");
        cliente.setEmail("juan@example.com");

        when(clienteService.getClienteById(clienteId))
                .thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")));

        verify(clienteService, times(1)).getClienteById(clienteId);
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} debe actualizar cliente")
    void cuandoActualizarCliente_debeActualizar() throws Exception {
        Long clienteId = 1L;

        ActualizarClienteRequest request = new ActualizarClienteRequest();
        request.setNombre("Juan Carlos");

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(clienteId);
        clienteActualizado.setNombre("Juan Carlos");

        when(clienteService.actualizarCliente(anyLong(), any(ActualizarClienteRequest.class)))
                .thenReturn(clienteActualizado);

        mockMvc.perform(put("/api/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Carlos")));

        verify(clienteService, times(1))
                .actualizarCliente(eq(clienteId), any(ActualizarClienteRequest.class));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id}/activar debe activar cliente")
    void cuandoActivarCliente_debeActivar() throws Exception {
        Long clienteId = 1L;

        doNothing().when(clienteService).activarCliente(clienteId);

        mockMvc.perform(put("/api/clientes/{id}/activar", clienteId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("activado")));

        verify(clienteService, times(1)).activarCliente(clienteId);
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} debe eliminar cliente")
    void cuandoEliminarCliente_debeEliminar() throws Exception {
        Long clienteId = 1L;

        doNothing().when(clienteService).eliminarCliente(clienteId);

        mockMvc.perform(delete("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("eliminado")));

        verify(clienteService, times(1)).eliminarCliente(clienteId);
    }
}