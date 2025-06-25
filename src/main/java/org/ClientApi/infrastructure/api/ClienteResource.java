package org.ClientApi.infrastructure.api;

import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ClientApi.DTOs.*;
import org.ClientApi.domain.models.Client;
import org.ClientApi.application.services.ClientService;

import java.net.URI;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClientService service;

    @POST
    /*Creando un cliente nuevo*/
    public Response create(ClientCreateDTO client) {
        try {
            if (client == null) {
                return badRequest("El cuerpo de la solicitud está vacío");
            }
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
    /*Obtener Todos los clientes*/
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
    /*Clientes según el país*/
    @Path("/pais/{codigo}")
    public Response getByCountry(@PathParam("codigo") String codigo) {
        try {
            List<Client> result = service.findByCountry(codigo);
            if (result == null || result.isEmpty()) {
                return Response.noContent().build();
            }
            return Response.ok(result).build();
        } catch (Throwable t) {
            return serverError(t.getMessage());
        }
    }

    @GET
    /*Clientes por Identificador.*/
    @Path("/{id}")
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
    /*Actualizando clientes (campos permitidos)*/
    @Path("/{id}")
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
    /*Eliminando un cliente.*/
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Client found = service.findById(id);
            if (found == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(new BaseEntity.ErrorDTO("No se encontró un cliente para eliminar con este id: " + id)).build();
            }
            service.delete(id);
            return Response.noContent().build(); // 204
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
