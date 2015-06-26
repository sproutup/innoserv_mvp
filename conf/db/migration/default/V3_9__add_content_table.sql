create table content (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  url                       varchar(255),
  product_id                bigint,
  product_trial_id          bigint,
  user_id                   bigint,
  constraint pk_content primary key (id))
;

alter table content add constraint fk_content_product_27 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_content_product_27 on content (product_id);

alter table content add constraint fk_content_product_trial_28 foreign key (product_trial_id) references product_trial (id) on delete restrict on update restrict;
create index ix_content_product_trial_28 on content (product_trial_id);

alter table content add constraint fk_content_user_28 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_content_user_28 on content (user_id);
