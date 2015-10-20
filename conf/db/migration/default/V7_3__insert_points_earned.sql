insert into reward_event (created_at, updated_at, points, user_id, reward_activity_id)
select created_at, created_at, 10, user_id, 3002
from comment
where created_at < '2015-09-28 16:44:56';

insert into reward_event (created_at, updated_at, points, user_id, reward_activity_id, product_id)
select created_at, created_at, 1000, user_id, 1000, product_id
from content
where created_at < '2015-09-28 16:44:56'
group by url;
