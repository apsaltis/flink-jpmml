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

package io.radicalbit.flink.pmml.java;

import io.radicalbit.flink.pmml.java.strategies.MissingValueStrategy;
import io.radicalbit.flink.pmml.java.strategies.PreparationErrorStrategy;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.Model;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class wrapping a raw input in the form of a Map<String,Object>. It contains the input preparation logic.
 */

public class RawInput {

    private final Map<String, Object> inputData;

    public RawInput(Map<String, Object> data) {
        inputData = data;
    }

    /**
     * Prepares a raw input using a JPMML evaluator and adapting its behaviour according to the
     * strategies used.
     *
     * @param evaluator
     * @param preparationErrorStrategy This strategy handles the behaviour in case of exceptions in the input preparation.
     * @param missingValueStrategy     This strategy handles the case of a missing value in the input. It triggers everytime
     *                                 a specific key is expected and not present. It also triggers when the key is present
     *                                 but the value is null.
     * @return
     * @throws Exception
     */
    public final PreparedInput prepare(ModelEvaluator<? extends Model> evaluator,
                                 PreparationErrorStrategy preparationErrorStrategy,
                                 MissingValueStrategy missingValueStrategy
    ) throws Exception {

        final Map<FieldName, FieldValue> preparedData = new LinkedHashMap<FieldName, FieldValue>();

        final List<InputField> activeFields = evaluator.getActiveFields();
        for (InputField activeField : activeFields) {

            Object rawValue;
            FieldValue activeValue;

            rawValue = inputData.get(activeField.getName().getValue());

            if (rawValue == null) {
                rawValue = missingValueStrategy.handleError(activeField.getName(), evaluator);
            }

            try {
                activeValue = EvaluatorUtil.prepare(activeField, rawValue);
            } catch (Exception e) {
                activeValue = preparationErrorStrategy.handleError(activeField.getName(), rawValue, evaluator, e);
            }

            preparedData.put(activeField.getName(), activeValue);
        }
        return new PreparedInput(preparedData, evaluator);
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }
}
