create table company (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  company_name              varchar(255),
  constraint pk_company primary key (id))
;

alter table product add company_id	bigint;

alter table product add constraint fk_product_company_10 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_product_company_10 on product (company_id);

