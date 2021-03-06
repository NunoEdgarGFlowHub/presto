====================
Black Hole Connector
====================

The Black Hole connector works like the ``/dev/null`` device on Unix-like
operating systems for data writing and like ``/dev/null`` or ``/dev/zero``
for data reading. Metadata for any tables created via this connector
is kept in memory on the coordinator and discarded when Presto restarts.
Created tables are by default always empty, and any data written to them
will be ignored and data reads will return no rows.

During table creation, a desired rows number can be specified.
In such case, writes will behave in the same way, but reads will
always return specified number of some constant rows.
You shouldn't rely on the content of such rows.

.. warning::

    This connector will not work properly with multiple coordinators,
    since each coordinator will have a different metadata.

Configuration
-------------

To configure the Black Hole connector, create a catalog properties file
``etc/catalog/blackhole.properties`` with the following contents:

.. code-block:: none

    connector.name=blackhole

Examples
--------

Create a table using the blackhole connector::

    CREATE TABLE blackhole.test.nation AS
    SELECT * from tpch.tiny.nation;

Insert data into a table in the blackhole connector::

    INSERT INTO blackhole.test.nation
    SELECT * FROM tpch.tiny.nation;

Select from the blackhole connector::

    SELECT COUNT(*) FROM blackhole.test.nation;

The above query will always return ``0``.

Create a table with constant number of rows::

    CREATE TABLE blackhole.test.nation WITH
        (splits_count = 500,
         pages_per_split = 1000,
         rows_per_page = 2000)
    AS SELECT * from tpch.tiny.nation;
    SELECT COUNT(*) FROM blackhole.test.nation;

The above query will return value ``1 000 000 000`` (500 * 1000 * 2000).
