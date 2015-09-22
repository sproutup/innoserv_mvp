alter table reward_event add constraint fk_reward_event_content_27 foreign key (content_id) references content (id) on delete restrict on update restrict;
create index ix_reward_event_content_27 on reward_event (content_id);
