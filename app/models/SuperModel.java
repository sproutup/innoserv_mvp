package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import play.db.ebean.Model;

@MappedSuperclass
public class SuperModel extends Model {

  @Id
  @GeneratedValue
  public Long id;

  @Column(name = "created_at")
  public Date createdAt;

  @Column(name = "updated_at")
  public Date updatedAt;

  @Version
  public int version;

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

  @PrePersist
  void createdAt() {
    this.createdAt = this.updatedAt = new Date();
  }

  @PreUpdate
  void updatedAt() {
    this.updatedAt = new Date();
  }
}