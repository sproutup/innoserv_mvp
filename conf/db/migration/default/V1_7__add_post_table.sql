create table post (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  title                     varchar(255),
  content                   TEXT,
  parent_id                 bigint,
  user_id                   bigint,
  version                   integer not null,
  constraint pk_post primary key (id))
;

alter table post add constraint fk_post_parent_6 foreign key (parent_id) references post (id) on delete restrict on update restrict;
create index ix_post_parent_6 on post (parent_id);

alter table post add constraint fk_post_user_7 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_post_user_7 on post (user_id);
