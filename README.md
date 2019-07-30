# 社区demo

##资料
[调用github登录接口,Github OAuth](https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/)

##脚本
```sql
create table USER
(
    ID           INTEGER default  auto_increment,
    ACCOUNT_ID   VARCHAR(100),
    NAME         VARCHAR(50),
    TOKEN        CHAR(36),
    GMT_CREATE   BIGINT,
    GMT_MODIFIED BIGINT,
    constraint USER_PK
        primary key (ID)
);
```