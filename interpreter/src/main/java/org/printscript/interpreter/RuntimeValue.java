package org.printscript.interpreter;

import java.math.BigDecimal;
import org.printscript.syntax.TypeName;

public sealed interface RuntimeValue permits RuntimeValue.NumberValue, RuntimeValue.StringValue, RuntimeValue.UnitValue {
    TypeName type();

    record NumberValue(BigDecimal value) implements RuntimeValue {
        @Override
        public TypeName type() {
            return TypeName.NUMBER;
        }
    }

    record StringValue(String value) implements RuntimeValue {
        @Override
        public TypeName type() {
            return TypeName.STRING;
        }
    }

    record UnitValue() implements RuntimeValue {
        public static final UnitValue INSTANCE = new UnitValue();

        @Override
        public TypeName type() {
            return null;
        }
    }
}
