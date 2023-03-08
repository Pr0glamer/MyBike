package nix.project.mybike.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;

@AllArgsConstructor
@Getter
@Setter
public class DebtDTO {
    private Client client;
    private Bike bike;
    private Integer amount;
}