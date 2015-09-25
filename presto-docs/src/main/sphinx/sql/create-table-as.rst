===============
CREATE TABLE AS
===============

Synopsis
--------

.. code-block:: none

    CREATE TABLE table_name AS query 
    [ WITH [ NO ] DATA ]

Description
-----------

Create a new table containing the result of a :doc:`select` query.
Use :doc:`create-table` to create an empty table.

Examples
--------

Create a new table ``orders_by_date`` that summarizes ``orders``::

    CREATE TABLE orders_by_date AS
    SELECT orderdate, sum(totalprice) AS price
    FROM orders
    GROUP BY orderdate

Create a new ``empty_nation`` table with the same schema as ``nation`` and no data::

    CREATE TABLE empty_nation AS
    SELECT * 
    FROM nation 
    WITH NO DATA
