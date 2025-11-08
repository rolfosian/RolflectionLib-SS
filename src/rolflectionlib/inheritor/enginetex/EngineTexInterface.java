package rolflectionlib.inheritor.enginetex;

public interface EngineTexInterface {
    public void setTexIds(int[] texIds);
    public int[] getTexIds();
    public void setDelegate(EngineTexDelegate delegate);
    public EngineTexDelegate getDelegate();
}
