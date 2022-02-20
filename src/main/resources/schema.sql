drop table if exists hashs;
drop table if exists links;
drop table if exists users;

create table hashs (id bigint not null, hashs_set varchar(255)) engine=InnoDB;
create table links (id bigint not null auto_increment, full_link varchar(255), hash varchar(255),
                    short_link varchar(255), primary key (id)) engine=InnoDB;
create table users (id bigint not null auto_increment, secret_key varchar(255), primary key (id)) engine=InnoDB;
alter table hashs add constraint foreign key (id) references users (id);
