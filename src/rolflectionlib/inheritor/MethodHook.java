package rolflectionlib.inheritor;

public interface MethodHook {
    /**
     * Hook that runs before the super class method is called. Takes the main method parameters.
     * @param methodParams The method's parameters, intercepted by the inheritor class before the super method is called.
     * @return An array that should contain the possibly altered params in the same order as given so they can then be passed to the super method
     */
    public Object[] runBefore(Object... methodParams);

    /**
     * Hook that runs after the super class method is called. Takes the super method's return value as its parameter.
     * @param returnValue The return value of the super method
     * @return The final return value of the inheritor class method
     */
    public Object runAfter(Object returnValue);

    /**
     * Interface method hook. Isn't used for non-interface methods.
     * @param params The interface method parameters
     * @return Return value for the interface method
     */
    public Object runInterface(Object... params);

    /**
     * Class method full override hook. For methods that are specified in their MethodData to not call the super method
     * @param params Super method parameters
     * @return The final return value of the inheritor class method
     */
    public Object runFullOverride(Object... params);
}
