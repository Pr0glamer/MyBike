package nix.project.mybike;

import com.fasterxml.jackson.databind.ObjectMapper;
import nix.project.mybike.controllers.rest.ClientRESTController;
import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ClientRESTControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientRESTController clientController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    public void getClient_withValidId_returnsClient() throws Exception {

        int clientId = 123;
        Client client = new Client(clientId, "Faust", 1991, 380093339099L, null, null);
        given(clientService.findOne(clientId)).willReturn(client);
        ObjectMapper objectMapper = new ObjectMapper();


        MvcResult result = mockMvc.perform(get("/rest/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        Client savedClient = objectMapper.readValue(responseString, Client.class);


        assertEquals("Faust", savedClient.getFullName());
        assertEquals(1991, savedClient.getYearOfBirth());
        assertEquals(380093339099L, savedClient.getTelephone());
    }

    @Test
    public void getClient_withNonexistentId_returnsNotFound() throws Exception {

        int nonExistentClientId = 123;
        given(clientService.findOne(nonExistentClientId)).willReturn(null);


        mockMvc.perform(get("/rest/clients/{id}", nonExistentClientId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createClient_withValidRequest_returnsCreatedClient() throws Exception {

        Client newClient = new Client(123, "Faust", 1991, 380093339099L, null, null);
        given(clientService.save(any(Client.class))).willReturn(newClient);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newClient);

        MvcResult result = mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        Client savedClient = objectMapper.readValue(responseString, Client.class);


        assertEquals("Faust", savedClient.getFullName());
        assertEquals(1991, savedClient.getYearOfBirth());
        assertEquals(380093339099L, savedClient.getTelephone());
    }

    @Test
    public void createClient_withInvalidRequest_returnsBadRequest() throws Exception {

        Client invalidClient = new Client(-1, "", 0, 0L, null, null);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(invalidClient);

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteClient_withValidId_deletesClient() throws Exception {

        int clientId = 123;
        Client newClient = new Client(clientId, "Faust", 1991, 380093339099L, null, null);
        given(clientService.findOne(clientId)).willReturn(newClient);


        mockMvc.perform(delete("/rest/clients/{id}", clientId))
                .andExpect(status().isNoContent());


        verify(clientService, times(1)).delete(clientId);
    }

    @Test
    public void deleteClient_withNonexistentId_returnsNotFound() throws Exception {

        int nonExistentClientId = 123;
        given(clientService.findOne(nonExistentClientId)).willReturn(null);

        mockMvc.perform(delete("/rest/clients/{id}", nonExistentClientId))
                .andExpect(status().isNotFound());
    }

}