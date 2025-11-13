package rolflectionlib.inheritor;

public abstract class ClassMethodHook implements MethodHook {
    @Override
    public abstract Object[] runBefore(Object... methodParams);

    @Override
    public abstract Object runAfter(Object returnValue);

    @Override
    public final Object runInterface(Object... params) {
        throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
    }
}
