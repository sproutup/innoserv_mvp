drop table if exists numbers_small;
drop table if exists numbers;

create table numbers_small (
  number int
);

insert into numbers_small
values (0),(1),(2),(3),(4),(5),(6),(7),(8),(9);

create table numbers (
  number bigint
);

insert into numbers
select thousands.number * 1000 + hundreds.number * 100 + tens.number * 10 + ones.number
from numbers_small thousands, numbers_small hundreds, numbers_small tens, numbers_small ones
limit 1000000;

create table date_dim (
  id                bigint auto_increment not null,
  date              date not null,
  day               varchar(10),
  day_of_week       int,
  day_of_month      int,
  day_of_year       int,
  previous_day      date not null default '0000-00-00',
  next_day          date not null default '0000-00-00',
  weekend           varchar(10) not null default "Weekday",
  week_of_year      int,
  month             varchar(10),
  month_of_year     int,
  quarter_of_year   int,
  year              int,
  unique key `date` (`date`),
  constraint pk_date_dim primary key (id)
);

insert into date_dim (date)
select date_add( '2015-01-01', interval number day )
from numbers
where date_add( '2015-01-01', interval number day ) between '2015-01-01' and '2015-12-31'
order by number;

update date_dim set
day             = dayname(date),
day_of_week     = weekday(date),
day_of_month    = date_format( date, "%d" ),
day_of_year     = date_format( date, "%j" ),
previous_day    = date_add(date, interval -1 day),
next_day        = date_add(date, interval 1 day),
weekend         = if( weekday(date) in (5, 6), 'weekend', 'weekday'),
week_of_year    = date_format( date, "%v" ),
month           = monthname(date),
month_of_year   = date_format( date, "%m"),
quarter_of_year = quarter(date),
year            = year(date);

create table page_path_dim (
  id                bigint auto_increment not null,
  path              varchar(1024) not null,
  constraint pk_page_path_dim primary key (id)
);

create table metrics_dim (
  id                bigint auto_increment not null,
  type              varchar(256), -- unique_page_views, time_on_page,
  constraint pk_metrics_dim primary key (id)
);

insert into metrics_dim
values (1, 'unique_page_views'),
       (2, 'time_on_page'),
       (3, 'sessions'),
       (4, 'followers');

create table provider_dim (
  id                bigint auto_increment not null,
  name              varchar(256), -- gl-analytics, yt-analytics, twitter, facebook, instagram, pinterest
  constraint pk_provider_dim primary key (id)
);

insert into provider_dim
values (1, 'unique_page_views'),
       (2, 'yt-analytics'),
       (3, 'twitter'),
       (4, 'facebook'),
       (5, 'instagram'),
       (6, 'pinterest');

create table event_fact (
  id                bigint auto_increment not null,
  date_dim_id       bigint,
  user_id           bigint,
  product_id        bigint,
  page_path_dim_id  bigint,
  metrics_dim_id    bigint,
  provider_dim_id   bigint,
  counter           bigint,
  constraint pk_event_fact primary key (id)
);

alter table event_fact add constraint fk_event_fact_date_dim_1 foreign key (date_dim_id) references date_dim (id) on delete restrict on update restrict;
create index ix_event_fact_date_dim_1 on event_fact (date_dim_id);

alter table event_fact add constraint fk_event_fact_user_2 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_event_fact_user_4 on event_fact (user_id);

alter table event_fact add constraint fk_event_fact_product_3 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_event_fact_product_5 on event_fact (product_id);

alter table event_fact add constraint fk_event_fact_page_path_dim_4 foreign key (page_path_dim_id) references page_path_dim (id) on delete restrict on update restrict;
create index ix_event_fact_page_path_dim_4 on event_fact (page_path_dim_id);

alter table event_fact add constraint fk_event_fact_metrics_dim_5 foreign key (metrics_dim_id) references metrics_dim (id) on delete restrict on update restrict;
create index ix_event_fact_metrics_dim_5 on event_fact (metrics_dim_id);

alter table event_fact add constraint fk_event_fact_provider_dim_6 foreign key (provider_dim_id) references provider_dim (id) on delete restrict on update restrict;
create index ix_event_fact_provider_dim_6 on event_fact (provider_dim_id);
