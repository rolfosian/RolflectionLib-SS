package rolflectionlib.inheritor;

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
    public abstract Object runInterface(Object... params);
    
}
