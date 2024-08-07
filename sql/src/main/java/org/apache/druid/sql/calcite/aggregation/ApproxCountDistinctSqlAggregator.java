/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.sql.calcite.aggregation;

import org.apache.calcite.rel.core.AggregateCall;
import org.apache.calcite.sql.SqlAggFunction;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Optionality;
import org.apache.druid.sql.calcite.planner.PlannerContext;
import org.apache.druid.sql.calcite.rel.InputAccessor;
import org.apache.druid.sql.calcite.rel.VirtualColumnRegistry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation for APPROX_COUNT_DISTINCT, and for COUNT(DISTINCT expr) when {@code useApproximateCountDistinct}
 * is enabled.
 *
 * In production, the delegate is chosen based on the value of
 * {@link org.apache.druid.sql.guice.SqlModule#PROPERTY_SQL_APPROX_COUNT_DISTINCT_CHOICE}.
 */
public class ApproxCountDistinctSqlAggregator implements SqlAggregator
{
  private static final String NAME = "APPROX_COUNT_DISTINCT";
  private final SqlAggFunction delegateFunction;
  private final SqlAggregator delegate;

  public ApproxCountDistinctSqlAggregator(final SqlAggregator delegate)
  {
    this.delegate = delegate;
    this.delegateFunction = new ApproxCountDistinctSqlAggFunction(delegate.calciteFunction());
  }

  @Override
  public SqlAggFunction calciteFunction()
  {
    return delegateFunction;
  }

  @Nullable
  @Override
  public Aggregation toDruidAggregation(
      PlannerContext plannerContext,
      VirtualColumnRegistry virtualColumnRegistry,
      String name,
      AggregateCall aggregateCall,
      InputAccessor inputAccessor,
      List<Aggregation> existingAggregations,
      boolean finalizeAggregations
  )
  {
    return delegate.toDruidAggregation(
        plannerContext,
        virtualColumnRegistry,
        name,
        aggregateCall,
        inputAccessor,
        existingAggregations,
        finalizeAggregations
    );
  }

  private static class ApproxCountDistinctSqlAggFunction extends SqlAggFunction
  {
    ApproxCountDistinctSqlAggFunction(SqlAggFunction delegate)
    {
      super(
          NAME,
          null,
          SqlKind.OTHER_FUNCTION,
          ReturnTypes.explicit(SqlTypeName.BIGINT),
          delegate.getOperandTypeInference(),
          delegate.getOperandTypeChecker(),
          delegate.getFunctionType(),
          false,
          false,
          Optionality.FORBIDDEN
      );
    }
  }
}
