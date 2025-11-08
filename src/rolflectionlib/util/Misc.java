package rolflectionlib.util;

import java.util.*;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.input.InputEventClass;
import com.fs.starfarer.api.input.InputEventType;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import rolflectionlib.util.ListenerFactory.ActionListener;

public class Misc {
    
    public static Object createButtonClickEventInstance(PositionAPI buttonPosition) {
        return createInputEventInstance(
        InputEventClass.MOUSE_EVENT,
        InputEventType.MOUSE_DOWN,
        (int)buttonPosition.getCenterX(),
        (int)buttonPosition.getCenterY(),
        0, // LMB
        '\0' // unused?
        );
    }

    public static Object createInputEventInstance(InputEventClass eventClass, InputEventType eventType, int x, int y, int val, char char_) {
        return RolfLectionUtil.instantiateClass(ClassRefs.inputEventClass,
        ClassRefs.inputEventClassParamTypes,
        eventClass,
        eventType,
        x,
        y,
        val, // keyboard key or mouse button, is -1 for mouse move
        char_ // char is only appicable for keyboard keys afaik, give '\0' for mouse prob
        );
    }

    public static void clickButton(ButtonAPI button) {
        if (button == null) return;

        Object listener = RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonGetListenerMethod, button);
        RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonListenerActionPerformedMethod, listener, createButtonClickEventInstance(((ButtonAPI)button).getPosition()), button);
    }

    public static void setButtonHook(ButtonAPI button, Runnable runBefore, Runnable runAfter) {
        Object oldListener = RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonGetListenerMethod, button);

        RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonSetListenerMethod, button, new ActionListener() {
            @Override
            public void trigger(Object... args) {
                runBefore.run();
                RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonListenerActionPerformedMethod, oldListener, args);
                runAfter.run();
            }
        }.getProxy());
    }

    public static List<Object> getChildrenRecursive(Object parentPanel) {
        List<Object> list = new ArrayList<>();
        collectChildren(parentPanel, list);
        return list;
    }

    private static void collectChildren(Object parent, List<Object> list) {
        List<Object> children;

        if (ClassRefs.uiPanelClass.isInstance(parent)) {
            children = (List<Object>) RolfLectionUtil.invokeMethodDirectly(ClassRefs.uiPanelgetChildrenNonCopyMethod, parent);
        } else {
            children = null;
        }

        if (children != null) {
            for (Object child : children) {
                list.add(child);
                collectChildren(child, list);
            }
        }
    }

    public static UIPanelAPI getCoreUI() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        return dialog == null ? (UIPanelAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.campaignUIGetCoreMethod, campaignUI) : (UIPanelAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.interactionDialogGetCoreUIMethod, dialog);
    }
}
