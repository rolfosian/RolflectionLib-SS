package rolflectionlib.inheritor.test;

import rolflectionlib.inheritor.Inherit;

public class BaseTestClass {
    private int value;

    public BaseTestClass(int initial) {
        value = initial;
    }

    public int add(int x, int y) {
        Inherit.print("Original add called with", x, y);
        return value + x + y;
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