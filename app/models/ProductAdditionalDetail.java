package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Constraint;

import play.db.ebean.Model.Finder;

@Entity
public class ProductAdditionalDetail extends TimeStampModel {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;

	@OneToOne
	@JoinColumn(name="product_id", unique=true)
	public Product product;
	
	@OneToOne
	@Column(nullable=true)
	public File bannerPhoto;

	public void setProduct(Product product) {
		this.product = product;
	}

	/*
	 * Product Description photo video
	 */
	@OneToOne
	@Column(nullable=true)
	public File descriptionVideo1;

	@OneToOne
	@Column(nullable=true)
	public File descriptionVideo2;

	@OneToOne
	@Column(nullable=true)
	public File descriptionVideo3;

	@OneToOne
	@Column(nullable=true)
	public File descriptionPhoto1;
	
	@OneToOne
	@Column(nullable=true)
	public File descriptionPhoto2;

	@OneToOne
	@Column(nullable=true)
	public File descriptionPhoto3;

	@Column(columnDefinition = "TEXT")
	public String productAdditionalInfo;

	/*
	 * Story stuff
	 */
	@Column(columnDefinition = "TEXT")
	public String productStoryPara1;

	@Column(columnDefinition = "TEXT")
	public String productStoryPara2;

	@Column(columnDefinition = "TEXT")
	public String productStoryPara3;

	@Column(columnDefinition = "TEXT")
	public String productStoryPara4;

	@OneToOne
	@Column(nullable=true)
	public File storyPhoto1;

	@OneToOne
	@Column(nullable=true)
	public File storyPhoto2;
	
	/*
	 * Team stuff
	 */
	public String member1Name;

	public String member2Name;

	public String member3Name;

	public String member4Name;

	public String member5Name;
	
	public String member6Name;
	
	public String member1Title;

	public String member2Title;

	public String member3Title;

	public String member4Title;

	public String member5Title;
	
	public String member6Title;
	
	@Column(columnDefinition = "TEXT")
	public String member1Description;

	@Column(columnDefinition = "TEXT")
	public String member2Description;

	@Column(columnDefinition = "TEXT")
	public String member3Description;

	@Column(columnDefinition = "TEXT")
	public String member4Description;

	@Column(columnDefinition = "TEXT")
	public String member5Description;
	
	@Column(columnDefinition = "TEXT")
	public String member6Description;	
	
	@OneToOne
	@Column(nullable=true)
	public File member1Photo;
	
	@OneToOne
	@Column(nullable=true)
	public File member2Photo;
	
	@OneToOne
	@Column(nullable=true)
	public File member3Photo;
	
	@OneToOne
	@Column(nullable=true)
	public File member4Photo;
	
	@OneToOne
	@Column(nullable=true)
	public File member5Photo;

	@OneToOne
	@Column(nullable=true)
	public File member6Photo;
	
	public boolean isMember1Primary = false;
	public boolean isMember2Primary = false;
	public boolean isMember3Primary = false;
	public boolean isMember4Primary = false;
	public boolean isMember5Primary = false;
	public boolean isMember6Primary = false;

	public static Finder<Long, ProductAdditionalDetail> find = new Finder<Long, ProductAdditionalDetail>(
			Long.class, ProductAdditionalDetail.class);

	public List<ProductAdditionalDetail> getAll() {
		return find.all();
	}

	public static ProductAdditionalDetail findbyID(Long id) {
		return find.byId(id);
	}
	
	public  static ProductAdditionalDetail findbyProductID(Long productID) {
		return find.where().eq("product_id", productID).findUnique();
	}
	
	@Override
	public void save() {
		super.save();
	}
	
	@Override
	public void update(Object o) {
		System.out.println(">>>update-o");
		super.update(o);
	}
	
	public void update() {
		System.out.println(">>>update");
		super.update();
	}
	
	  @Override
	  public String toString() {
	    return String.format("%d %s", id, product);
	  }

}
