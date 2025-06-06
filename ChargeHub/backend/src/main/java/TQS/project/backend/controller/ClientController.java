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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/client")
public class ClientController {

  private final ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @Operation(summary = "Retrieve client information by email.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client found and returned.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Client.class))),
        @ApiResponse(responseCode = "404", description = "Client not found.", content = @Content)
      })
  @GetMapping("/{mail}")
  public ResponseEntity<Client> getClientByMail(@PathVariable String mail) {
    Optional<Client> client = clientService.getClientByMail(mail);
    return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
