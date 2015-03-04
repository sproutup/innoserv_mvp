
create table file (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  uuid                      varchar(40),
  user_id                   bigint,
  ref_id                    bigint,
  ref_type                  varchar(255),
  bucket                    varchar(255),
  name                      varchar(255),
  original_name             varchar(255),
  length                    bigint,
  type                      varchar(255),
  folder                    varchar(255),
  verified                  boolean default false,
  constraint pk_file primary key (id))
;

alter table file add constraint fk_file_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_file_user_1 on file (user_id);
