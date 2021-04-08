package io.spring.billing.entities;

import com.sun.istack.internal.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"id"})
public class Line implements BillingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer quantity;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="bill_id")
    private Bill bill;

    @Embedded
    private Audit audit = new Audit();

    public Double calculateAmount() {
        return quantity.doubleValue() * product.getPrice();
    }

    @Override
    public String toString() {
        return String.format("\t\n{id: %s, quantity: %s, product: %s, bill: %s}",
                getId(), getQuantity(), getProduct().getId(), getBill().getId());
    }
}
