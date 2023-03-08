package nix.project.mybike;

import nix.project.mybike.controllers.ClientController;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import nix.project.mybike.services.DebtService;
import nix.project.mybike.util.ClientValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ClientControllerTest {
    @InjectMocks
    private ClientController clientController;

    @Mock
    private ClientService clientService;

    @Mock
    private DebtService debtService;

    @Mock
    private ClientValidator clientValidator;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Test
    public void testIndex() {
        int page = 1;
        int clientsPerPage = 3;
        String sortField = "yearOfBirth";
        String sortDirection = "asc";
        Page<Client> clientPage = new PageImpl<>(Collections.emptyList());
        when(clientService.findWithPagination(page, clientsPerPage, sortField, sortDirection))
                .thenReturn(clientPage);

        String viewName = clientController.index(model, page, clientsPerPage, sortField, sortDirection);

        verify(clientService).findWithPagination(page, clientsPerPage, sortField, sortDirection);
        verify(model).addAttribute(eq("currentPage"), eq(page));
        verify(model).addAttribute(eq("totalPages"), eq(clientPage.getTotalPages()));
        verify(model).addAttribute(eq("totalItems"), eq(clientPage.getTotalElements()));
        verify(model).addAttribute(eq("clients"), eq(Collections.emptyList()));
        verify(model).addAttribute(eq("sortField"), eq(sortField));
        verify(model).addAttribute(eq("sortDir"), eq(sortDirection));
        verify(model).addAttribute(eq("reverseSortDir"), eq("desc"));

        assertEquals("clients/index", viewName);

    }

    @Test
    public void testShow() {
        int id = 1;
        Client client = new Client();
        List<Bike> bikes = Collections.emptyList();
        Map<Bike, Integer> debts = Collections.emptyMap();
        when(clientService.findOne(id)).thenReturn(client);
        when(clientService.getBikesByClientId(id)).thenReturn(bikes);
        when(clientService.getDebtByClientId(id)).thenReturn(debts);

        String viewName = clientController.show(id, model);

        verify(clientService).findOne(id);
        verify(clientService).getBikesByClientId(id);
        verify(clientService).getDebtByClientId(id);
        verify(model).addAttribute(eq("client"), eq(client));
        verify(model).addAttribute(eq("bikes"), eq(bikes));
        verify(model).addAttribute(eq("debts"), eq(debts));

        assertEquals("clients/show", viewName);
    }

    @Test
    public void testNewClient() {
        String viewName = clientController.newClient(new Client());

        assertEquals("clients/new", viewName);
    }

    @Test
    public void testCreate() {
        Client client = new Client();
        BindingResult bindingResult = new BeanPropertyBindingResult(client, "client");
        when(clientService.save(client)).thenReturn(client);

        String viewName = clientController.create(client, bindingResult);

        verify(clientValidator).validate(client, bindingResult);
        verify(clientService).save(client);

        assertEquals("redirect:/clients", viewName);
    }

    @Test
    public void testEdit() {
        int id = 1;
        Client client = new Client();
        when(clientService.findOne(id)).thenReturn(client);

        String viewName = clientController.edit(model, id);

        verify(clientService).findOne(id);
        verify(model).addAttribute(eq("client"), eq(client));

        assertEquals("clients/edit", viewName);
    }

    @Test
    public void deleteClientTest() throws Exception {
        int clientId = 1;
        Client client = new Client();
        doNothing().when(clientService).delete(clientId);
        String viewName = clientController.delete(clientId);
        verify(clientService, times(1)).delete(clientId);
        assertEquals("redirect:/clients", viewName);
    }

    @Test
    public void updateClientTest() throws Exception {
        int clientId = 1;
        Client client = new Client();
        doNothing().when(clientService).update(clientId, client);
        String viewName = clientController.update(client, bindingResult, clientId);
        verify(clientService, times(1)).update(clientId, client);
        assertEquals("redirect:/clients", viewName);


    }

}
