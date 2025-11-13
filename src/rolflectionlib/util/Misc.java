package rolflectionlib.util;

import java.util.*;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.input.InputEventClass;
import com.fs.starfarer.api.input.InputEventType;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.loading.SpecStore;

import rolflectionlib.util.ListenerFactory.ActionListener;
import rolflectionlib.util.ListenerFactory.DialogDismissedListener;

@SuppressWarnings("unchecked")
public class Misc {
    private static final Logger logger = Global.getLogger(Misc.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

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
    /**
     * Only works in campaign.
     * @return CoreUI
     */
    public static UIPanelAPI getCoreUI() {
        CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        return dialog == null ? (UIPanelAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.campaignUIGetCoreMethod, campaignUI) : (UIPanelAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.interactionDialogGetCoreUIMethod, dialog);
    }

    public static Object createConfirmDialog(String text, String confirmText, String cancelText, float width, float height, DialogDismissedListener dialogListener, Object screenPanel) {
        return RolfLectionUtil.instantiateClass(
            ClassRefs.confirmDialogClass,
            ClassRefs.confirmDialogClassParamTypes,
            width,
            height,
            screenPanel,
            dialogListener.getProxy(),
            text,
            new String[]{confirmText, cancelText}
        );
    }

    /**
     * 
     * @param title Confirm dialog window title
     * @param confirmText Confirm button text
     * @param cancelText Cancel button text
     * @param width Dialog window width
     * @param height Dialog window height
     * @param dialogListener Listener implementation that calls {@link DialogDismissedListener} trigger method when the confirm dialog is closed
     * @param screenPanel Panel field the confirm dialog anchors to. In most cases is the screenPanel field of CampaignUI. 
     * @return Object array containing dialog components in this order: [0] Title label, [1] Confirm button, [2] Cancel button, [3] Confirm dialog object itself
     */
    public static Object[] showConfirmationDialog(
        String title,
        String confirmText,
        String cancelText,
        float width,
        float height,
        DialogDismissedListener dialogListener,
        Object screenPanel
    ) {

        Object confirmDialog = createConfirmDialog(title, confirmText, cancelText, width, height, dialogListener, screenPanel);
        RolfLectionUtil.invokeMethodDirectly(ClassRefs.confirmDialogShowMethod, confirmDialog, 0.25f, 0.25f);

        LabelAPI label = (LabelAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.confirmDialogGetLabelMethod, confirmDialog);
        ButtonAPI yes = (ButtonAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.confirmDialogGetButtonMethod, confirmDialog, 0);
        ButtonAPI no = (ButtonAPI) RolfLectionUtil.invokeMethodDirectly(ClassRefs.confirmDialogGetButtonMethod, confirmDialog, 1);

        return new Object[] {label, yes, no, confirmDialog};
    }

    public static Object getButtonListener(ButtonAPI button) {
        return RolfLectionUtil.invokeMethodDirectly(ClassRefs.buttonGetListenerMethod, button);
    }
}
