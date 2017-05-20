import java.lang.reflect.Type;

/**
 * Created by mdaigle on 5/17/17.
 */
public abstract class Parameter {
    enum ParameterType {
        STRING, INTEGER, PREDICATE
    }

    abstract Object getValue();

    abstract ParameterType getType();

    public static Type getClassForType(ParameterType type) {
        switch (type) {
            case STRING:
                return StringParameter.class;
            default:
                return null;
        }
    }
}
