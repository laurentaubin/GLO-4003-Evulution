package ca.ulaval.glo4003.ws.api.contact;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.contact.dto.ContactDto;
import ca.ulaval.glo4003.ws.domain.contact.ContactService;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContactResourceImplTest {

  @Mock private ContactService contactService;
  @Mock private ContactDto contactDto;

  private ContactResource contactResource;

  @BeforeEach
  public void setUp() throws Exception {
    contactResource = new ContactResourceImpl(contactService);
  }

  @Test
  public void whenFindAllContacts_thenDelegateToService() {
    // given
    BDDMockito.given(contactService.findAllContacts()).willReturn(Lists.newArrayList(contactDto));

    // when
    List<ContactDto> contactDtos = contactResource.getContacts();

    // then
    assertThat(contactDtos).contains(contactDto);
    Mockito.verify(contactService).findAllContacts();
  }
}
