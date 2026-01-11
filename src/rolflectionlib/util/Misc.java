package rolflectionlib.util;

import java.util.*;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;

import com.fs.starfarer.api.loading.Description;

@SuppressWarnings("unchecked")
public class Misc {
    public static Map<String, Object> DESCRIPTION_MAP;
    public static final String CUSTOM_DESC_PERSISTENT_DATA_KEY = "$rlCustomCampaignEntityDescs";
    public static final List<String> CUSTOM_DESC_KEYS = new ArrayList<>();

    public static void setEntityDescription(SectorEntityToken entity, String desc) {
        Description newDescription = new Description(entity.getId(), Description.Type.CUSTOM);
        newDescription.setText1(desc);
        newDescription.setText2(desc);

        String key = entity.getId() + "_CUSTOM";
        DESCRIPTION_MAP.put(key, newDescription);
        ((Map<String, Object>)Global.getSector().getPersistentData().get(CUSTOM_DESC_PERSISTENT_DATA_KEY)).put(entity.getId(), newDescription);

        CUSTOM_DESC_KEYS.add(key);
        entity.setCustomDescriptionId(entity.getId());
    }
}
