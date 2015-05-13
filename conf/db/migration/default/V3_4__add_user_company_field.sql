alter table users add company_id bigint;

alter table users add constraint fk_users_company_32 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_users_company_32 on users (company_id);
