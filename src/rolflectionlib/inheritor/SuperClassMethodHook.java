package rolflectionlib.inheritor;

/**
 *<code>SuperClassMethodHook</code> corresponds with regular class methods. Sandwiches a super call between the two hooks given. Both parameters and final return values may be altered at each stage.</p><p>
 */
public abstract class SuperClassMethodHook implements MethodHook {
    /**
     * Hook that runs before the super class method is called. Takes the main method parameters.
     * @param methodParams The method's parameters, intercepted by the inheritor class before the super method is called.
     * @return An array that should contain the possibly altered params in the same order as given so they can then be passed to the super method
     */
    @Override
    public abstract Object[] runBefore(Object... methodParams);
    
    /**
     * Hook that runs after the super class method is called. Takes the super method's return value as its parameter.
     * @param returnValue The return value of the super method
     * @return The final return value of the inheritor class method
     */
    @Override
    public abstract Object runAfter(Object returnValue);

    @Override
    public final Object runInterface(Object... params) {
        throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
    }

    @Override
    public final Object runFullOverride(Object... params) {
        throw new UnsupportedOperationException("Unimplemented method 'runFullOverride'");
    }
}
