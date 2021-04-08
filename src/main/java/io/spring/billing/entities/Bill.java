package io.spring.billing.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"id"})
public class Bill implements BillingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String description;

    private String observation;

    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "bill_id")
    private List<Line> lines;

    @Embedded
    private Audit audit = new Audit();

    public void addLine(Line line) {
        if (lines == null) {
            lines = new ArrayList<>();
        }
        lines.add(line);
    }

    public Double getTotal() {
        Double total = 0.0;
        for (Line line : Optional.ofNullable(lines).orElse(Collections.emptyList())) {
            total += line.calculateAmount();
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("\t\n{id: %s, description: %s, observation: %s, client: %s}",
                getId(), getDescription(), getObservation(), getClient().getName()+" "+getClient().getSurname());
    }

}
