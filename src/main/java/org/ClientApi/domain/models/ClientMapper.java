package org.ClientApi.domain.models;

import org.ClientApi.DTOs.*;

public class ClientMapper {

    public static ClientResponseDTO toResponse(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.id = client.id;
        dto.primerNombre = client.primerNombre;
        dto.segundoNombre = client.segundoNombre;
        dto.primerApellido = client.primerApellido;
        dto.segundoApellido = client.segundoApellido;
        dto.correo = client.correo;
        dto.direccion = client.direccion;
        dto.telefono = client.telefono;
        dto.pais = client.pais.toUpperCase();
        dto.gentilicio = client.gentilicio;
        return dto;
    }

    public static Client ToEntity(ClientCreateDTO dto) {
        Client client = new Client();
        client.primerNombre = dto.primerNombre;
        client.segundoNombre = dto.segundoNombre;
        client.primerApellido = dto.primerApellido;
        client.segundoApellido = dto.segundoApellido;
        client.correo = dto.correo;
        client.direccion = dto.direccion;
        client.telefono = dto.telefono;
        client.pais = dto.pais.toUpperCase();
        return client;
    }

    public static void updateEntity(Client client, ClientUpdateDTO dto) {
        client.correo = dto.correo;
        client.direccion = dto.direccion;
        client.telefono = dto.telefono;
        client.pais = dto.pais.toUpperCase();
    }
}