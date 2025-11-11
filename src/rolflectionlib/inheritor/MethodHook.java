package rolflectionlib.inheritor;

public interface MethodHook {
    public Object[] runBefore(Object... methodParams);
    public Object runAfter(Object returnValue);
}
