package rolflectionlib.inheritor.enginetex;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;

import rolflectionlib.inheritor.enginetex.EngineTexDelegate.EngineTexType;

public class Test {
    public static void run() {
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (ship.getOwner() == 0) {
                EngineTex.setEngineTextures(ship, 
                    new AstralTestDelegate(EngineTexType.GLOW_AND_SMOOTH_GLOW)
                );
            }
        }
    }

    public static void run(boolean with10CR) {
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (ship.getOwner() == 0) {
                EngineTex.setEngineTextures(ship,
                    new AstralTestDelegate(EngineTexType.GLOW_AND_SMOOTH_GLOW)
                );
                if (with10CR) ship.setCurrentCR(0.1f);
                return;
            }
        }
    }
}
