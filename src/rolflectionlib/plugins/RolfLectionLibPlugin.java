package rolflectionlib.plugins;

import java.util.*;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;

import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.loading.SpecStore;

import rolflectionlib.inheritor.enginetex.EngineTex;
import rolflectionlib.inheritor.examples.MultiListener;

import rolflectionlib.ui.UiUtil;
import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.Misc;
import rolflectionlib.util.ObfuscatedClasses;
import rolflectionlib.util.RolfLectionUtil;
import rolflectionlib.util.TexReflection;

@SuppressWarnings("unchecked")
public class RolfLectionLibPlugin extends BaseModPlugin {
    private static Logger logger = Global.getLogger(RolfLectionLibPlugin.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }
    
    private static final String SAVE_TIMESTAMP_KEY = "$rlSaveTimestamp";
    public static String SAVE_TIMESTAMP;

    @Override
    public void onApplicationLoad() {
        ObfuscatedClasses.init();
        RolfLectionUtil.init();

        ClassRefs.findAllClasses();
        TexReflection.init();
        EngineTex.init();
        MultiListener.init();
        UiUtil.init();

        for (Object method : SpecStore.class.getDeclaredMethods()) {
            if (RolfLectionUtil.getReturnType(method).equals(Map.class)) {
                Misc.DESCRIPTION_MAP = (Map<String, Object>) RolfLectionUtil.invokeMethodDirectly(method, null, Description.class); // null instance param because it's a static method
                break;
            };
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientListener(new RolfLectionLibListener(false));

        // For custom dynamic at runtime campaign entity descriptions
        Map<String, Object> persistentData = Global.getSector().getPersistentData();
        String saveTimestamp = (String) persistentData.get(SAVE_TIMESTAMP_KEY);

        if (saveTimestamp == null) {
            saveTimestamp = String.valueOf(System.currentTimeMillis());

            persistentData.put(SAVE_TIMESTAMP_KEY, saveTimestamp);
        }

        if (!saveTimestamp.equals(SAVE_TIMESTAMP)) { // in case of different save we need to remove different save descriptions
            for (String key : Misc.CUSTOM_DESC_KEYS) {
                Misc.DESCRIPTION_MAP.remove(key);
            }
            Misc.CUSTOM_DESC_KEYS.clear();
        }

        Map<String, Object> customStationDescs = (Map<String, Object>) persistentData.get(Misc.CUSTOM_DESC_PERSISTENT_DATA_KEY);
        if (customStationDescs != null) {
            for (Map.Entry<String, Object> entry : customStationDescs.entrySet()) {
                Misc.DESCRIPTION_MAP.put(entry.getKey() + "_CUSTOM", entry.getValue());

                SectorEntityToken token = Global.getSector().getEntityById(entry.getKey());
                token.setCustomDescriptionId(token.getId());
            }
        } else {
            persistentData.put(Misc.CUSTOM_DESC_PERSISTENT_DATA_KEY, new HashMap<>());
        }

        SAVE_TIMESTAMP = saveTimestamp;
    }

    @Override
    public void onNewGame() {

    }
}
