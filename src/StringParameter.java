/**
 * Created by mdaigle on 5/20/17.
 */
public class StringParameter extends Parameter{
    String value;
    ParameterType type;

    public StringParameter(String value) {
        this.value = value;
        this.type = ParameterType.STRING;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public ParameterType getType() {
        return this.type;
    }
}
