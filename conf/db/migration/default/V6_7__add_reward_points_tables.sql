create table reward_category (
  id                        bigint not null,
  created_at                datetime,
  updated_at                datetime,
  title                     varchar(255),
  description               varchar(255),
  active                    tinyint(1) default 1,
  constraint pk_reward_category primary key (id));

insert into reward_category
values (1, now(), now(), 'Content Creation', '', 1),
       (2, now(), now(), 'Spread the word on Social Media/SproutUp Buzz', '', 1),
       (3, now(), now(), 'Support other influencers content', '', 1),
       (4, now(), now(), 'Believes in the product', '', 1),
       (5, now(), now(), 'Enthusiasm', '', 1),
       (6, now(), now(), 'Misc', '', 1);

create table reward_activity (
  id                        bigint not null,
  created_at                datetime,
  updated_at                datetime,
  title                     varchar(255),
  points                    bigint,
  description               varchar(1024),
  criteria                  varchar(1024),
  reward_category_id        bigint,
  active                    tinyint(1) default 1,
  constraint pk_reward_activity primary key (id));

alter table reward_activity add constraint fk_reward_activity_reward_category_1 foreign key (reward_category_id) references reward_category (id) on delete restrict on update restrict;
create index ix_reward_activity_reward_category_1 on reward_activity (reward_category_id);

insert into reward_activity
values (1000, now(), now(), 'Publish content from approved trial', 1000, 'A Blog (with embedded photos, video), or a YouTube/Vimeo video, or a live demo on Periscope/Meerkat', 'Video length: 30-60 seconds (Vine); 2-5 minutes (YouTube, Vimeo); Blog is published and includes product use photos, a personal referral link; Has given sproutUp access to blog page Google Analytics; Posted on SproutUp Buzz', 1, 1),
       (2000, now(), now(), 'Post Instagram', 20, 'URL to IG post', 'original post that includes product handle and @SproutUpco handle', 2, 1),
       (2001, now(), now(), 'Post Vine', 20, 'URL to Vine post', 'original post that includes product handle and @SproutUpco handle', 2, 1),
       (2002, now(), now(), 'Tweet', 20, 'URL to TWT post', 'original post that includes product handle and @SproutUpco handle', 2, 1),
       (2003, now(), now(), 'Post on FaceBook', 20, 'URL to FB post', 'original post that includes product handle and @SproutUpco handle', 2, 1),
       (2004, now(), now(), 'Post on YouTube', 20, 'URL to video post', 'original post that includes product handle and @SproutUpco handle', 2, 1),
       (3000, now(), now(), 'Share a Post from SproutUp', 10, 'share a post or a general comment about a product using SproutUp Buzz', '', 3, 1),
       (3001, now(), now(), 'Retweet a post of other influencer', 10, '', '', 3, 1),
       (3002, now(), now(), 'Comment on a Post of other influencer', 10, 'comment on SproutUp Buzz', 'some meaningful content that builds up a discussion;', 3, 1),
       (3003, now(), now(), 'Answer a question', 10, 'answer a question on SproutUp Buzz', 'provide an answer to a open question posted by an influencer', 3, 1),
       (4000, now(), now(), 'Buy product', 5000, 'Use SproutUp discount coupon to buy a product', '', 4, 1),
       (4001, now(), now(), 'Keep product', 1, 'Keeps the product as reward', '', 4, 1),
       (5000, now(), now(), 'Complete trial within 3 days of receiving product', 300, '', '', 5, 1),
       (5001, now(), now(), 'Complete trial within allocated time', 100, '', '', 5, 1),
       (5002, now(), now(), 'Completes trial but 5 days late in returning product after deadline', -200, '', '', 5, 1),
       (5003, now(), now(), 'No Content and no return of product', -2000, '', '', 5, 1),
       (6000, now(), now(), 'Payout', 0, 'User cash out', '', 6, 1),
       (6001, now(), now(), 'Bonus points', 0, 'Bonus points manual', '', 6, 1),
       (6002, now(), now(), 'Performance points', 0, 'Measure of outreach', '', 6, 1);

create table reward_event (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  points                    bigint,
  user_id                   bigint,
  content_id                bigint,
  product_id                bigint,
  reward_activity_id        bigint,
  active                    tinyint(1) default 1,
  constraint pk_reward_event primary key (id));

alter table reward_event add constraint fk_reward_event_user_28 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_reward_event_user_28 on reward_event (user_id);

alter table reward_event add constraint fk_reward_event_product_27 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_reward_event_product_27 on reward_event (product_id);

alter table reward_event add constraint fk_reward_event_reward_activity_1 foreign key (reward_activity_id) references reward_activity (id) on delete restrict on update restrict;
create index ix_reward_event_reward_activity_1 on reward_event (reward_activity_id);
