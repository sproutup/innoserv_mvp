delete from media_feedback;

drop table media_feedback;

delete from feedback;

drop table feedback;

create table post (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  product_id                bigint,
  post_text                 TEXT,
  title                     varchar(255),
  active_flag               tinyint(1) default 0,
  category                  varchar(255),
  date_time_stamp           datetime,
  likes_count               integer,
  parent_id                 bigint,
  constraint pk_post primary key (id))
;

alter table post add constraint fk_post_user_4 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_post_user_4 on post (user_id);
alter table post add constraint fk_post_product_5 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_post_product_5 on post (product_id);
alter table post add constraint fk_post_parent_6 foreign key (parent_id) references post (id) on delete restrict on update restrict;
create index ix_post_parent_6 on post (parent_id);

