create table oss_application  (
  id serial primary key,
  user_id int not null,
  apikey text not null,
  apisecret text not null,
  total_spaces int default 0,
  used_spaces int default 0,

  status smallint not null default 1,
  created_at timestamp default now(),
  updated_at timestamp default now()
);
--;;
create index oss_application_apikey_index on oss_application(apikey);
--;;
create index oss_application_user_index on oss_application(user_id);
