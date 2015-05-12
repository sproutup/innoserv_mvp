alter table users add avatar_id bigint;

alter table users add constraint fk_users_avatar_31 foreign key (avatar_id) references file (id) on delete restrict on update restrict;
create index ix_users_avatar_31 on users (avatar_id);
