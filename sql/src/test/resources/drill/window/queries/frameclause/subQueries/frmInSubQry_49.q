SELECT *
    FROM
    ( SELECT MAX(c7) OVER W as w_max
      FROM "t_alltype.parquet"
          WINDOW W AS ( PARTITION BY c8 ORDER BY c1 RANGE BETWEEN CURRENT ROW AND CURRENT ROW )
    ) subQry
WHERE subQry.w_max > TIMESTAMP_TO_MILLIS(TIME_PARSE('1982-12-12', 'yyyy-MM-dd'))
