insert into reward_activity
values (6003, now(), now(), 'Suggest Product', 100, 'Suggest a Product', '', 6, 1);

alter table product_suggestion add user_id bigint;
alter table product_suggestion add  open_graph_id bigint;
alter table product_suggestion add  body TEXT;
alter table product_suggestion add  active_flag tinyint(1) default 0;

alter table product_suggestion add constraint fk_product_suggestion_user_40 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_product_suggestion_user_40 on product_suggestion (user_id);
alter table product_suggestion add constraint fk_product_suggestion_openGraph_41 foreign key (open_graph_id) references open_graph (id) on delete restrict on update restrict;
create index ix_product_suggestion_openGraph_41 on product_suggestion (open_graph_id);
