alter table event_fact add content_id bigint;

alter table event_fact add constraint fk_event_fact_content foreign key (content_id) references content (id) on delete restrict on update restrict;
create index ix_event_fact_content on event_fact (content_id);

alter table event_fact drop foreign key fk_event_fact_page_path_dim_4;
drop index ix_event_fact_page_path_dim_4 ON event_fact;
alter table event_fact drop column page_path_dim_id;
