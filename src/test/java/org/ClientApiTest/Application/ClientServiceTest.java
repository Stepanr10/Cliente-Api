package org.ClientApiTest.Application;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.ClientApi.DTOs.ClientCreateDTO;
import org.ClientApi.DTOs.ClientResponseDTO;
import org.ClientApi.DTOs.ClientUpdateDTO;
import org.ClientApi.application.services.ClientService;
import org.ClientApi.domain.models.Client;
import org.ClientApi.infrastructure.external.RestCountryService;
import org.ClientApi.infrastructure.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ClientServiceTest {

    @Inject
    ClientService clientService;

    @InjectMock
    ClientRepository repository;

    @InjectMock
    RestCountryService countryService;

    Client client;

    @BeforeEach
    public void setup() {
        client = new Client();
        client.id = 1L;
        client.primerNombre = "Juan";
        client.primerApellido = "Pérez";
        client.correo = "juan@example.com";
        client.telefono = "8091234567";
        client.direccion = "Calle 123";
        client.pais = "DO";
        client.gentilicio = "Dominicano";
    }

    @Test
    void testCreateClient() {
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.primerNombre = "Juan";
        dto.primerApellido = "Pérez";
        dto.correo = "juan@example.com";
        dto.telefono = "8091234567";
        dto.direccion = "Calle 123";
        dto.pais = "DO";

        when(countryService.getNationality("DO")).thenReturn("Dominicano");

        Client result = clientService.create(dto);

        assertNotNull(result);
        assertEquals("Dominicano", result.gentilicio);
        verify(repository).persist(any(Client.class));
    }

    @Test
    void testFindAllReturnsList() {
        when(repository.listAll()).thenReturn(List.of(client));

        List<Client> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).primerNombre);
    }

    @Test
    void testFindByIdSuccess() {
        when(repository.findById(1L)).thenReturn(client);

        Client found = clientService.findById(1L);

        assertNotNull(found);
        assertEquals("Juan", found.primerNombre);
    }

    @Test
    void testFindByIdReturnsNull() {
        when(repository.findById(99L)).thenReturn(null);

        Client found = clientService.findById(99L);

        assertNull(found);
    }

    @Test
    void testFindByCountry() {
        when(repository.findByCountry("DO")).thenReturn(List.of(client));

        List<Client> list = clientService.findByCountry("DO");

        assertEquals(1, list.size());
        assertEquals("DO", list.get(0).pais);
    }

    @Test
    void testUpdateSuccess() {
        when(repository.findById(1L)).thenReturn(client);
        when(countryService.getNationality("US")).thenReturn("Estadounidense");

        ClientUpdateDTO update = new ClientUpdateDTO();
        update.correo = "nuevo@example.com";
        update.direccion = "Nueva calle";
        update.telefono = "8290000000";
        update.pais = "US";

        ClientResponseDTO result = clientService.update(1L, update);

        assertEquals("nuevo@example.com", result.correo);
        assertEquals("Estadounidense", result.gentilicio);
        verify(repository).persist(client);
    }

    @Test
    void testUpdateThrowsIfNotFound() {
        when(repository.findById(999L)).thenReturn(null);

        ClientUpdateDTO update = new ClientUpdateDTO();
        update.correo = "a@a.com";
        update.pais = "DO";
        update.telefono = "1234567";
        update.direccion = "Algo";

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            clientService.update(999L, update);
        });

        assertTrue(ex.getMessage().contains("No existe un cliente"));
    }

    @Test
    void testDeleteCallsRepository() {
        clientService.delete(1L);
        verify(repository).deleteById(1L);
    }
}
