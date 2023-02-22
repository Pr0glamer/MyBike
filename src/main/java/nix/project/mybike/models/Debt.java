package nix.project.mybike.models;


import javax.persistence.*;
import javax.validation.constraints.Min;


@Entity
@Table(name = "client_debt")
public class Debt {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    @Column(name = "amount")
    @Min(value = 0)
    private Integer amount;

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client owner;

    @ManyToOne
    @JoinColumn(name = "bike_id", referencedColumnName = "id")
    private Bike bike;


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public Debt() {
    }
}
