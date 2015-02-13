create table likes (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  user_id                   bigint,
  ref_id                    bigint,
  ref_type                  varchar(255),
  constraint pk_likes primary key (id)
  )
;

alter table likes add constraint fk_likes_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_likes_user_1 on likes (user_id);
