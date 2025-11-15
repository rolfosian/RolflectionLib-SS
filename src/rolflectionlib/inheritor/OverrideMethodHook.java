package rolflectionlib.inheritor;

/**
 * Corresponds to methods where <code>superCall</code> specified in {@link MethodData} is false 
 */
public abstract class OverrideMethodHook implements MethodHook {
    @Override
    public final Object[] runBefore(Object... methodParams) {
        throw new UnsupportedOperationException("Unimplemented method 'runBefore'");
    }

    @Override
    public final Object runAfter(Object returnValue) {
        throw new UnsupportedOperationException("Unimplemented method 'runAfter'");
    }

    @Override
    public final Object runInterface(Object... params) {
        throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
    }
    /**
     * Class method full override hook. For methods that are specified in their MethodData to not call the super method
     * @param params Super method parameters
     * @return The final return value of the inheritor class method
     */
    public abstract Object runFullOverride(Object... params);
}
