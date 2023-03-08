package nix.project.mybike.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "reservations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reservation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reserve_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reserveDate;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client clientWhoReserved;

    @ManyToOne
    @JoinColumn(name = "bike_id", referencedColumnName = "id")
    private Bike reservedBike;

}
