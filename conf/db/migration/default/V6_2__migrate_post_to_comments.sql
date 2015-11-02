INSERT INTO comment
(user_id, body, created_at, updated_at, ref_id, ref_type)
SELECT user_id, body, created_at, updated_at, parent_id, 'models.post'
FROM post
where parent_id is not null;

delete from post
where parent_id is not null;
