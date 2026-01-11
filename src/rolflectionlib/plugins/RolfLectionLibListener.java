package rolflectionlib.plugins;

import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import rolflectionlib.ui.OptionPanelListener;

public class RolfLectionLibListener extends BaseCampaignEventListener {
    public static interface OptionPanelHook {
        public void afterOptionSelected(OptionPanelListener listener, Object optionData);
    }
    
    private static RolfLectionLibListener instance;
    private List<OptionPanelHook> optionPanelHooks = new ArrayList<>();

    private RolfLectionLibListener() {
        super(false);
    }

    public static void registerOptionPanelHook(OptionPanelHook hook) {
        if (!isInstance()) {
            instance = new RolfLectionLibListener();
            Global.getSector().addTransientListener(instance);
        }
        instance.addOptionPanelHook(hook);
    }

    protected void addOptionPanelHook(OptionPanelHook hook) {
        this.optionPanelHooks.add(hook);
    }

    protected void removeOptionPanelHook(OptionPanelHook hook) {
        this.optionPanelHooks.remove(hook);
    }

    public static void deregisterOptionPanelHook(OptionPanelHook hook) {
        if (isInstance()) {
            instance.removeOptionPanelHook(hook);
        }
    }
    
    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        RolfLectionLibCombatPlugin.frame = 0;

        new OptionPanelListener(dialog) {
            @Override
            public void afterOptionSelected(Object optionData) {
                for (OptionPanelHook hook : optionPanelHooks) hook.afterOptionSelected(this, optionData);
            }
        };
    }

    private static boolean isInstance() {
        if (instance == null) return false;
        for (CampaignEventListener listener : Global.getSector().getAllListeners()) {
            if (listener instanceof RolfLectionLibListener l) {
                instance = l;
                return true;
            }
        }
        return false;
    }
}
