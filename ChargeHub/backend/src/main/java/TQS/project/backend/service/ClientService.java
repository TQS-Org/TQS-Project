package TQS.project.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.ClientRepository;

@Service
public class ClientService {

  private ClientRepository clientRepository;

  @Autowired
  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Optional<Client> getClientByMail(String mail) {
    return clientRepository.findByMail(mail);
  }
}
