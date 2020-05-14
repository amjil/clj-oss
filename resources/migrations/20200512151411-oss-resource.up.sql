create table oss_resource  (
  id serial primary key,
  user_id int not null,
  app_id int not null,

  file_size int not null default 0,
  content_type text not null,
  original_name text not null,
  file_name text,

  status smallint not null default 1,
  created_at timestamp default now(),
  updated_at timestamp default now()
);
--;;
create index oss_resource_user_index on oss_resource(user_id);
--;;
create unique index oss_resource_filename_index on oss_resource(file_name);
