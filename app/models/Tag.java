package models;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import play.Logger;
import play.libs.Json;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

    public String name;

    public Long counter;

	@OneToMany(cascade = CascadeType.ALL)
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
	public static Tag addTag(String name, Long refId, String refType) {
		Tag rs = null;
		TagLink taglink = null;

		name.toLowerCase().trim();

		// see if tag exists already
		rs = Tag.find.where()
				.eq("name", name)
				.findUnique();

		if (rs == null) {
			// Tag not found so we insert
			rs = new Tag();
			rs.name = name;
            rs.counter = 0L;
			rs.save();
		} else {
			// Tag found so check if tag is already added so we avoid duplicates
			taglink = TagLink.find.where()
					.eq("tag", rs)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// Add a new link to the tag and increase counter
		if (taglink == null) {
			taglink = new TagLink();
			taglink.refId = refId;
			taglink.refType = refType;

			rs.links.add(taglink);
            if(rs.counter==null) rs.counter = 0L;
            rs.counter = rs.counter + 1L;
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
		if (rs != null) {
			taglink = TagLink.find.where()
					.eq("tag", rs)
					.eq("refId", refId)
					.eq("refType", refType)
					.findUnique();
		}

		// if link is found then delete and decrease counter
		if (taglink != null) {
			taglink.delete();
            if(rs.counter==null) rs.counter = 0L;
            rs.counter = rs.counter - 1L;
            rs.save();
		}
	}

	/*
	Remove all tags from an object identified by refId and refType
	 */
	public static void removeAllTags(Long refId, String refType) {

		// find all tag links
		List<Tag> tags = getAllTags(refId, refType);

		// delete taglinks one by one
		for (Tag element : tags) {
			removeTag(element.name, refId, refType);
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
		List<Tag> tagwlinks = Tag.find.query()
				.setRawSql(rawSql)
				.where()
				.findList();

		return tagwlinks;
	}

    /*
    Get top tags
     */
    public static List<Tag> getTopTags(int size){
        List<Tag> rs = Tag.find.where().ge("counter", 1).orderBy("counter desc").setMaxRows(size).findList();
        return rs;
    }

    public static List<Tag> popularTagsInPost(long productId, int category){
        String sql = "select t.id as id, t.name as name, count(*) as counter FROM tag t, tag_link tl, post p " +
                "where t.id = tl.tag_id " +
                "and p.id = tl.ref_id " +
                "and tl.ref_type = 'models.Post' " +
                "and p.category = " + category + " " +
                "and p.product_id = " + productId + " " +
                "group by t.name " +
                "order by counter desc " +
                "limit 0, 10";

        // parse the sql
        RawSql rawSql = RawSqlBuilder.parse(sql)
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
		String tbl = refType.substring(p + 1);

		// build sql that select all the objects
		String sql = "select m.id FROM tag t, tag_link tl, " + tbl + " m " +
				"where t.id = tl.tag_id " +
				"and m.id = tl.ref_id " +
				"and tl.ref_type = '" + refType + "' " +
				"and t.name = '" + name.toLowerCase().trim() + "'";

		// parse the sql
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.create();

		return rawSql;
	}


	public ObjectNode toJson() {
		ObjectNode node = Json.newObject();
        node.put("name", this.name);
        node.put("counter", this.counter);
		return node;
	}

	public static ArrayNode toJson(List<Tag> tags){
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		for (Tag tag : tags){
			arrayNode.add(tag.toJson());
		}
		return arrayNode;
	}
}
