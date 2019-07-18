---
title: "Hive兼容"
is_beta: true
nav-parent_id: tableapi
nav-pos:9
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

随着Flink在流处理中的广泛应用，它在批处理方面也显示出了巨大潜力。如果能够改进Flink的批处理能力，尤其是从SQL方面入手，将会提供给用户一套完整的解决方案，以满足的他们流处理和批处理的需求。

从另一方面来看，Hive早已聚焦在大数据技术领域并建立了与其相关的一套完整生态系统。因此对于绝大多数用户而言，Hive不仅仅是一个用于大数据分析和ETL的SQL引擎，反而更像是一个可以用于发现数据，定义数据，演化数据的数据管理平台。所以总而言之，使用Hive已经成为了Hadoop大数据处理的一个基准。

因此，对于Flink来说，为了满足用户对于批处理和SQL的需求，与Hive生态系统的整合将是势在必行的。Flink是一种可用于替代Hive的批处理引擎。通过Flink和FlinkSQL，不管是Hive用户还是Flink用户，都可以享受到Hive丰富的SQL语法支持，完善的生态系统以及Flink强大的批处理性能。


支持的Hive版本
----------------------
目标版本: Hive `2.3.4`和`1.2.1`


Hive Metastore(元数据存储)集成
-----------------------------
Hive的元数据存储集成分为两个方面：

1. 使Hive的元数据对象（例如tables和views）可以被Flink使用，与此同时Flink也同样可以在Hive中创建这些元数据对象。该功能已经在`HiveCatalog`中被实现。

2. 将Flink的元数据对象（例如tables, views和UDFs）持久化在Hive Metastore中。该功能正在开发之中，将会在`HiveCatalog`中被实现。

对于 `HiveCatalog`的用法，详见[Catalogs]({{ site.baseurl }}/dev/table/catalog.html)


Hive数据集成
-----------------
如何通过Flink's Hive data connector使用Hive中的数据请详见[Connecting to other systems]({{ site.baseurl }}/dev/batch/connectors.html)


范例
--------

更完整的例子请详见[Hive Compatibility Example] ({{ site.baseurl }}/dev/table/hive_compatibility.html)


局限性和未来计划
--------------------
目前Flink's Hive data connector不支持分区写入，该功能正在开发中。
