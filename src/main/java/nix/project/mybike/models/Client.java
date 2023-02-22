package nix.project.mybike.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "client")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Should not be empty")
    @Size(min = 2, max = 100, message = "From 2 to 100 symbols")
    @Column(name = "full_name")
    private String fullName;

    @Min(value = 1900, message = "Must be grater than 1900")
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Column(name = "telephone")
    private Long telephone;

    public List<Debt> getDebts() {
        return debts == null ? Collections.emptyList() : debts.stream().filter(d -> d.getAmount() > 0).collect(Collectors.toList());
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    @OneToMany(mappedBy = "owner")
    private List<Bike> bikes;

    @OneToMany(mappedBy = "owner")
    private List<Debt> debts;

    @JsonIgnore
    public int getDebt() {
        return  debts == null ? 0 : debts.stream().mapToInt(Debt::getAmount).sum();
    }
}
