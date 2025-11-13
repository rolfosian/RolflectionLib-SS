package rolflectionlib.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;

import java.lang.Cloneable;

public class Cloner {
    private static final Map<Class<?>, Object> cloneMethodMap = new HashMap<>();

    static {
        try {
            for (Class<?> cls : ObfuscatedClasses.getClasses()) {
                List<Class<?>> interfaces = Arrays.asList(cls.getInterfaces());
    
                if (interfaces.contains(Cloneable.class)) {
                    cloneMethodMap.put(cls, cls.getMethod("clone"));
                } 
    
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object clone(Object toClone) {
        return RolfLectionUtil.invokeMethodDirectly(cloneMethodMap.get(toClone.getClass()), toClone);
    }

    public ShipAPI clone(ShipAPI ship) {
        return (ShipAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.shipCloneMethod, ship);
    }

    public ShipHullSpecAPI clone(ShipHullSpecAPI hullspec) {
        return (ShipHullSpecAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.hullSpecCloneMethod, hullspec);
    }

    // idk if this is even a thing
    public ShipVariantAPI clone(ShipVariantAPI variant) {
        return (ShipVariantAPI) RolfLectionUtil.invokeMethodDirectly(cloneMethodMap.get(variant.getClass()), variant);
    }

    // idk if this is even a thing
    public WeaponSpecAPI clone(WeaponSpecAPI weaponSpec) {
        return (WeaponSpecAPI) null;
    }

    // idk if this is even a thing
    public SectorEntityToken clone(SectorEntityToken token) {
        return (SectorEntityToken) RolfLectionUtil.invokeMethodDirectly(cloneMethodMap.get(token.getClass()), token);
    }

    protected static void init() {}
}
