create table youtube_channel (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  id_str                    varchar(20),
  user_id                   bigint,
  title                     varchar(100),
  description               varchar(1000),
  published_at              datetime,
  thumbnail_url             varchar(1024),
  constraint pk_trial_log primary key (id))
;

alter table youtube_channel add constraint fk_youtube_channel_user foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_youtube_channel_user on youtube_channel (user_id);

create table youtube_video (
  id                        bigint auto_increment not null,
  created_at                datetime,
  updated_at                datetime,
  id_str                    varchar(20),
  youtube_channel_id        bigint,
  user_id                   bigint,
  published_at              datetime,
  title                     varchar(100),
  description               varchar(5000),
  thumbnail_url             varchar(1024),
  constraint pk_youtube_video primary key (id))
;

alter table youtube_video add constraint fk_youtube_video_user foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_youtube_video_user on youtube_video (user_id);

alter table youtube_video add constraint fk_youtube_video_youtube_channel foreign key (youtube_channel_id) references youtube_channel (id) on delete restrict on update restrict;
create index ix_youtube_video_youtube_channel on youtube_video (youtube_channel_id);
