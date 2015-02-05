package models;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import play.Logger;
@Entity
public class Tag extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Tag findById(Long id) {
		return find.byId(id);
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	public String name;

	@OneToMany(cascade=CascadeType.ALL)
	public List<TagLink> links;

	public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class,
			Tag.class);

	public Tag() {
		// Left empty
	}

	public Tag(String name) {
		this.name = name;
	}


	/*
	Add tag to an object identified by refId and refType
	 */
	public static Tag addTag(String name, Long refId, String refType){
		Tag rs = null;
		TagLink taglink = null;

		name.toLowerCase().trim();

		// see if tag exists already
		rs = Tag.find.where()
				.eq("name", name)
				.findUnique();

		if(rs == null) {
			// Tag not found so we insert
			rs = new Tag();
			rs.name = name;
			rs.save();
		}
		else {
			// Tag found so check if tag is already added so we avoid duplicates
			taglink = TagLink.find.where()
					.eq("tag", rs)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// Add a new link to the tag
		if(taglink == null){
			taglink = new TagLink();
			taglink.refId = refId;
			taglink.refType = refType;

			rs.links.add(taglink);
			rs.save();
		}

		return rs;
	}

	/*
	Remove tag from an object identified by refId and refType
	 */
	public static void removeTag(String name, Long refId, String refType) {
		Tag rs = null;
		TagLink taglink = null;

		name.toLowerCase().trim();

		// find tag
		rs = Tag.find.where()
				.eq("name", name)
				.findUnique();

		// if tag is found then find link
		if(rs != null) {
			taglink = TagLink.find.where()
					.eq("tag", rs)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// if link is found then delete
		if(taglink != null){
			taglink.delete();
		}
	}

	/*
	Remove all tags from an object identified by refId and refType
	 */
	public static void removeAllTags(Long refId, String refType) {

		// find all tag links
		List<TagLink> links = TagLink.find.where()
				.eq("refId", refId)
				.eq("refType", refType)
				.findList();

		// delete tag links one by one
		for (TagLink element : links) {
			element.delete();
		}
	}

	/*
	Get all tags on an object identified by refId and refType
	 */
	public static List<Tag> getAllTags(Long refId, String refType) {
		// sql that selects all the tag links for the object
		String sql = "SELECT t.id, t.name FROM tag t, tag_link tl " +
			"where t.id = tl.tag_id " +
			"and tl.ref_id = " + refId + " " +
			"and tl.ref_type = '" + refType + "'";

		// parse the sql
		RawSql rawSql = RawSqlBuilder.parse(sql)
			// map resultSet columns to bean properties
			.columnMapping("t.id", "id")
			.columnMapping("t.name", "name")
			.create();

		// execute the query
		List<Tag> links = Tag.find.query()
				.setRawSql(rawSql)
				.where()
				.findList();

		return links;
	}

	/*
	Raw sql helper function
	Returns sql that finds all objects with the specified tag and class = refType
	 */
	public static RawSql findAllByTagRawSql(String name, String refType) {
		// get last part of class name which is the table name  model.product -> product
		int p = refType.lastIndexOf(".");
		String tbl = refType.substring(p+1);

		// build sql that select all the objects
		String sql = "select m.id FROM tag t, tag_link tl, "+ tbl +" m " +
				"where t.id = tl.tag_id " +
				"and m.id = tl.ref_id " +
				"and tl.ref_type = '"+ refType +"' " +
				"and t.name = '"+ name.toLowerCase().trim() +"'";

		// parse the sql
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.create();

		return rawSql;
	}
}
