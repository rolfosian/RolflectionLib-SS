package rolflectionlib.inheritor.test;

import java.util.List;
import java.util.Map;

public class BaseTestClass {
    private int value;
    private Map<Object, Object> map;
    private String str;

    public BaseTestClass(int initial, String str, Map<Object, Object> map) {
        value = initial;
        this.map = map;
        this.str = str;
    }

    public String manyParams(String arg0, String arg1, String arg2, List<?> arg3, Map<?, ?> arg4, int arg5, long arg6, short arg7, double arg8) {
        return arg0;
    }

    public String oneParamReturnValue1(String arg) {
        return arg;
    }

    public String oneParamReturnValue2(String arg) {
        return arg;
    }

    public void oneParamVoid1(List<Object> arg) {
        return;
    }
    
    public void oneParamVoid2(List<Object> arg) {
        return;
    }

    public int twoParamReturnValue1(int x, int y) {
        return value + x + y;
    }

    public int twoParamReturnValue2(int x, int y) {
        return value + x + y;
    }
    
    public void twoParamVoid1(String key, Object value) {
        map.put(key, value);
    }

    public void twoParamVoid2(String key, Object value) {
        map.put(key, value);
    }

    public Object noParamReturnValue1() {
        return this.map.get(str);
    }

    public Object noParamReturnValue2() {
        return this.map.get(str);
    }

    public void noParamVoid1() {
        
    }

    public void noParamVoid2() {
        
    }
}