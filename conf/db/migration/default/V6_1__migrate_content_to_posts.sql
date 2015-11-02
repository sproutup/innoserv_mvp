INSERT INTO post
(user_id, product_id, body, created_at, updated_at, content_id)
SELECT user_id, product_id, url, created_at, updated_at, id
FROM content;
