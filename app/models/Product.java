package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product extends Model {

  public String productID;
  public String productName;
  public String productDescription;
  private List<Product> products;

  public Product(){
    initialize();
  }

  private Product(String productID, String productName, String productDescription){

    this.productID = productID;
    this.productDescription = productDescription;
    this.productName = productName;

  }

  public List<Product> getAll(){
    return products;
  }

  public Product findbyProductID(String productID){
    for (Product prod : products){
      if (prod.productID.equals(productID)){
        return prod;
      }
    }
    return null;
  }

  public List<Product> findbyProductName(String productName){
    final List<Product> results = new ArrayList<Product>();
    for (Product prod : products){
      if (prod.productName.toLowerCase().contains(productName)){
        results.add(prod);
      }
    }
    return results;
  }

  public boolean remove(Product product){
    return products.remove(product);
    //Todo remove from the db too
  }

  public void add(){
    products.remove(findbyProductID(this.productID));
    products.add(this);
  }

  private void initialize() {
    products = new ArrayList<Product>();
    products.add(new Product("11111", "Belleds", "Smart audio and lighting platform"));
    products.add(new Product("22222", "emerging product 2", "description 2"));
    products.add(new Product("33333", "emerging product 3", "description 3"));
    products.add(new Product("44444", "emerging product 4", "description 4"));
  }

}
