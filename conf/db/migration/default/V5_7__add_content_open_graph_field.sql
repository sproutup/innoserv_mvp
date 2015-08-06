alter table content add open_graph_id bigint;

alter table content add constraint fk_content_openGraph_8 foreign key (open_graph_id) references open_graph (id) on delete restrict on update restrict;
create index ix_content_openGraph_8 on content (open_graph_id);
