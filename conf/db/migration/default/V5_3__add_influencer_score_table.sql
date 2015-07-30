create table influencer_score (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  user_id                   bigint,
  twitter_followers         bigint,
  facebook_followers        bigint,
  blog_followers            bigint,
  youtube_subs              bigint,
  klout_score               double,
  influencer_score          bigint,
  constraint pk_influencer_score primary key (id))
;

alter table influencer_score add constraint fk_influencer_score_user_10 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_influencer_score_user_10 on influencer_score (user_id);
