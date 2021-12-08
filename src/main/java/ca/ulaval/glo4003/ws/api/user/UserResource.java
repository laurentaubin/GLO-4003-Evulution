package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.request.LoginUserRequest;
import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public interface UserResource {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/customers")
  Response registerUser(RegisterUserRequest registerUserRequest);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/login")
  Response login(LoginUserRequest loginUserRequest);
}
