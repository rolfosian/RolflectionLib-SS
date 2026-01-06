package rolflectionlib.plugins;

import java.util.List;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.input.InputEventAPI;

public class RolfLectionLibCombatPlugin extends BaseEveryFrameCombatPlugin {
    public static int frame;
    
    @Override
    public void advance(float arg0, List<InputEventAPI> arg1) {
        frame = (frame + 1) % 10;
    }
}
