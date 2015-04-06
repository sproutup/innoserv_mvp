alter table users add external_type varchar(255);
alter table users add external tinyint(1) default 0;


alter table post alter column active_flag set default '1';
alter table product alter column active_flag set default '1';
alter table users alter column active set default '1';
alter table media alter column active_flag set default '1';
