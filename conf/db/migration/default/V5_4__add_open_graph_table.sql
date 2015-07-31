create table open_graph (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  content_id                bigint,
  title                     varchar(255),
  type                      varchar(255),
  image                     varchar(255),
  url                       varchar(255),
  description               varchar(255),
  site_name                 varchar(255),
  video                     varchar(255),
  constraint pk_open_graph primary key (id))
;

alter table open_graph add constraint fk_open_graph_content_15 foreign key (content_id) references content (id) on delete restrict on update restrict;
create index ix_open_graph_content_15 on open_graph (content_id);
