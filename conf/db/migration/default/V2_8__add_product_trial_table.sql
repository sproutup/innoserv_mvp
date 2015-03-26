create table product_trial (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  email                     varchar(255),
  name                      varchar(255),
  active                    tinyint(1) default 0,
  product_id                bigint,
  user_id                   bigint,
  constraint pk_product_trial primary key (id))
;

alter table product_trial add constraint fk_product_trial_product_27 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_product_trial_product_27 on product_trial (product_id);

alter table product_trial add constraint fk_product_trial_user_28 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_product_trial_user_28 on product_trial (user_id);
