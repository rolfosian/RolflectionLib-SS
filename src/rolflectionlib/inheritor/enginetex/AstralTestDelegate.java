package rolflectionlib.inheritor.enginetex;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.EngineSlotAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;

import rolflectionlib.util.Misc;
import rolflectionlib.util.TexReflection;

public class AstralTestDelegate extends EngineTexDelegate {
    // TEST DELEGATE FOR ASTRAL CARRIER ENGINES

    private static final Logger logger = Global.getLogger(EngineTex.class);
    private static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    // this can probably be automated with a bit of math but i dont into that
    private static int[][] engineTexOrderStatic = TexReflection.getTexOrder(
        new String[][] {
            { // STEP ONE

                // Far Left Pair
                "graphics/fx/beamfringed.png", 
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },
            
            { // STEP TWO

                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamfringed.png", 

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP THREE
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamfringed.png", 
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP FOUR
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamfringed.png", 

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP FIVE
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamfringed.png", 
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP SIX
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamfringed.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP SEVEN
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamfringed.png",
                "graphics/fx/beamcoreb.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },

            { // STEP EIGHT
                
                // Far Left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle left Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Middle Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamcoreb.png",

                // Far Right Pair
                "graphics/fx/beamcoreb.png",
                "graphics/fx/beamfringed.png",

                "graphics/fx/slipstream_map3.png" // Middle engine comes last for Astral
            },
            // No ninth step because we keep middle engine static
        }
    );

    private static final int STEPS = 5;
    private int stepper = 0;
    private int currentTexIdsIdx = 0;

    private int incr = 1;

    private boolean forwards = true;
    private boolean hasMoved = false;

    public AstralTestDelegate() {
        this.engineTexOrder = Arrays.copyOf(engineTexOrderStatic, engineTexOrderStatic.length);
    }

    private boolean isFirstOrLastEngine(int engineIdx) {
        if (forwards) {
            ShipEngineAPI engine = engines.get(engineIdx);

            if (engine.isActive() && engineIdx == 0) return true;
            
            for (int i = engineIdx + 1; i < engines.size(); i++) {
                if (engines.get(i).isActive()) return false;
            }

            return true;

        } else {
            ShipEngineAPI engine = engines.get(engineIdx);

            if (engine.isActive() && engineIdx == engines.size() - 1) return true;

            for (int i = 0; i < engineIdx; i++) {
                if (engines.get(i).isActive()) return false;
            }

            return true;
        }
    }
    
    @Override
    public void onTexBind(int current, boolean isRolloverEngine) {
        if (Global.getCombatEngine().isPaused()) return;
        if (isRolloverEngine) stepper++;

        if (stepper == STEPS) {
            stepper = 0;

            // if (isFirstOrLastEngine(current)) this.reverse(); // this doesnt work

            currentTexIdsIdx = (currentTexIdsIdx + incr) % engineTexOrder.length;

            texWrapper.setTexIds(this.engineTexOrder[currentTexIdsIdx]);
        }
    }

    private void reverse() { // this doesnt work
        incr = -incr;
        forwards = !forwards;
    }
}
