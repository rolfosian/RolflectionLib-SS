package rolflectionlib.plugins;

import java.util.*;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.loading.SpecStore;

import rolflectionlib.inheritor.enginetex.EngineTex;
import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.ListenerFactory;
import rolflectionlib.util.Misc;
import rolflectionlib.util.ObfuscatedClasses;
import rolflectionlib.util.RolfLectionUtil;
import rolflectionlib.util.TexReflection;

@SuppressWarnings("unchecked")
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

        // TODO account for different saves
        // for (Object method : SpecStore.class.getDeclaredMethods()) {
        //     if (RolfLectionUtil.getReturnType(method).equals(Map.class)) {
        //         Misc.DESCRIPTION_MAP = (Map<String, Object>) RolfLectionUtil.invokeMethodDirectly(method, null, Description.class); // null instance param because it's a static method
        //         break;
        //     };
        // }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientListener(new RolfLectionLibListener(false));

        // For custom dynamic at runtime campaign entity descriptions TODO account for different saves
        // Map<String, Object> persistentData = Global.getSector().getPersistentData();
        // Map<String, Object> customStationDescs = (Map<String, Object>) persistentData.get(Misc.CUSTOM_DESC_PERSISTENT_DATA_KEY);

        // if (customStationDescs != null) {
        //     Misc.DESCRIPTION_MAP = new HashMap<>();

        //     for (Map.Entry<String, Object> entry : customStationDescs.entrySet()) {
        //         Misc.DESCRIPTION_MAP.put(entry.getKey() + "_CUSTOM", entry.getValue());

        //         SectorEntityToken token = Global.getSector().getEntityById(entry.getKey());
        //         token.setCustomDescriptionId(token.getId());
        //     }

        // } else {
        //     Map<String, Object> descMap = new HashMap<>();
        //     Misc.DESCRIPTION_MAP = descMap;
        //     persistentData.put(Misc.CUSTOM_DESC_PERSISTENT_DATA_KEY, descMap);
        // }
    }

    @Override
    public void onNewGame() {

    }
}
