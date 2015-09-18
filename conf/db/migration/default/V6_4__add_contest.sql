create table contest (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  product_id                bigint,
  contest_name              varchar(255),
  contest_title             varchar(255),
  contest_button_title      varchar(255),
  contest_description       TEXT,
  begin_date                datetime,
  end_date                  datetime,
  social_media_share_message TEXT,
  contest_perk              TEXT,
  contest_confirmation      TEXT,
  active                    tinyint(1) default 0,
  contest_outcome           TEXT,
  total_num_participated    integer,
  constraint pk_contest primary key (id))
;

alter table contest add constraint fk_contest_product_10 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_contest_product_10 on contest (product_id);
