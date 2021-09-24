package ca.ulaval.glo4003.ws.api.customer;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginCustomerDto;
import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public interface CustomerResource {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/customers")
  Response registerCustomer(RegisterCustomerDto registerCustomerDto);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/login")
  Response login(LoginCustomerDto loginCustomerDto);
}
