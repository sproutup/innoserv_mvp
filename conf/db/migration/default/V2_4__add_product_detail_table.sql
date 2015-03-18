alter table product modify product_long_description  TEXT;
alter table product modify feature_list TEXT;
alter table product add  created_at datetime;
alter table product add updated_at datetime;
alter table product add url_crowd_funding_campaign varchar(255);
alter table product add active_flag tinyint(1) default 1;
alter table product add trial_sign_up_flag tinyint(1) default 0;
alter table product add buy_flag tinyint(1) default 0;
alter table product add contact_email_address varchar(255);

create table product_additional_detail (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  product_id                bigint,
  banner_photo_id           bigint,
  description_video1_id     bigint,
  description_video2_id     bigint,
  description_video3_id     bigint,
  description_photo1_id     bigint,
  description_photo2_id     bigint,
  description_photo3_id     bigint,
  product_additional_info   TEXT,
  product_story_para1       TEXT,
  product_story_para2       TEXT,
  product_story_para3       TEXT,
  product_story_para4       TEXT,
  story_photo1_id           bigint,
  story_photo2_id           bigint,
  member1name               varchar(255),
  member2name               varchar(255),
  member3name               varchar(255),
  member4name               varchar(255),
  member5name               varchar(255),
  member6name               varchar(255),
  member1title              varchar(255),
  member2title              varchar(255),
  member3title              varchar(255),
  member4title              varchar(255),
  member5title              varchar(255),
  member6title              varchar(255),
  member1description        TEXT,
  member2description        TEXT,
  member3description        TEXT,
  member4description        TEXT,
  member5description        TEXT,
  member6description        TEXT,
  member1photo_id           bigint,
  member2photo_id           bigint,
  member3photo_id           bigint,
  member4photo_id           bigint,
  member5photo_id           bigint,
  member6photo_id           bigint,
  is_member1primary         tinyint(1) default 0,
  is_member2primary         tinyint(1) default 0,
  is_member3primary         tinyint(1) default 0,
  is_member4primary         tinyint(1) default 0,
  is_member5primary         tinyint(1) default 0,
  is_member6primary         tinyint(1) default 0,
  constraint pk_product_additional_detail primary key (id))
;

alter table product_additional_detail add constraint fk_product_additional_detail_product_10 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_product_additional_detail_product_10 on product_additional_detail (product_id);
alter table product_additional_detail add constraint fk_product_additional_detail_bannerPhoto_11 foreign key (banner_photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_bannerPhoto_11 on product_additional_detail (banner_photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionVideo1_12 foreign key (description_video1_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionVideo1_12 on product_additional_detail (description_video1_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionVideo2_13 foreign key (description_video2_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionVideo2_13 on product_additional_detail (description_video2_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionVideo3_14 foreign key (description_video3_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionVideo3_14 on product_additional_detail (description_video3_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionPhoto1_15 foreign key (description_photo1_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionPhoto1_15 on product_additional_detail (description_photo1_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionPhoto2_16 foreign key (description_photo2_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionPhoto2_16 on product_additional_detail (description_photo2_id);
alter table product_additional_detail add constraint fk_product_additional_detail_descriptionPhoto3_17 foreign key (description_photo3_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_descriptionPhoto3_17 on product_additional_detail (description_photo3_id);
alter table product_additional_detail add constraint fk_product_additional_detail_storyPhoto1_18 foreign key (story_photo1_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_storyPhoto1_18 on product_additional_detail (story_photo1_id);
alter table product_additional_detail add constraint fk_product_additional_detail_storyPhoto2_19 foreign key (story_photo2_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_storyPhoto2_19 on product_additional_detail (story_photo2_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member1Photo_20 foreign key (member1photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member1Photo_20 on product_additional_detail (member1photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member2Photo_21 foreign key (member2photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member2Photo_21 on product_additional_detail (member2photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member3Photo_22 foreign key (member3photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member3Photo_22 on product_additional_detail (member3photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member4Photo_23 foreign key (member4photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member4Photo_23 on product_additional_detail (member4photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member5Photo_24 foreign key (member5photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member5Photo_24 on product_additional_detail (member5photo_id);
alter table product_additional_detail add constraint fk_product_additional_detail_member6Photo_25 foreign key (member6photo_id) references file (id) on delete restrict on update restrict;
create index ix_product_additional_detail_member6Photo_25 on product_additional_detail (member6photo_id);
