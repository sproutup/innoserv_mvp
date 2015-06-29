create table trial (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  email                     varchar(255),
  name                      varchar(255),
  address                   varchar(1024),
  phone                     varchar(255),
  reason                    varchar(2048),
  active                    tinyint(1) default 0,
  product_id                bigint,
  user_id                   bigint,
  constraint pk_product_trial primary key (id))
;

alter table trial add constraint fk_trial_product_27 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_product_trial_27 on trial (product_id);

alter table trial add constraint fk_trial_user_28 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_trial_user_28 on trial (user_id);

alter table content add trial_id bigint;

alter table content add constraint fk_content_trial_28 foreign key (trial_id) references trial (id) on delete restrict on update restrict;
create index ix_content_trial_28 on content (trial_id);
