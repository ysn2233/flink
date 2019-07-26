---
title: "Hive集成范例"
is_beta: true
nav-parent_id: examples
nav-pos: 21
---
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->


下面的例子会展现如何使用Flink SQL Client去与Hive的元数据和Table进行交互。

## 环境

假设工作环境可以访问所有的服务器节点，且以下组件已经被正确安装配置。

- Hadoop Cluster (HDFS + YARN)
- Hive 2.3.4
- Flink cluster

## 搭建Flink环境

### 以yarn-session模式搭建Flink集群环境

启动一个yarn-session

{% highlight bash %}
$ ./bin/yarn-session.sh -n 4 -qu root.default -s 4 -tm 2048 -nm test_session_001
{% endhighlight %}

### 以local模式搭建Flink集群环境

简单运行

{% highlight bash %}
$ ./bin/start-cluster.sh
{% endhighlight %}

### 配置Flink SQL CLI

下面将会设置的Flink SQL CLI的yaml配置文件。对于Flink SQL CLI和catlogs的详细说明，可参见[Flink SQL CLI]({{ site.baseurl }}/dev/table/sqlClient.html)与 [Catalogs]({{ site.baseurl }}/dev/table/catalog.html).

{% highlight yaml %}
execution:
    # 使用batch模式
    type: batch
    # 使用table result模式
    result-mode: table
    time-characteristic: event-time
    periodic-watermarks-interval: 200
    parallelism: 1
    max-parallelism: 12
    min-idle-state-retention: 0
    max-idle-state-retention: 0
    current-catalog: myhive
    current-database: default

deployment:
  response-timeout: 5000

catalogs:
   - name: myhive
   type: hive
   property-version: 1
   hive-conf-dir: /opt/hive-conf
   hive-version: 2.3.4
   
{% endhighlight %}


需要注意的是，如果用户使用Flink yarn-session模式，将会获取一个sessionId将它视为`\${appId}`。然后请在`conf/sql-client-defaults.yaml`文件中的`deployment`条目中设置`yid: ${appId}`.

如果用户使用Flink local模式，则不需要做其余配置。

请确认所有依赖的jar包被放入`/lib`文件夹，包括`flink-connector-hive`,`flink-hadoop-compatibility`,`flink-shaded-hadoop-2-uber`以及Hive相关的jar包。详见[Catalogs]({{ site.baseurl }}/dev/table/catalog.html)

通过下面的命令启动Flink SQL CLI

{% highlight bash %}
$ ./bin/sql-client.sh embedded
{% endhighlight %}

## Flink SQL Client Example

### Example 1

#### Hive准备

假设Hive已经成功的安装和运行在环境中，让我们为它准备一些数据做示例。

首先，我们定位到Hive当前的数据库（在例子中为`default`），并确保当前该数据库中没有任何的表。

{% highlight bash %}
hive> show databases;
OK
default
Time taken: 0.841 seconds, Fetched: 1 row(s)

hive> show tables;
OK
Time taken: 0.087 seconds
{% endhighlight %}

随后，我们创建一张简单表，表中有两列：name字段类型为string, value字段类型为double，该表以文本文件的形式存储，每行由','进行分隔。

{% highlight bash %}
hive> CREATE TABLE mytable(name string, value double) row format delimited fields terminated by ',' stored as textfile;
OK
Time taken: 0.127 seconds
{% endhighlight %}

紧接着，我们通过Hive在`default`数据库中创建一张名为`mytable`的表

然后让我们读取一些数据到`mytable`中。下面是我们准备读入的数据，假设文件路径为'/tmp/data.txt'。

{% highlight txt %}
Tom,4.72
John,8.00
Tom,24.2
Bob,3.14
Bob,4.72
Tom,34.9
Mary,4.79
Tiff,2.72
Bill,4.33
Mary,77.7
{% endhighlight %}

通过下面的命令读取和检验数据：

{% highlight bash %}
hive> load data local inpath '/tmp/data.txt' into table mytable;
Loading data to table default.mytable
OK
Time taken: 0.324 seconds

hive> select * from mytable;
OK
Tom	4.72
John	8.0
Tom	24.2
Bob	3.14
Bob	4.72
Tom	34.9
Mary	4.79
Tiff	2.72
Bill	4.33
Mary	77.7
Time taken: 0.097 seconds, Fetched: 10 row(s)
{% endhighlight %}

#### 通过Flnk SQL CLI获取Hive的metadata和数据

在Flink SQL CLI中，我们可以开始查询Hive metadata.

{% highlight bash %}

Flink SQL> show catalogs;
myhive
default_catalog

# ------ 设置当前的catlog为'myhive' catalog（如果你没有在yaml文件中设置它） ------

Flink SQL> use catalog myhive;

# ------ 获取所有已经在'mytable' catalog中注册的数据库 ------

Flink SQL> show databases;
default

# ------ 获取之间注册的'mytable`表 ------

Flink SQL> show tables;
mytable

# ------ 我们可以发现Flink获取到的表结构与我们在Hive中创建的完全相同 - string类型的name以及double类型的value ------
Flink SQL> describe mytable;
root
 |-- name: name
 |-- type: STRING
 |-- name: value
 |-- type: DOUBLE


Flink SQL> select * from mytable;

      name      value
__________ __________

      Tom        4.72
      John	     8.0
      Tom	     24.2
      Bob	     3.14
      Bob	     4.72
      Tom	     34.9
      Mary	     4.79
      Tiff	     2.72
      Bill	     4.33
      Mary	     77.7

Flink SQL> select * from mytable;
{% endhighlight %}


### Example 2

通过上面所有的步骤，我们为Hive准备了另外两张表，order_details和products，他们在Hive SQL ClI的表结构为：

{% highlight bash %}
Hive> describe order_details;
OK
orderid               bigint
productid             bigint
unitprice             double
quantity              int
discount              double

Hive> describe products;
OK
productid             bigint
productname           string
supplierid            bigint
categoryid            bigint
quantityperunit       string
unitprice             double
unitsinstock          bigint
unitsonorder          bigint
reorderlevel          int
discontinued          int
);
{% endhighlight %}

让我们运行几条SQL语句去获取Hive的数据

{% highlight sql %}
Flink SQL> select * from products;

Flink SQL> select count(*) from order_details;

Flink SQL> select
   t.productid,
   t.productname,
   sum(t.price) as sale
from
  (select
      A.productid,
      A.productname as productname,
        B.unitprice * discount as price
     from
      products as A, order_details as B
     where A.productid = B.productid) as t
  group by t.productid, t.productname;

{% endhighlight %}

Flink Table API Example
--------------------------

待续