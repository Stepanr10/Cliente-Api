package org.ClientApi.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.ClientApi.domain.models.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {

    public List<Client> findByCountry(String pais) {
        return find("pais", pais.toUpperCase()).list();
    }

    public Client findById(Long id) {
        return find("id", id).firstResult();
    }

}
