package rolflectionlib.inheritor.test;

public interface BaseTestInterface {
    public String interfaceOneParamReturnValue(String arg);
    public void interfaceOneParamVoid(int arg0);
    public int interfaceTwoParamReturnValue(int x, int y);
    public void interfaceTwoParamVoid(String key, Object value);
    public Object interfaceNoParamReturnValue();
    public void interfaceNoParamVoid();
}
