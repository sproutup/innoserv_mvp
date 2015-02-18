package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.db.ebean.Model;

@MappedSuperclass
public class TimeStampModel extends Model{

	  @Column(name = "created_at")
	  public Date createdAt;

	  @Column(name = "updated_at")
	  public Date updatedAt;

	  @PrePersist
	  void createdAt() {
	    this.createdAt = this.updatedAt = new Date();
	  }

	  @PreUpdate
	  void updatedAt() {
	    this.updatedAt = new Date();
	  }

	  @Override
	  public void save() {
	    createdAt();
	    super.save();
	  }

	  @Override
	  public void update() {
	    updatedAt();
	    super.update();
	  }
}
