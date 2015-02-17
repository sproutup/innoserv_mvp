delete from follower;

drop table follower;

create table follow (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  user_id                   bigint,
  ref_type                  varchar(255),
  ref_id                    bigint,
  constraint pk_follow primary key (id))
;

alter table follow add constraint fk_follow_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_follow_user_1 on follow (user_id);

alter table security_role add constraint uq_security_role_role_name unique (role_name);
