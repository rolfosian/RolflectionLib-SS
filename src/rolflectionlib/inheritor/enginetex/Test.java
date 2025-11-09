package rolflectionlib.inheritor.enginetex;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;

public class Test {
    public static EngineTexInterface engineTex = null;

    public static void run() {
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (ship.getOwner() == 0) {
                engineTex = EngineTex.setEngineTextures(ship, new AstralTestDelegate());
            }
        }
    }

    public static void run(boolean with10CR) {
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (ship.getOwner() == 0) {
                engineTex = EngineTex.setEngineTextures(ship, new AstralTestDelegate());
                if (with10CR) ship.setCurrentCR(0.1f);
                return;
            }
        }
    }
}
