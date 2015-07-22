alter table user_referral add trial_id bigint;
alter table user_referral add constraint fk_user_referral_trial_36 foreign key (trial_id) references trial (id) on delete restrict on update restrict;
create index ix_user_referral_trial_36 on user_referral (trial_id);

