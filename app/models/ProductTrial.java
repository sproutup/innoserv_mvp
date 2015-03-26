package models;

import javax.persistence.*;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class ProductTrial extends TimeStampModel {

    public static Finder<Long, ProductTrial> find = new Finder<Long, ProductTrial>(
            Long.class, ProductTrial.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String name;

    public boolean active = true;

    @ManyToOne
    public Product product;

    @ManyToOne
    public User user;
}
