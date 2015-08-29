alter table post add content_id bigint;
alter table post change column content body text;
alter table post drop column date_time_stamp;

alter table post add constraint fk_post_content foreign key (content_id) references content (id) on delete restrict on update restrict;
create index ix_post_content on post (content_id);
