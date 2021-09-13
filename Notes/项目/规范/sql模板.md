```
create table if not exists table_name(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    XXXXX
    XXXXX
    XXXXX
    create_time timestamp NOT NULL,
    modify_time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    XXXXX
);ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT
CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

索引命名规范

* 普通索引

  idx_字段名

* 唯一索引

  uk_字段名1
