create table analytics_account_summary (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  analytics_account_id      bigint,
  ga_id                     varchar(255),
  kind                      varchar(255),
  name                      varchar(255),
  constraint pk_analytics_account_summary primary key (id)
);

alter table analytics_account_summary add constraint fk_analytics_account_summary_analytics_account_1 foreign key (analytics_account_id) references analytics_account (id) on delete restrict on update restrict;
create index ix_analytics_account_summary_analytics_account_1 on analytics_account_summary (analytics_account_id);

alter table analytics_account add username varchar(255);
alter table analytics_account add is_valid tinyint(1) default 0;
alter table analytics_account add error_message varchar(255);

