package nix.project.mybike.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "bike")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bike {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Should not be empty")
    @Size(min = 2, max = 100, message = "From 2 to 100 symbols")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Should not be empty")
    @Size(min = 2, max = 100, message = "From 2 to 100 symbols")
    @Column(name = "producer")
    private String producer;

    @Min(value = 1900, message = "Should be greater than 1900")
    @Column(name = "year")
    private Integer year;

    @Column(name = "price_per_hour")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonIgnore
    private Client owner;

    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "picture")
    @JsonIgnore
    private byte[] picture;

    @OneToMany(mappedBy = "bike")
    @JsonIgnore
    private List<Debt> debts;

    @Transient
    private boolean expired;

    @Value("${constant.expired}")
    @Transient
    private int rentPeriod;

    public void checkExpired() {
        var takenAt = this.getTakenAt();
        long diffInMillies = 0;
        if(takenAt!=null) {
            diffInMillies = Math.abs(takenAt.getTime() - new Date().getTime());
        }
        if (diffInMillies > rentPeriod) {
            this.setExpired(true);
        }
    }

}
