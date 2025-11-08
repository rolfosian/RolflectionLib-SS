package rolflectionlib.plugins;

import java.util.*;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import rolflectionlib.inheritor.enginetex.EngineTex;
import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.ListenerFactory;
import rolflectionlib.util.ObfuscatedClasses;
import rolflectionlib.util.RolfLectionUtil;
import rolflectionlib.util.TexReflection;

public class RolfLectionLibPlugin extends BaseModPlugin {
    private Logger logger = Global.getLogger(RolfLectionLibPlugin.class);
    
    public static int frame = 0;

    @Override
    public void onApplicationLoad() {
        ObfuscatedClasses.init();
        RolfLectionUtil.init();

        ClassRefs.findAllClasses();
        ListenerFactory.init();
        TexReflection.init();
        EngineTex.init();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientListener(new RolfLectionLibListener(false));
    }

    @Override
    public void onNewGame() {

    }
}
