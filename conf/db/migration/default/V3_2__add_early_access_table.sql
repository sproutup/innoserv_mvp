create table early_access_request (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  email                     varchar(255),
  name                      varchar(255),
  product_url               varchar(255),
  constraint pk_early_access_request primary key (id))
;

