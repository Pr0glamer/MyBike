package nix.project.mybike;

import nix.project.mybike.controllers.BikesController;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.services.BikesService;
import nix.project.mybike.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BikeControllerTest {

    @Mock
    private BikesService bikesService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private BikesController bikesController;

    @Mock
    private Model model;

    private MockMvc mockMvc;

    @Mock
    private MultipartFile imageFile;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ModelAndView modelAndView;

    @BeforeEach
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(bikesController).build();
        byte[] fileContent = "test data".getBytes();
        when(imageFile.getBytes()).thenReturn(fileContent);
    }

    @Test
    public void testIndex() throws Exception {
        Page<Bike> bikes = new PageImpl<>(Collections.emptyList());
        when(bikesService.findWithPagination(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(bikes);

        mockMvc.perform(get("/bikes"))
                .andExpect(status().isOk())
                .andExpect(view().name("bikes/index"))
                .andExpect(model().attribute("bikes", hasSize(0)))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("totalItems", 0L))
                .andExpect(model().attribute("available", false))
                .andExpect(model().attribute("sortField", "year"))
                .andExpect(model().attribute("sortDir", "asc"))
                .andExpect(model().attribute("search", ""))
                .andExpect(model().attribute("reverseSortDir", "desc"));

        verify(bikesService).findWithPagination(1, 3, "year", "asc");
    }

    @Test
    public void testShow() throws Exception {
        int id = 1;
        Bike bike = new Bike();
        bike.setId(id);
        when(bikesService.findOne(id)).thenReturn(bike);

        mockMvc.perform(get("/bikes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("bikes/show"))
                .andExpect(model().attribute("bike", bike));

        verify(bikesService).findOne(id);
    }

    @Test
    public void testNewBike() throws Exception {
        mockMvc.perform(get("/bikes/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("bikes/new"))
                .andExpect(model().attributeExists("bike"));
    }

    @Test
    public void testCreate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/bikes")
                        .file("image", "test.png".getBytes())
                        .param("title", "title")
                        .param("producer", "Producer 1")
                        .param("year", "1999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/bikes"));

        verify(bikesService).save(any(Bike.class));
    }

    @Test
    public void testCreateValidationError() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/bikes")
                        .file("image", "test.png".getBytes())
                        .param("title", "")
                        .param("producer", "Producer 1")
                        .param("year", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEdit() throws Exception {
        int id = 1;
        Bike bike = new Bike();
        bike.setId(id);
        when(bikesService.findOne(id)).thenReturn(bike);

        mockMvc.perform(get("/bikes/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("bikes/edit"))
                .andExpect(model().attribute("bike", bike));

        verify(bikesService).findOne(id);
    }

    @Test
    public void testDeleteBike() throws Exception {
        int id = 1;
        doNothing().when(bikesService).delete(id);
        mockMvc.perform(delete("/bikes/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/bikes"));
        verify(bikesService, times(1)).delete(id);
    }

    @Test
    public void testRelease() {
        int id = 1;
        Bike bike = new Bike();
        bike.setId(id);

        when(bikesService.release(id)).thenReturn(bike);
        String result = bikesController.release(id);

        verify(bikesService, times(1)).release(id);

        assertEquals("redirect:/bikes/1", result);
    }

    @Test
    public void testAssign() {
        int id = 1;
        Bike bike = new Bike();
        bike.setId(id);

        Client client = new Client();

        when(bikesService.assign(id, client, 5)).thenReturn(bike);
        String result = bikesController.assign(id, client, "5");
        verify(bikesService, times(1)).assign(id, client, 5);


        assertEquals("redirect:/bikes/1", result);
    }

    @Test
    public void testSearchPage() {
        String result = bikesController.searchPage();

        assertEquals("bikes/search", result);
    }

    @Test
    public void testMakeSearch() {
        String query = "test";

        List<Bike> bikes = new ArrayList<>();
        bikes.add(new Bike());

        PageImpl<Bike> page = new PageImpl<Bike>(bikes, PageRequest.of(0, 3), 1);

        when(bikesService.searchByTitle(query, false)).thenReturn(bikes);

        String result = bikesController.makeSearch(model, query);

        verify(bikesService, times(1)).searchByTitle(query, false);
        verify(model, times(1)).addAttribute("bikes", page.getContent());

        assertEquals("bikes/search", result);
    }

    @Test
    void uploadPicture_withValidPicture_uploadSuccessful() throws Exception {

        int bikeId = 1;
        byte[] pictureBytes = {0x10, 0x20, 0x30};
        MultipartFile picture = new MockMultipartFile("picture", "test.jpg", "image/jpeg", pictureBytes);
        Bike bike = new Bike();
        bike.setId(bikeId);
        when(bikesService.findById(bikeId)).thenReturn(bike);

        String result = new BikesController(bikesService, null, null).uploadPicture(bikeId, picture, model);

        verify(bikesService).save(bike);
        assertThat(result).isEqualTo("redirect:/bikes");
    }

    @Test
    void uploadPicture_withEmptyPicture_returnsError() throws Exception {

        int bikeId = 1;
        MultipartFile picture = new MockMultipartFile("picture", new byte[0]);
        Bike bike = new Bike();
        when(bikesService.findById(bikeId)).thenReturn(bike);


        String result = new BikesController(bikesService, null, null).uploadPicture(bikeId, picture, model);

        verify(bikesService, times(1)).findById(bikeId);
        assertThat(result).isEqualTo("redirect:/bikes");

    }

    @Test
    void getBikeImage_withValidBike_returnsImageBytes() throws Exception {

        int bikeId = 1;
        byte[] pictureBytes = {0x10, 0x20, 0x30};
        Bike bike = new Bike();
        bike.setId(bikeId);
        bike.setPicture(pictureBytes);
        when(bikesService.findById(bikeId)).thenReturn(bike);

        ResponseEntity<byte[]> result = new BikesController(bikesService, null, null).getBikeImage(bikeId);

        assertThat(result.getBody()).isEqualTo(pictureBytes);
    }

    @Test
    void getBikeImage_withInvalidBike_returnsNull() throws Exception {

        int bikeId = 1;
        when(bikesService.findById(bikeId)).thenReturn(null);

        ResponseEntity<byte[]> result = new BikesController(bikesService, null, null).getBikeImage(bikeId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}


