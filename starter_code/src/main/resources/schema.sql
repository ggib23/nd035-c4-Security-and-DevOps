CREATE TABLE IF NOT EXISTS ITEM(
    id bigint not null auto_increment,
    name varchar(100),
    price float,
    description varchar(255),
    primary key (id)
);