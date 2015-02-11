alter table post add content varchar(255);
alter table post add created_at datetime;
alter table post add updated_at datetime;
alter table post drop column post_text;
