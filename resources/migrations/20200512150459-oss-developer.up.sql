create table oss_developer  (
  id serial primary key,
  name text,
  mobile text,
  password text,
  email text,
  intro text,
  status smallint not null default 1,

  created_at timestamp default now(),
  updated_at timestamp default now()
);
--;;
create unique index oss_developer_name_index on oss_developer(name);
--;;
create unique index oss_developer_mobile_index on oss_developer(mobile);
--;;
create unique index oss_developer_email_index on oss_developer(email);
