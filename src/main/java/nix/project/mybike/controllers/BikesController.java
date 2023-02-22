package nix.project.mybike.controllers;


import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.services.BikesService;
import nix.project.mybike.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Controller
@RequestMapping("/bikes")
public class BikesController {

    private final BikesService bikesService;
    private final ClientService clientService;

    @Autowired
    public BikesController(BikesService bikesService, ClientService clientService) {
        this.bikesService = bikesService;
        this.clientService = clientService;
    }


    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "bikes_per_page", defaultValue = "3") Integer bikesPerPage,
                        @RequestParam(value = "sortField", defaultValue = "year") String sortField,
                        @RequestParam(value = "sortDir", defaultValue = "asc") String sortDirection,
                        @RequestParam(value = "search", defaultValue = "") String searchTitle,
                        @RequestParam(required = false) Boolean available) {

        if(available == null) {
            available = false;
        }

        Page<Bike> bikes = null;

        if(!searchTitle.equals("")) {
            if (available) {
                bikes = bikesService.findWithPaginationWithTitleAndAvailability(page, bikesPerPage, sortField, sortDirection, searchTitle);
            } else {
                bikes = bikesService.findWithPaginationWithTitle(page, bikesPerPage, sortField, sortDirection, searchTitle);
            }
        } else if (available) {
            bikes = bikesService.findWithPaginationWithAvailability(page, bikesPerPage, sortField, sortDirection);

        } else {
            bikes = bikesService.findWithPagination(page, bikesPerPage, sortField, sortDirection);
        }

        List<Bike> bikesContent = bikes.getContent();
        bikesContent.stream().forEach(Bike::checkExpired);

        model.addAttribute("bikes",           bikesContent);
        model.addAttribute("currentPage",    page);
        model.addAttribute("totalPages",     bikes.getTotalPages());
        model.addAttribute("totalItems",     bikes.getTotalElements());
        model.addAttribute("available",      available);
        model.addAttribute("sortField",      sortField);
        model.addAttribute("sortDir",        sortDirection);
        model.addAttribute("search",         searchTitle);
        model.addAttribute("reverseSortDir", sortDirection.equals("asc") ? "desc" : "asc");

        return "bikes/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("client") Client client) {
        model.addAttribute("bike", bikesService.findOne(id));

        Client bikeOwner = bikesService.getOwner(id);

        if (bikeOwner != null)
            model.addAttribute("owner", bikeOwner);
        else
            model.addAttribute("clients", clientService.findAll());

        return "bikes/show";
    }

    @GetMapping("/new")
    public String newBike(@ModelAttribute("bike") Bike Bike) {
        return "bikes/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("bike") @Valid Bike bike,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "bikes/new";

        bikesService.save(bike);
        return "redirect:/bikes";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("bike", bikesService.findOne(id));
        return "bikes/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("bike") @Valid Bike bike, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "bikes/edit";

        bikesService.update(id, bike);
        return "redirect:/bikes";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bikesService.delete(id);
        return "redirect:/bikes";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bikesService.release(id);
        return "redirect:/bikes/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("client") Client selectedClient, @RequestParam String hours) {
        bikesService.assign(id, selectedClient, Integer.parseInt(hours));
        return "redirect:/bikes/" + id;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "bikes/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("bikes", bikesService.searchByTitle(query, false));
        return "bikes/search";
    }


    @PostMapping("/{id}/upload-picture")
    public String uploadPicture(@PathVariable int id, @RequestParam("picture") MultipartFile picture, Model model) {
        Bike bike = bikesService.findById(id);
        if (picture.isEmpty()) {
            model.addAttribute("error", "Please select a picture.");
            return "redirect:/bikes";
        }
        try {
            bike.setPicture(picture.getBytes());
            bikesService.save(bike);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload picture.");
            e.printStackTrace();
        }
        return "redirect:/bikes";
    }

    @GetMapping(value = "/image/{id}")
    public ResponseEntity<byte[]> getBikeImage(@PathVariable int id) {
        Bike bike = bikesService.findById(id);
        if (bike.getPicture() != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bike.getPicture());
        } else {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/static/images/default-image.jfif");
                byte[] byteArray;
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    int read;
                    byte[] buffer = new byte[4096];
                    while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    byteArray = outputStream.toByteArray();
                }
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(byteArray);
            } catch (IOException e) {
                return ResponseEntity.notFound().build();
            }
        }
    }

}
