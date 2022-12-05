create sequence seq_blog200412345 increment by 1 start with 1;

create table blog200412345
(
    id  number(11) not null primary key,
    name    varchar2(30) not null,
    email   varchar2(30) not null,
    title   varchar2(50),
    content varchar2(100)
);
drop sequence seq_blog200412345;
drop table blog200412345;

insert into blog200412345 values(seq_blog200412345.nextval, '유응구', '200412345@office.induk.ac.kr', '제목', '내용');

update blog200412345 set name='강아지', email='dog@dog', title='인덕대학교', content='멍멍' where id='2';

/*
update blog200412345 set name=?, email=?, title=?, content=? where id=?;
*/

select * from blog200412345;