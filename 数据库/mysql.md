## 1. 数据库(datebase)

保存有组织的数据的容器，简单理解为存放数据的仓库





## 2. 数据库管理系统(database manage system)

我们平常不是直接从数据库里面获取数据的，而是通过使用数据库管理系统来访问数据库从而获取数据的，这些软件称为DBMS（由于习惯我们平时所说的数据库指的就是数据库管理系统，容易被误导）





## 3. 表(table)

类似于文件夹，把数据分类放在同一个文件内，即放在同一个表中





## 4. 列和行(column and row)

表的结构类似于excel的表格，表列对应excel列，表行对应excel行

![1576386898426](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1576386898426.png)





## 5. 数据类型(data type)

表中的每列(字段)都有自己的数据类型

```mysql
# 常见数据类型
int
float
double
char
varchar
datetime
timestamp
TEXT
```





## 6. 三大范式

第一范式：每个字段都是最小的单元，不可再分
第二范式：满足第一范式，表中的字段必须完全依赖于全部主键而非部分主键
第三范式：满足第二范式，非主键外的所有字段必须互不依赖





## 7. 约束

NOT NULL: 非空
UNIQUE: 唯一性
PRIMARY KEY: 主键
FOREIGN KEY: 外键
CHECK: 控制字段的值范围





## 8. 超键、候选键、主键、外键

主键：唯一标识数据的单个或多个字段

外键：存在他表中的主键

候选键：可以作为主键但没有设为主键的单个或多个字段

超键：候选键的集合





## 9. 表操作

```mysql
# 建表
CREATE TABLE [IF NOT EXISTS] `runoob_tbl`(
   `runoob_id` INT UNSIGNED AUTO_INCREMENT,
   `runoob_title` VARCHAR(100) NOT NULL,
   `runoob_author` VARCHAR(40) NOT NULL,
   `submission_date` DATE,
   PRIMARY KEY ( `runoob_id` )
)ENGINE = InnoDB DEFAULT CHARSET = utf8;
```

```mysql
# 删表
DROP TABLE [IF EXISTS] <表名>
# 清空
TRUNCATE TABLE <表名>
```





## 10. SELECT语句

基本查询

```mysql
SELECT (字段1, 字段2, ...) FROM <表名>
```

条件查询

```mysql
SELECT (字段1, 字段2, ...) FROM <表名> WHERE <条件表达式>
```

排序查询

```mysql
SELECT (字段1, 字段2, ...) FROM <表名> ORDER BY DESC 字段1 DESC, 字段2 ASC, ...
```

分页查询

```mysql
SELECT (字段1, 字段2, ...) FROM <表名> LIMIT <M> OFFSET <N>

# 分页优化，查询第十万条后的100条数据
SELECT * FROM <表名> LIMIT 100 OFFSET 100000;
SELECT * FROM <表名> WHERE id >= (SELECT id FROM <表名> LIMIT 100000,1) limit 100;
```

子查询

```mysql
SELECT * FROM (SELECT * FROM <表名>) AS <另取的表名> WHERE <条件表达式>
```

分组查询

```mysql
SELECT (字段1，聚合函数) FROM <表名>
WHERE <条件表达式>
GROUP BY 字段1
HAVING <条件表达式>
ORDER BY <条件表达式>

# WHERE，用于分组前，WHERE过滤的是行
# SELECT，字段只能加 GROUP BY 后面的字段和聚合函数 
# HAVING，用于分组后，过滤组，且条件字段必须在前面查询存在

# 画重点，分组需要列出非GROUP 之后的字段
SELECT * FROM <表名> WHERE id IN （
	SELECT max(id) FROM <表名> GROUP BY id,time
）
```

并 / 差 / 交集查询

```mysql
SELECT (字段1, 字段2, ...) FROM <表名1>
UNION [ALL] / EXCEPT / INTERSECT
SELECT (字段1, 字段2, ...) FROM <表名2>

# 二表字段需一样
```

聚合查询

```mysql
COUNT(字段)，SUM(),AVG(),MAX(),MIN()

# 若查询无结果，COUNT()返回0，而SUM()、AVG()、MAX()、MIN()返回NULL
```

连接查询

```mysql
SELECT (表1.字段, 表2.字段2, ...) FROM <表1> XXX JOIN <表2> ON <表1>.column = <表2>.column

XXX: INNER / RIGHT OUTER / LEFT OUTER / FULL OUTER
```

* 内连接，只返回同时存在于两张表的行数据

* 外连接

  * 左连接，返回右表都存在的行，左边不存在填充NULL

  * 右连接，返回左表都存在的行，右边不存在填充NULL

  * 全连接，把两张表的所有记录全部选择出来，自动把对方不存在的列填充为NULL






## 11. INSERT语句

```mysql
# 插入或替换（根据主键来执行）
# 若存在该主键，删除原记录，插入一条新的、否则直接插入记录
REPLACE INTO <表名> （字段） VALUES （值）
```

```mysql
# 插入或更新（根据主键来执行）
# 若存在该主键，更新记录、否则直接插入记录
# 返回0，1，2
INSERT INTO <表名> (字段1) VALUES (值) ON DUPLICATE KEY UPDATE `字段2` = "更新值"
```

```mysql
# 插入或忽略
INSERT IGNORE INTO <表名> （字段） VALUES （值） 
```







## 12. UPDATE

```mysql
# 返回更新条数
UPDATE <表名> SET 字段1=值1, 字段2=值2, ... WHERE <条件表达式>
```







## 13. DELETE

```mysql
# 返回条数
DELETE FROM <表名> WHERE <条件表达式>
```

```mysql
# 跨表更新（两个表都可更新）
UPDATE <表名1> XXX JOIN <表名2> on <表名1>.a = <表名2>.b SET <表名1>.x = 0,<表名2>.x=0 WHERE <条件表达式>
```





## 14. 视图

```mysql
# 视图是一种虚表，建立在原来的表上，其本质是查询语句，不会增加查询效率
# 简化查询
# 权限限制，安全性
CREATE VIEW <视图名> SELECT (字段1, 字段2, ...) FROM <表名> WHERE <条件表达式>
```









## 15. 名词

* DML（data manipulation language）数据操作语言：select、update

* DDL（data definition language）数据定义语言：create、alter、drop

* DCL（data control language）数据控制语言：grant、revoke






## 16. 其他

1. 判断null：ISNULL
2. 替换：IFNULL(`字段`，`替换的值`)，若为null则替换成给定值