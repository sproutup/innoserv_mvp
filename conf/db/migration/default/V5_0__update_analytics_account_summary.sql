alter table analytics_account_summary add viewCount bigint;
alter table analytics_account_summary add followerCount bigint;
alter table analytics_account_summary add itemCount bigint;
alter table analytics_account_summary add is_valid tinyint(1) default 0;
alter table analytics_account_summary add error_message varchar(255);

