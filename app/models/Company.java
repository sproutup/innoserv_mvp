package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model.Finder;

@Entity
public class Company extends TimeStampModel{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public String companyName;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="company")
	public List<Product> products;

	
	public static Finder<Long, Company> find = new Finder<Long, Company>(
			Long.class, Company.class);

	
	
	public static List<Company> getAll() {
		return find.all();
	}

	public static Company findbyID(Long id) {
		return find.byId(id);
	}
	

}
