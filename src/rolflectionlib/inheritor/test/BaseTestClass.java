package rolflectionlib.inheritor.test;

import java.util.Map;

import rolflectionlib.inheritor.Inherit;

public class BaseTestClass {
    private int value;
    private Map<Object, Object> map;
    private String str;

    public BaseTestClass(int initial, String str, Map<Object, Object> map) {
        value = initial;
        this.map = map;
        this.str = str;
    }

    public int add(int x, int y) {
        Inherit.print("Original add called with", x, y);
        return value + x + y;
    }
    
    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get() {
        return this.map.get(str);
    }

    public String getString() {
        return this.str;
    }

    public void increment() {
        Inherit.print("Original increment called");
        value++;
    }

    public int getValue() {
        Inherit.print("Original getValue called");
        return this.value;
    }
}