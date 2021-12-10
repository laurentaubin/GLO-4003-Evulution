package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.OwnershipDomainService;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;

import java.util.List;

public class UserService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;
  private final SessionAdministrator sessionAdministrator;
  private final UserAssembler userAssembler;
  private final SessionDtoAssembler sessionDtoAssembler;
  private final OwnershipDomainService ownershipDomainService;

  public UserService() {
    this(
            serviceLocator.resolve(UserRepository.class),
        serviceLocator.resolve(SessionAdministrator.class),
        serviceLocator.resolve(UserAssembler.class),
        new SessionDtoAssembler(),
        serviceLocator.resolve(OwnershipDomainService.class));
  }

  public UserService(
      UserRepository userRepository,
      SessionAdministrator sessionAdministrator,
      UserAssembler userAssembler,
      SessionDtoAssembler sessionDtoAssembler,
      OwnershipDomainService ownershipDomainService) {
    this.userRepository = userRepository;
    this.sessionAdministrator = sessionAdministrator;
    this.userAssembler = userAssembler;
    this.sessionDtoAssembler = sessionDtoAssembler;
    this.ownershipDomainService = ownershipDomainService;
  }

  public void registerUser(RegisterUserDto registerUserDto) {
    User user = userAssembler.assemble(registerUserDto);
    sessionAdministrator.registerUser(user, registerUserDto.getPassword());
  }

  public SessionDto login(String email, String password) {
    return sessionDtoAssembler.assemble(sessionAdministrator.login(email, password));
  }

  public void isAllowed(TokenDto tokenDto, List<Role> privilegedRoles) {
    sessionAdministrator.validatePermissions(tokenDto, privilegedRoles);
  }

  public void mapDeliveryIdToTransactionId(TokenDto tokenDto, TransactionId transactionId, DeliveryId deliveryId) {
    Session userSession = sessionAdministrator.retrieveSession(tokenDto);
    mapDeliveryIdToTransactionId(userSession, transactionId, deliveryId);
  }

  public void validateTransactionOwnership(TokenDto tokenDto, TransactionId transactionId, List<Role> privilegedRoles) {
    isAllowed(tokenDto, privilegedRoles);
    Session session = sessionAdministrator.retrieveSession(tokenDto);
    User user = userRepository.findUser(session.getEmail());
    ownershipDomainService.validateTransactionOwnership(user, transactionId);
  }

  public void validateDeliveryOwnership(TokenDto tokenDto, DeliveryId deliveryId, List<Role> privilegedRoles) {
    isAllowed(tokenDto, privilegedRoles);
    Session session = sessionAdministrator.retrieveSession(tokenDto);
    User user = userRepository.findUser(session.getEmail());
    ownershipDomainService.validateDeliveryOwnership(user, deliveryId);
  }

  public TransactionId getTransactionIdFromDeliveryId(TokenDto tokenDto, DeliveryId deliveryId) {
    Session session = sessionAdministrator.retrieveSession(tokenDto);
    User user = userRepository.findUser(session.getEmail());
    return user.getTransactionIdFromDeliveryId(deliveryId);

  }

  private void mapDeliveryIdToTransactionId(
          Session session, TransactionId transactionId, DeliveryId deliveryId) {
    User user = userRepository.findUser(session.getEmail());
    user.addTransactionDelivery(transactionId, deliveryId);
    userRepository.update(user);
  }
}
