-- database: presto; groups: insert; mutable_tables: datatype|created; tables: datatype
-- delimiter: |; ignoreOrder: true; 
--!
insert into ${mutableTables.datatype} select * from datatype union all select * from datatype;
select * from ${mutableTables.datatype}
--!
12|12.25|String1|1999-01-08|1999-01-08 02:05:06|true|
25|55.52|test|1952-01-05|1989-01-08 04:05:06|false|
964|0.245|Again|1936-02-08|2005-01-09 04:05:06|false|
100|12.25|testing|1949-07-08|2002-01-07 01:05:06|true|
100|99.8777|AGAIN|1987-04-09|2010-01-02 04:03:06|true|
5252|12.25|sample|1987-04-09|2010-01-02 04:03:06|true|
100|9.8777|STRING1|1923-04-08|2010-01-02 05:09:06|true|
8996|98.8777|again|1987-04-09|2010-01-02 04:03:06|false|
100|12.8788|string1|1922-04-02|2010-01-02 02:05:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|Sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5000|67.87|testing|null|2010-01-02 04:03:06|null|
6000|null|null|1987-04-06|null|true|
null|98.52|null|null|null|true|
12|12.25|String1|1999-01-08|1999-01-08 02:05:06|true|
25|55.52|test|1952-01-05|1989-01-08 04:05:06|false|
964|0.245|Again|1936-02-08|2005-01-09 04:05:06|false|
100|12.25|testing|1949-07-08|2002-01-07 01:05:06|true|
100|99.8777|AGAIN|1987-04-09|2010-01-02 04:03:06|true|
5252|12.25|sample|1987-04-09|2010-01-02 04:03:06|true|
100|9.8777|STRING1|1923-04-08|2010-01-02 05:09:06|true|
8996|98.8777|again|1987-04-09|2010-01-02 04:03:06|false|
100|12.8788|string1|1922-04-02|2010-01-02 02:05:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|Sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5748|67.87|sample|1987-04-06|2010-01-02 04:03:06|true|
5000|67.87|testing|null|2010-01-02 04:03:06|null|
6000|null|null|1987-04-06|null|true|
null|98.52|null|null|null|true|
