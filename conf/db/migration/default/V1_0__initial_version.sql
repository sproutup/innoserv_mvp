create table feedback (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  product_id                bigint,
  text_comments             varchar(255),
  date_time_stamp           datetime,
  likes_count               integer,
  constraint pk_feedback primary key (id))
;

create table follower (
  id                        bigint auto_increment not null,
  follower_id               bigint,
  following_id              bigint,
  constraint pk_follower primary key (id))
;

create table linked_account (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table media (
  id                        bigint auto_increment not null,
  media_id                  varchar(255),
  product_id                bigint,
  user_id                   bigint,
  cloud_front_link          varchar(255),
  submitted_by_user_id      varchar(255),
  product_name              varchar(255),
  media_file_name           varchar(255),
  media_type                varchar(255),
  likes_count               integer,
  date_time_stamp           datetime,
  active_flag               tinyint(1) default 0,
  constraint pk_media primary key (id))
;

create table product (
  id                        bigint auto_increment not null,
  product_ean               varchar(255),
  product_name              varchar(255),
  product_description       varchar(255),
  product_long_description  varchar(255),
  constraint pk_product primary key (id))
;

create table s3file (
  id                        varchar(40) not null,
  bucket                    varchar(255),
  file_name                 varchar(255),
  folder_name               varchar(255),
  submitted_user            varchar(255),
  product_name              varchar(255),
  constraint pk_s3file primary key (id))
;

create table security_role (
  id                        bigint auto_increment not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table tag (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_tag primary key (id))
;

create table token_action (
  id                        bigint auto_increment not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   datetime,
  expires                   datetime,
  constraint ck_token_action_type check (type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  phone_number              varchar(255),
  street_address1           varchar(255),
  street_address2           varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  zipcode                   varchar(255),
  gender                    varchar(255),
  dateofbirth               datetime,
  last_login                datetime,
  active                    tinyint(1) default 0,
  email_validated           tinyint(1) default 0,
  constraint pk_users primary key (id))
;

create table user_permission (
  id                        bigint auto_increment not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;


create table media_feedback (
  media_id                       bigint not null,
  feedback_id                    bigint not null,
  constraint pk_media_feedback primary key (media_id, feedback_id))
;

create table product_tag (
  product_id                     bigint not null,
  tag_id                         bigint not null,
  constraint pk_product_tag primary key (product_id, tag_id))
;

create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;
alter table feedback add constraint fk_feedback_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_feedback_user_1 on feedback (user_id);
alter table feedback add constraint fk_feedback_product_2 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_feedback_product_2 on feedback (product_id);
alter table linked_account add constraint fk_linked_account_user_3 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_3 on linked_account (user_id);
alter table media add constraint fk_media_product_4 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_media_product_4 on media (product_id);
alter table media add constraint fk_media_user_5 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_media_user_5 on media (user_id);
alter table token_action add constraint fk_token_action_targetUser_6 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_6 on token_action (target_user_id);



alter table media_feedback add constraint fk_media_feedback_media_01 foreign key (media_id) references media (id) on delete restrict on update restrict;

alter table media_feedback add constraint fk_media_feedback_feedback_02 foreign key (feedback_id) references feedback (id) on delete restrict on update restrict;

alter table product_tag add constraint fk_product_tag_product_01 foreign key (product_id) references product (id) on delete restrict on update restrict;

alter table product_tag add constraint fk_product_tag_tag_02 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_security_role_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_permission_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;
