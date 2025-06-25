package org.ClientApi.infrastructure.api;

import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ClientApi.DTOs.*;
import org.ClientApi.domain.models.Client;
import org.ClientApi.application.services.ClientService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/clientes")
@Tag(name = "Clientes", description = "Con este Api se busca gestionar un CRUD de clientes utilizando una base de datos SQLlite. ")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClientService service;

    /* Se agregan anotaciones y detalles a cada operación para mostrar claridad
     *  y al ejecutar en swagger se visualice el objetivo de cada endpoint.*/

    @POST
    @Operation(summary = "Crear un cliente.")
    public Response create(ClientCreateDTO client) {
        try {
            Client cliente = service.create(client);
            URI uri = URI.create("/clientes/" + cliente.id);
            return Response.created(uri).entity(cliente).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (PersistenceException e) {
            return handlePersistenceError(e);
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    @GET
    @Operation(summary = "Obtener todos los clientes.")
    public Response getAll() {
        try {
            List<Client> result = service.findAll();
            if (result == null || result.isEmpty()) {
                return Response.noContent().build();
            }
            return Response.ok(result).build();
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }


    @GET
    @Path("/pais/{codigo}")
    @Operation(summary = "Obtener un cliente según el país.")
    public Response getByCountry(@PathParam("codigo") String codigo) {
        try {
            List<Client> result = service.findByCountry(codigo);
            if (result == null || result.isEmpty()) {
                return Response.noContent().build(); // 204
            }
            return Response.ok(service.findByCountry(codigo)).build();
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un cliente según su ID en la base de datos.")
    public Response getById(@PathParam("id") Long id) {
        try {
            Client found = service.findById(id);
            if (found == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(found).build();
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar datos de un cliente filtrandolo por su ID. (telefono, correo, dirección, pais")
    public Response update(@PathParam("id") Long id, ClientUpdateDTO update) {
        try {
            ClientResponseDTO updated = service.update(id, update);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (PersistenceException e) {
            return handlePersistenceError(e);
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un cliente buscandolo por su ID.")
    public Response delete(@PathParam("id") Long id) {
        try {
            Client found = service.findById(id);
            if (found == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(new BaseEntity.ErrorDTO("No se encontró un cliente para eliminar con este id: " + id)).build();
            }
            service.delete(id);
            return Response.ok("Cliente eliminado").build();
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new BaseEntity.ErrorDTO("Validación: " + message)).build();
    }

    private Response serverError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new BaseEntity.ErrorDTO("Error interno: " + message)).build();
    }

    private Response handlePersistenceError(PersistenceException e) {
        if (e.getCause() != null && e.getCause().getMessage().contains("UNIQUE constraint failed")) {
            return Response.status(Response.Status.CONFLICT).entity(new BaseEntity.ErrorDTO("El correo ya está registrado")).build();
        }
        return serverError("Error de base de datos: " + e.getMessage());
    }
}
