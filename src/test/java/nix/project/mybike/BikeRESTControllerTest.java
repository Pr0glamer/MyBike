package nix.project.mybike;

import com.fasterxml.jackson.databind.ObjectMapper;
import nix.project.mybike.controllers.rest.BikeRESTController;
import nix.project.mybike.models.Bike;
import nix.project.mybike.services.BikesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BikeRESTControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BikesService bikeService;

    @InjectMocks
    private BikeRESTController bikeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bikeController).build();
    }

    @Test
    public void getAllBikes_ShouldReturnListOfBikes() throws Exception {
        List<Bike> bikes = Arrays.asList(new Bike(), new Bike());
        when(bikeService.findAll(true)).thenReturn(bikes);

        mockMvc.perform(get("/rest/bikes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(bikeService, times(1)).findAll(true);
        verifyNoMoreInteractions(bikeService);
    }

    @Test
    public void getBikeById_ShouldReturnBike() throws Exception {
        Bike bike = new Bike();
        when(bikeService.findById(1)).thenReturn(bike);

        mockMvc.perform(get("/rest/bikes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bikeService, times(1)).findById(1);
        verifyNoMoreInteractions(bikeService);
    }

    @Test
    public void getBikeById_ShouldReturnNotFound() throws Exception {
        when(bikeService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/rest/bikes/1"))
                .andExpect(status().isNotFound());

        verify(bikeService, times(1)).findById(1);
        verifyNoMoreInteractions(bikeService);
    }
    @Test
    public void addBike() throws Exception {
        Bike bike = new Bike();
        bike.setTitle("Test Bike");
        bike.setProducer("Test Producer");
        bike.setYear(2000);
        bike.setPrice(10);

        mockMvc.perform(post("/rest/bikes")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(bike)))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateBike() throws Exception {

        Bike bike = new Bike();
        bike.setId(1);
        bike.setTitle("Test Bike");
        bike.setProducer("Test Producer");
        bike.setYear(2000);
        bike.setPrice(10);
        when(bikeService.findById(1)).thenReturn(bike);
        mockMvc.perform(put("/rest/bikes/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(bike)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteBike() throws Exception {
        Bike bike = new Bike();
        bike.setId(1);
        bike.setTitle("Test Bike");
        bike.setProducer("Test Producer");
        bike.setYear(2000);
        bike.setPrice(10);
        when(bikeService.findById(1)).thenReturn(bike);
        mockMvc.perform(delete("/rest/bikes/1"))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}