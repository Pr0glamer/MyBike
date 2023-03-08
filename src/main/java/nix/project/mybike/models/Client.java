package nix.project.mybike.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "client")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Should not be empty")
    @Size(min = 2, max = 100, message = "From 2 to 100 symbols")
    @Column(name = "full_name")
    private String fullName;

    @Override
    public String toString() {
        return fullName + " (" + login + ')';
    }

    @Min(value = 1900, message = "Must be grater than 1900")
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Column(name = "telephone")
    private Long telephone;

    @Column(name = "user_role")
    private String role;

    @NotEmpty(message = "Should not be empty")
    @Size(min = 2, max = 100, message = "From 2 to 100 symbols")
    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    public List<Debt> getDebts() {
        return debts == null ? Collections.emptyList() : debts;
    }

    @OneToMany(mappedBy = "owner")
    private List<Bike> bikes;

    @OneToMany(mappedBy = "owner")
    private List<Debt> debts;

    @OneToMany(mappedBy = "clientWhoReserved")
    private List<Reservation> reservations;

    @JsonIgnore
    public int getDebt() {
        return  debts == null ? 0 : debts.stream().mapToInt(Debt::getAmount).sum();
    }
}
