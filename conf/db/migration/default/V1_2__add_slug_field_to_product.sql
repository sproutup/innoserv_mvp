alter table product add slug varchar(255);
alter table product add constraint uq_product_slug unique (slug);

update product
set slug = lower(replace(product_name,' ','-'))
where slug is null;