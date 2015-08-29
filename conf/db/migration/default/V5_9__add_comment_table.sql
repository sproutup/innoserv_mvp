create table comment (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  user_id                   bigint,
  body                      text,
  ref_id                    bigint,
  ref_type                  varchar(255),
  constraint pk_comment primary key (id)
  )
;

alter table comment add constraint fk_comment_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (user_id);
