create table campaign (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  product_id                bigint,
  campaign_name             varchar(255),
  campaign_short_description varchar(255),
  campaign_long_description TEXT,
  begin_date                datetime,
  end_date                  datetime,
  campaign_share_message    TEXT,
  campaign_perks1           TEXT,
  campaign_perks2           TEXT,
  campaign_perks3           TEXT,
  campaign_perks4           TEXT,
  active                    tinyint(1) default 1,
  campaign_outcome          TEXT,
  total_num_viewed          integer,
  total_num_participated    integer,
  constraint pk_campaign primary key (id))
;


create table user_referral (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  referral_id               varchar(255),
  num_visits                integer,
  requested_disc            tinyint(1) default 0,
  shared_on_social_media    tinyint(1) default 0,
  active                    tinyint(1) default 1,
  campaign_id               bigint,
  user_id                   bigint,
  referral_id          		varchar(255),
  constraint pk_user_referral primary key (id))
;

alter table campaign add constraint fk_campaign_product_1 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_campaign_product_1 on campaign (product_id);
alter table user_referral add constraint fk_user_referral_campaign_34 foreign key (campaign_id) references campaign (id) on delete restrict on update restrict;
create index ix_user_referral_campaign_34 on user_referral (campaign_id);
alter table user_referral add constraint fk_user_referral_user_35 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_referral_user_35 on user_referral (user_id);

