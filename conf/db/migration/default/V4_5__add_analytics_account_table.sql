create table analytics_account (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  user_id                   bigint,
  provider                  varchar(255),
  access_token              varchar(255),
  refresh_token             varchar(255),
  google_analytics_api      tinyint(1) default 0,
  youtube_analytics_api     tinyint(1) default 0,
  constraint pk_analytics_account primary key (id)
);

alter table analytics_account add constraint fk_analytics_account_user_3 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_analytics_account_user_3 on analytics_account (user_id);
