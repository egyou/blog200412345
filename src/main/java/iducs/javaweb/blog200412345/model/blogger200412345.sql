create sequence seq_blogger200412345 increment by 1 start with 1;

create table blogger200412345
(
    id      number(11) not null primary key,
    email   varchar2(30) not null unique,
    pw      varchar2(20) not null,
    name    varchar2(30) not null,
    phone   varchar2(50),
    address varchar2(100)
);
drop sequence seq_blogger200412345;

drop table blogger200412345;

insert into blogger200412345 values(seq_blogger200412345.nextval, '200412345@office.induk.ac.kr', 'cometrue', '유응구', '01011111111', '주소');

select * from blogger200412345;