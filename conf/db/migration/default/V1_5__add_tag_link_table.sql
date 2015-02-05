create table tag_link (
  id                        bigint auto_increment not null,
  tag_id                    bigint,
  ref_id                    bigint,
  ref_type                  varchar(255),
  constraint pk_tag_link primary key (id))
;

alter table tag_link add constraint fk_tag_link_tag_6 foreign key (tag_id) references tag (id) on delete restrict on update restrict;

create index ix_tag_link_tag_6 on tag_link (tag_id);

delete from product_tag;

drop table product_tag;
