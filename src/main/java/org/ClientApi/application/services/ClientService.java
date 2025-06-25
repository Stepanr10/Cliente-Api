package org.ClientApi.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ClientApi.DTOs.ClientCreateDTO;
import org.ClientApi.DTOs.ClientResponseDTO;
import org.ClientApi.DTOs.ClientUpdateDTO;
import org.ClientApi.domain.models.Client;
import org.ClientApi.domain.models.ClientMapper;
import org.ClientApi.domain.models.ClientValidation;
import org.ClientApi.infrastructure.external.RestCountryService;
import org.ClientApi.infrastructure.repository.ClientRepository;

import java.util.List;

@ApplicationScoped
public class ClientService {

    @Inject
    ClientRepository repository;

    @Inject
    RestCountryService countryService;

    /*Para la creación de clientes se utilizó un DTO que muestra todos los campos de la clase Client excepto el
     * id y genticilio ya que estos son autogenerados a través del codigo.*/
    @Transactional
    public Client create(ClientCreateDTO clientCreateDTO) {
        ClientValidation.validate(clientCreateDTO);
        Client client = ClientMapper.ToEntity(clientCreateDTO);
        client.gentilicio = countryService.getNationality(client.pais);
        repository.persist(client);
        return client;
    }

    public List<Client> findAll() {
        return repository.listAll();
    }

    public Client findById(Long id) {
        return repository.findById(id);
    }

    public List<Client> findByCountry(String pais) {
        return repository.findByCountry(pais);
    }

    //Se utiliza ClientUpdateDTO como entrada para mostrar solo los campos que se quieren actualizar.
    @Transactional
    public ClientResponseDTO update(Long id, ClientUpdateDTO update) {
        Client existing = findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("No existe un cliente con este id: " + id);
        }
        existing.correo = update.correo;
        existing.telefono = update.telefono;
        existing.direccion = update.direccion;
        existing.pais = update.pais;
        existing.gentilicio = countryService.getNationality(update.pais);

        repository.persist(existing);

        return ClientMapper.toResponse(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}