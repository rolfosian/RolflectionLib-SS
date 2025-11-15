package rolflectionlib.inheritor;

/**
 * 
 */
public abstract class InterfaceMethodHook implements MethodHook {
    @Override
    public final Object[] runBefore(Object... methodParams) {
        throw new UnsupportedOperationException("Unimplemented method 'runBefore'");
    }

    @Override
    public final Object runAfter(Object returnValue) {
        throw new UnsupportedOperationException("Unimplemented method 'runAfter'");
    }

    @Override
    public final Object runFullOverride(Object... params) {
        throw new UnsupportedOperationException("Unimplemented method 'runFullOverride'");
    }
    /**
     * Interface method hook. Isn't used for non-interface methods.
     * @param params The interface method parameters
     * @return Return value for the interface method
     */
    @Override
    public abstract Object runInterface(Object... params);
    
}
