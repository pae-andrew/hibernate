package ru.learnUp.lesson23.hibernate.controller;

import org.springframework.web.bind.annotation.*;
import ru.learnUp.lesson23.hibernate.dao.entity.Client;
import ru.learnUp.lesson23.hibernate.dao.filters.ClientFilter;
import ru.learnUp.lesson23.hibernate.dao.services.ClientService;
import ru.learnUp.lesson23.hibernate.view.ClientView;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("rest/client")
public class ClientControllerRest {

    private final ClientService clientService;
    private final ClientView mapper;

    public ClientControllerRest(ClientService clientService, ClientView mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }

    // get clients
    @GetMapping
    public List<Client> getClients(
            @RequestParam(value = "fullName", required = false) String fullName
    ) {
        return clientService.getClientBy(new ClientFilter(fullName));
    }

    @GetMapping("/{clientId}")
    public ClientView getBook(@PathVariable("clientId") Long clientId) {
        return mapper.mapToView(clientService.getClientById(clientId));
    }

    // add client
    @PostMapping
    public ClientView createBook(@RequestBody ClientView body) {
        if (body.getId() != null) {
            throw new EntityExistsException(
                    String.format("Post with id = %s already exist", body.getId())
            );
        }
        Client client = mapper.mapFromView(body);
        Client createdClient = clientService.createClient(client);
        return mapper.mapToView(createdClient);
    }

    // update client
    @PutMapping("/{clientId}")
    public ClientView updateClient(
            @PathVariable("clientId") Long clientId,
            @RequestBody ClientView body
    ) {
        if (body.getId() == null) {
            throw new EntityNotFoundException("Try to found null entity");
        }
        if (!Objects.equals(clientId, body.getId())) {
            throw new RuntimeException("Entity has bad id");
        }

        Client client = clientService.getClientById(clientId);

        if (!client.getFullName().equals(body.getFullName())) {
            client.setFullName(body.getFullName());
        }

        if (!client.getBirthDate().equals(body.getBirthDate())) {
            client.setBirthDate(body.getBirthDate());
        }

        Client updated = clientService.update(client);

        return mapper.mapToView(updated);
    }

    // delete client
    @DeleteMapping("/{clientId}")
    public Boolean deleteBook(@PathVariable("clientId") Long id) {
        return clientService.delete(id);
    }
}
