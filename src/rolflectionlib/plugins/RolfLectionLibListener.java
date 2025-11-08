package rolflectionlib.plugins;

import java.util.*;

import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import rolflectionlib.ui.OptionPanelListener;

public class RolfLectionLibListener extends BaseCampaignEventListener{
    public static interface OptionPanelHook {
        public void afterOptionSelected(Object optionData);
    }

    private static List<OptionPanelHook> optionPanelHooks = new ArrayList<>();

    public static void addOptionPanelHook(OptionPanelHook hook) {
        optionPanelHooks.add(hook);
    }

    public static void removeOptionPanelHook(OptionPanelHook hook) {
        optionPanelHooks.remove(hook);
    }

    public RolfLectionLibListener(boolean permaRegister) {
        super(permaRegister);
    }
    
    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        RolfLectionLibCombatPlugin.frame = 0;

        new OptionPanelListener(dialog) {
            @Override
            public void afterOptionSelected(Object optionData) {
                for (OptionPanelHook hook : optionPanelHooks) hook.afterOptionSelected(optionData);
            }
            
        };
    }
}
