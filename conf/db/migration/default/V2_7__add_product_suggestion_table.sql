create table product_suggestion (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  email                     varchar(255),
  product_name              varchar(255),
  product_url               varchar(255),
  constraint pk_product_suggestion primary key (id))
;

