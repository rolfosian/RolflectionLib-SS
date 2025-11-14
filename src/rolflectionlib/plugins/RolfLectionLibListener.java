package rolflectionlib.plugins;

import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;

import rolflectionlib.ui.OptionPanelListener;

public class RolfLectionLibListener extends BaseCampaignEventListener{
    public static interface OptionPanelHook {
        public void afterOptionSelected(OptionPanelListener listener, Object optionData);
    }

    private List<OptionPanelHook> optionPanelHooks = new ArrayList<>();

    public static void addOptionPanelHook(OptionPanelHook hook) {
        List<RolfLectionLibListener> listeners = Global.getSector().getListenerManager().getListeners(RolfLectionLibListener.class);
        
        if (listeners == null || listeners.isEmpty()) {
            RolfLectionLibListener listener = new RolfLectionLibListener(false);

            Global.getSector().addTransientListener(listener);
            listener.addOptionPanelHook(hook, true);

        } else {
            RolfLectionLibListener listener = listeners.get(0);
            listener.addOptionPanelHook(hook, true);
        }
    }

    protected void addOptionPanelHook(OptionPanelHook hook, boolean bool) {
        this.optionPanelHooks.add(hook);
    }

    protected void removeOptionPanelHook(OptionPanelHook hook, boolean bool) {
        this.optionPanelHooks.remove(hook);
    }

    public static void removeOptionPanelHook(OptionPanelHook hook) {
        List<RolfLectionLibListener> listeners = Global.getSector().getListenerManager().getListeners(RolfLectionLibListener.class);
        if (listeners != null && !listeners.isEmpty()) {
            RolfLectionLibListener listener = listeners.get(0);
            listener.removeOptionPanelHook(hook, true);
        }
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
                for (OptionPanelHook hook : optionPanelHooks) hook.afterOptionSelected(this, optionData);
            }
            
        };
    }
}
