create table trial_log (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  status                    int,
  trial_id                  bigint,
  constraint pk_trial_log primary key (id))
;

alter table trial_log add constraint fk_trial_log_trial_27 foreign key (trial_id) references trial (id) on delete restrict on update restrict;
create index ix_trial_log_trial_27 on trial_log (trial_id);
