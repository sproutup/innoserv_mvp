package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by peter on 3/23/15.
 */
@Entity
public class ProductSuggestion extends TimeStampModel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public String email;

    public String productName;

    public String productUrl;
}
