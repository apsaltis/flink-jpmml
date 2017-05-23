/*
 *
 * Copyright (c) 2017 Radicalbit
 *
 * This file is part of flink-JPMML
 *
 * flink-JPMML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * flink-JPMML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with flink-JPMML.  If not, see <http://www.gnu.org/licenses/>.
 *         
 */

package io.radicalbit.flink.pmml.java.strategies;

import io.radicalbit.flink.pmml.java.InputPreparationException;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.ModelEvaluator;


/**
 * This strategy just re-throws the exception raised underneath.
 */
public final class PropagateExceptionStrategy implements MissingValueStrategy, PreparationErrorStrategy {

    public PropagateExceptionStrategy() {
    }

    @Override
    public final Object handleError(FieldName fieldName, ModelEvaluator evaluator) throws Exception {
        throw new InputPreparationException("Field named " + fieldName.getValue() + " is missing.");
    }

    @Override
    public FieldValue handleError(FieldName name, Object rawValue, Evaluator evaluator, Exception e) throws Exception {
        throw e;
    }
}
