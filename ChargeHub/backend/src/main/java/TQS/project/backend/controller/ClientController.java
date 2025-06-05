package TQS.project.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TQS.project.backend.entity.Client;
import TQS.project.backend.service.ClientService;

@RestController
@RequestMapping("/api/client")
public class ClientController {

  private final ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping("/{mail}")
  public ResponseEntity<Client> getClientByMail(@PathVariable String mail) {
    Optional<Client> client = clientService.getClientByMail(mail);
    return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
