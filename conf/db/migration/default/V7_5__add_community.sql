drop table community;

create table community (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  name                      varchar(255),
  slug                      varchar(255),
  tagline                   varchar(255),
  logo_image_id             bigint,
  active_flag               boolean default true not null,
  constraint pk_community primary key (id));

alter table product add community_id bigint;

alter table community add constraint fk_community_logoImage_5 foreign key (logo_image_id) references file (id) on delete restrict on update restrict;
create index ix_community_logoImage_5 on community (logo_image_id);

alter table product add constraint fk_product_community_23 foreign key (community_id) references community (id) on delete restrict on update restrict;
create index ix_product_community_23 on product (community_id);
