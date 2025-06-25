package org.ClientApi.domain.models;

import org.ClientApi.DTOs.*;

public class ClientValidation {

    public static void validate(ClientCreateDTO dto) {
        if (dto.primerNombre == null || dto.primerNombre.isBlank())
            throw new IllegalArgumentException("El primer nombre es obligatorio");

        if (dto.primerApellido == null || dto.primerApellido.isBlank())
            throw new IllegalArgumentException("El primer apellido es obligatorio");

        if (dto.correo == null || !dto.correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Correo electrónico inválido");

        if (dto.direccion == null || dto.direccion.isBlank())
            throw new IllegalArgumentException("La dirección es obligatoria");

        if (dto.telefono == null || !dto.telefono.matches("^[0-9\\-+() ]{7,20}$"))
            throw new IllegalArgumentException("Teléfono inválido");

        if (dto.pais == null || !dto.pais.matches("^[a-zA-Z]{2}$"))
            throw new IllegalArgumentException("Código de país inválido (debe ser ISO 3166, ej: DO, US)");
    }
}
