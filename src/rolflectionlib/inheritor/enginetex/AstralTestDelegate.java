package rolflectionlib.inheritor.enginetex;

import java.awt.Color;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.EngineSlotAPI;
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

    // ENGINE RENDERING ORDER IS DETERMINED BY THE ORDER GIVEN IN THE SHIP FILE
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
    private static final int MAX_IDX = 7;
    private static final Color CONTRAIL_GREEN = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 25);
    private static final Color CONTRAIL_RED = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 175);

    private int stepper = 0;
    private int currentTexIdsIdx = 0;

    private int incr = 1;
    private boolean forwards = true;

    public AstralTestDelegate() {
        this.engineTexOrder = Arrays.copyOf(engineTexOrderStatic, engineTexOrderStatic.length);
    }

    private boolean isFirst() {
        if (currentTexIdsIdx == 0) return true;
        for (int i = 0; i < currentTexIdsIdx; i++)
            if (engines.get(i).isActive()) return false;
        return true;
    }

    private boolean isLast() {
        if (currentTexIdsIdx == engines.size() - 1) return true;
        for (int i = currentTexIdsIdx + 1; i < engines.size(); i++)
            if (engines.get(i).isActive()) return false;
        return true;
    }

    private boolean isFirstOrLast() {
        return forwards ? isLast() : isFirst();
    }

    // this can be optimized by indexing a dataclass with color and tex order step pairs but i cant be bothered doign that for this test
    private void christmastralLights(int current) {
        for (int i = 0; i < engineSlots.size(); i++) {
            if (i != current) {
                EngineSlotAPI engine = engineSlots.get(i);
                engine.setColor(Color.GREEN);
                engine.setContrailColor(Color.GREEN);
                engine.setGlowAlternateColor(Color.GREEN);
                contrailMap.get(engine).setColor(CONTRAIL_GREEN);
            }
        }

        EngineSlotAPI engine = engineSlots.get(current);
        engine.setColor(Color.RED);
        engine.setContrailColor(Color.RED);
        engine.setGlowAlternateColor(Color.RED);
        contrailMap.get(engine).setColor(CONTRAIL_RED);
    }
    
    @Override
    public void onTexBind(int current, boolean isRolloverEngine) {
        if (Global.getCombatEngine().isPaused()) return;
        if (isRolloverEngine) stepper++;

        if (stepper == STEPS) {
            stepper = 0;

            currentTexIdsIdx += incr;

            if (isFirstOrLast()) reverse();

            if (currentTexIdsIdx > MAX_IDX) currentTexIdsIdx -= 2; // i dont like this, -2 because middle engine comes last in rendering order

            texWrapper.setTexIds(this.engineTexOrder[currentTexIdsIdx]);
            this.christmastralLights(currentTexIdsIdx);
        }
    }

    private void reverse() {
        incr = -incr;
        forwards = !forwards;
    }
}
