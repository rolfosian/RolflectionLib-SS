// Code taken and modified beyond recognition from Officer Extension mod
package rolflectionlib.util;

import com.fs.starfarer.campaign.fleet.CampaignFleet;
// import com.fs.graphics.Sprite;
// import com.fs.starfarer.campaign.fleet.FleetMember;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;

import com.fs.starfarer.api.input.InputEventClass;
import com.fs.starfarer.api.input.InputEventType;

import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.Color;
import java.util.*;

import org.apache.log4j.Logger;

/** Stores references to class objects in the obfuscated game files */
public class ClassRefs {
    protected static final Logger logger = Logger.getLogger(ClassRefs.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    /** The class that CampaignUIAPI.showConfirmDialog instantiates. We need this because showConfirmDialog doesn't work
     *  if any core UI is open. */
    public static Class<?> confirmDialogClass;
    public static Class<?>[] confirmDialogClassParamTypes;
    public static Object confirmDialogGetHoloMethod;
    public static Object confirmDialogGetButtonMethod;
    public static Object confirmDialogGetInnerPanelMethod;
    public static Object confirmDialogShowMethod;
    public static Object confirmDialogGetLabelMethod;
    public static Object confirmDialogSetBackgroundDimAmountMethod;
    public static Object confirmDialogOutsideClickAbsorbedMethod;

    /** Interface that contains a single method: actionPerformed */
    public static Class<?> actionListenerInterface;
    /** Interface that contains a single method: dialogDismissed */
    public static Class<?> dialogDismissedInterface;
    public static Object dialogDismissedInterfaceMethod;

    /** Interface for renderable UI elements */
    public static Class<?> renderableUIElementInterface;
    public static Object renderableSetOpacityMethod;

    /** Obfuscated UI panel class */
    public static Class<?> uiPanelClass;
    public static Class<?>[] uiPanelClassConstructorParamTypes = new Class<?>[] {
        float.class,
        float.class,
    };
    public static Object uiPanelsetParentMethod;
    public static Object uiPanelsetOpacityMethod;
    public static Object uiPanelgetChildrenNonCopyMethod;
    public static Object uiPanelgetChildrenCopyMethod;
    public static Object uiPanelShowTooltipMethod;
    public static Object uiPanelHideTooltipMethod;
    public static Object uiPanelSetTooltipMethod;
    public static Object uiPanelGetTooltipMethod;
    public static Object uiPanelAddMethod;
    public static Object uiPanelRemoveMethod;
    public static Object positionSetMethod;

    /** Obfuscated fleet info panel class from the VisualPanelAPI */
    public static Class<?> visualPanelFleetInfoClass; 
    public static Class<?>[] visualPanelFleetInfoClassParamTypes = new Class<?>[] {
        String.class, // fleet 1 name
        CampaignFleet.class, // fleet 1
        String.class, // fleet 2 name
        CampaignFleet.class, // fleet 2
        FleetEncounterContextPlugin.class,
        boolean.class // is before or after engagement? idk
    };
    public static Object visualPanelGetChildrenNonCopyMethod;
    public static Object optionPanelGetButtonToItemMapMethod;
    public static Object interactionDialogGetCoreUIMethod;

    public static Class<?> commDirectoryListPanelClass;
    public static Object commDirectoryGetItemsMethod;
    public static Object commDirectoryEntriesMapField;

    /** Obfuscated ButtonAPI class */
    public static Class<?> buttonClass;
    public static Object buttonListenerActionPerformedMethod;
    public static Object buttonGetListenerMethod;
    public static Object buttonSetListenerMethod;
    public static Object buttonSetEnabledMethod;
    public static Object buttonSetShortcutMethod;
    public static Object buttonSetButtonPressedSoundMethod;
    public static Object buttonSetActiveMethod;

    // public static Class<?> buttonFactoryClass;
    // public static Object memberButtonFactoryMethod;
    // public static Object spriteButtonFactoryMethod;
    // public static class memberButtonEnums {
    //     public static Object FRIEND;
    //     public static Object NEUTRAL;
    //     public static Object ENEMY;
    // }

    public static Object tablePanelsetItemsSelectableMethod;
    public static Object tablePanelSelectMethod;
    public static Object tableRowGetButtonMethod;
    public static Object tableRowParamsField;
    public static Object tableRowCreatedField;
    public static Object tableRowRenderMethod;

    public static Object campaignUIScreenPanelField;
    public static Object campaignUIGetCoreMethod;
    public static Object coreUIgetCurrentTabMethod;

    public static Object fleetTabGetMarketPickerMethod;
    public static Object fleetTabGetFleetPanelMethod;
    public static Object fleetTabFleetInfoPanelField;

    public static Object fleetPanelGetListMethod;
    public static Object fleetPanelListGetItemsMethod;
    public static Object fleetPanelRecreateUIMethod;
    public static Object fleetPanelgetClickAndDropHandlerMethod;
    public static Object fleetPanelClickAndDropHandlerGetPickedUpMemberMethod;

    public static Class<?> uiPanelSuperClass;

    /** Obfuscated InputEvent class */
    public static Class<?> inputEventClass;
    public static Class<?>[] inputEventClassParamTypes = new Class<?>[] {
        InputEventClass.class, // mouse or keyboard
        InputEventType.class, // type of input
        int.class, // x
        int.class, // y
        int.class, // key/mouse button, is -1 for mouse move
        char.class // unused for mouse afaik, give '\0' for mouse prob
    };

    /** method to get optionData from optionItem class that is the type of the values of the InteractionDialogAPI OptionPanel's buttonsToItemsMap */
    public static Object getOptionDataMethod;

    public static Class<?>[] standardTooltipV2ConstructorParamTypes = RolfLectionUtil.getConstructorParamTypesSingleConstructor(com.fs.starfarer.ui.impl.StandardTooltipV2.class);
    public static Class<?> CRBarClass;
    public static Object CRBarClassSetProgressMethod;
    public static Object CRBarClassForceSyncMethod;

    /** Obfuscated Ship class */
    public static Class<?> shipClass;

    static {
        Class<?>[] interfaces = ObfuscatedClasses.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> interfc = interfaces[i];

            Object[] methods = interfc.getDeclaredMethods();
            if (methods.length == 1 && RolfLectionUtil.getMethodName(methods[0]).equals("dialogDismissed")) {
                dialogDismissedInterface = interfc;
                dialogDismissedInterfaceMethod = methods[0];
                break;
            }
        }

        Class<?>[] obfClasses = ObfuscatedClasses.getClasses();
        for (int i = 0; i < obfClasses.length; i++) {
            Class<?> cls = obfClasses[i];

            if (shipClass == null && cls.getCanonicalName() != null && cls.getCanonicalName().equals("com.fs.starfarer.combat.entities.Ship")) {
                shipClass = cls;
                continue;
            }

            if (optionPanelGetButtonToItemMapMethod == null && OptionPanelAPI.class.isAssignableFrom(cls)) {
                optionPanelGetButtonToItemMapMethod = RolfLectionUtil.getMethod("getButtonToItemMap", cls, 0);
                continue;
            }
            if (interactionDialogGetCoreUIMethod == null && InteractionDialogAPI.class.isAssignableFrom(cls) && !cls.isAnonymousClass()) {
                interactionDialogGetCoreUIMethod = RolfLectionUtil.getMethod("getCoreUI", cls, 0);
                continue;
            }

            if (buttonClass == null && ButtonAPI.class.isAssignableFrom(cls)) {
                buttonClass = cls;
                buttonGetListenerMethod = RolfLectionUtil.getMethod("getListener", buttonClass, 0);
                buttonSetListenerMethod = RolfLectionUtil.getMethod("setListener", buttonClass, 1);
                buttonSetEnabledMethod = RolfLectionUtil.getMethod("setEnabled", buttonClass, 1);
                buttonSetShortcutMethod = RolfLectionUtil.getMethodExplicit("setShortcut", buttonClass, new Class<?>[]{int.class, boolean.class});
                buttonSetButtonPressedSoundMethod = RolfLectionUtil.getMethod("setButtonPressedSound", buttonClass, 1);
                buttonSetActiveMethod = RolfLectionUtil.getMethod("setActive", buttonClass, 1);

                actionListenerInterface = RolfLectionUtil.getReturnType(buttonGetListenerMethod);
                buttonListenerActionPerformedMethod = actionListenerInterface.getMethods()[0];

                Object buttonPressedMethod = RolfLectionUtil.getMethod("buttonPressed", buttonClass, 2);
                inputEventClass = RolfLectionUtil.getMethodParamTypes(buttonPressedMethod)[0];
                continue;
            }

            if (campaignUIGetCoreMethod == null && cls.getSimpleName().equals("CampaignState")) {
                campaignUIGetCoreMethod = RolfLectionUtil.getMethod("getCore", cls, 0);

                Class<?> coreUIClass = RolfLectionUtil.getReturnType(campaignUIGetCoreMethod);
                coreUIgetCurrentTabMethod = RolfLectionUtil.getMethod("getCurrentTab", coreUIClass, 0);

                campaignUIScreenPanelField = RolfLectionUtil.getFieldByName("screenPanel", cls);
                uiPanelClass = RolfLectionUtil.getFieldType(campaignUIScreenPanelField);
                uiPanelSuperClass = uiPanelClass.getSuperclass();
                
                outer:
                for (Class<?> interfc : uiPanelClass.getInterfaces()) {
                    for (Object method : interfc.getDeclaredMethods()) {
                        if (RolfLectionUtil.getMethodName(method).equals("render")) {
                            renderableUIElementInterface = interfc;
                            renderableSetOpacityMethod = RolfLectionUtil.getMethod("setOpacity", renderableUIElementInterface, 1);
                            break outer;
                        }
                    }
                }

                uiPanelsetParentMethod = RolfLectionUtil.getMethod("setParent", uiPanelClass, 1);
                uiPanelsetOpacityMethod = RolfLectionUtil.getMethod("setOpacity", uiPanelClass, 1);
                uiPanelgetChildrenNonCopyMethod = RolfLectionUtil.getMethod("getChildrenNonCopy", uiPanelClass, 0);
                uiPanelgetChildrenCopyMethod = RolfLectionUtil.getMethod("getChildrenCopy", uiPanelClass, 0);
                uiPanelShowTooltipMethod = RolfLectionUtil.getMethod("showTooltip", uiPanelClass, 1);
                uiPanelHideTooltipMethod = RolfLectionUtil.getMethod("hideTooltip", uiPanelClass, 1);
                uiPanelSetTooltipMethod = RolfLectionUtil.getMethod("setTooltip", uiPanelClass, 2);
                uiPanelGetTooltipMethod = RolfLectionUtil.getMethod("getTooltip", uiPanelClass, 0);
                uiPanelAddMethod = RolfLectionUtil.getMethodExplicit("add", uiPanelClass, new Class<?>[]{ClassRefs.renderableUIElementInterface});
                uiPanelRemoveMethod = RolfLectionUtil.getMethodExplicit("remove", uiPanelClass, new Class<?>[]{ClassRefs.renderableUIElementInterface});
    
                positionSetMethod = RolfLectionUtil.getMethod("set", RolfLectionUtil.getReturnType(uiPanelAddMethod), 1);

                confirmDialogClassParamTypes = new Class<?>[] {
                    float.class,
                    float.class,
                    ClassRefs.uiPanelClass,
                    ClassRefs.dialogDismissedInterface,
                    String.class,
                    String[].class
                };
                continue;
            }

            Object[] methods = cls.getDeclaredMethods();
            switch(methods.length) {
                case 2:
                    if (getOptionDataMethod == null) {
                        boolean objReturnType = false;
                        boolean stringReturnType = false;
                        Object objReturnMethod = null;
        
                        for (int j = 0; j < 2; j++) {
                            Object method = methods[j];
                            Class<?> returnType = RolfLectionUtil.getReturnType(method);
        
                            if (returnType.equals(Object.class)) {
                                objReturnType = true;
                                objReturnMethod = method;
        
                            } else if (returnType.equals(String.class)) {
                                stringReturnType = true;
                            }
                        }
                        if (objReturnType && stringReturnType) {
                            getOptionDataMethod = objReturnMethod;
                        }
                    }
                    if (visualPanelFleetInfoClass == null && RolfLectionUtil.doInstantiationParamsMatch(cls, ClassRefs.visualPanelFleetInfoClassParamTypes)) {
                        visualPanelFleetInfoClass = cls;
                    }
                    continue;

                case 15:
                    if (confirmDialogClass == null) {
                        for (int j = 0; j < methods.length; j++) {
                            Object method = methods[j];
                            
                            if ((RolfLectionUtil.getMethodName(method)).equals("setNoiseOnConfirmDismiss")) {
                                confirmDialogClass = cls;

                                confirmDialogGetHoloMethod = RolfLectionUtil.getMethod("getHolo", confirmDialogClass, 0);
                                confirmDialogGetButtonMethod = RolfLectionUtil.getMethod("getButton", confirmDialogClass, 1);
                                confirmDialogGetInnerPanelMethod = RolfLectionUtil.getMethod("getInnerPanel", confirmDialogClass, 0);
                                confirmDialogShowMethod = RolfLectionUtil.getMethod("show", confirmDialogClass, 2);
                                confirmDialogGetLabelMethod = RolfLectionUtil.getMethod("getLabel", confirmDialogClass, 0);
                                confirmDialogSetBackgroundDimAmountMethod = RolfLectionUtil.getMethod("setBackgroundDimAmount", confirmDialogClass, 1);
                                confirmDialogOutsideClickAbsorbedMethod = RolfLectionUtil.getMethodDeclared("outsideClickAbsorbed", confirmDialogClass, 1);
                                break;
                            }
                        }
                    }
                    continue;

                case 17:
                    if (fleetTabGetFleetPanelMethod == null) {
                        for (int j = 0; j < methods.length; j++) {
                            Object method = methods[j];
        
                            if (RolfLectionUtil.getMethodName(method).equals("getMousedOverFleetMember")) {
                                fleetTabGetFleetPanelMethod = RolfLectionUtil.getMethod("getFleetPanel", cls, 0);
                                fleetTabGetMarketPickerMethod = RolfLectionUtil.getMethod("getMarketPicker", cls, 0);
                        
                                Class<?> fleetPanelCls = RolfLectionUtil.getReturnType(fleetTabGetFleetPanelMethod);
                                fleetPanelgetClickAndDropHandlerMethod = RolfLectionUtil.getMethod("getClickAndDropHandler", fleetPanelCls, 0);
                                fleetPanelRecreateUIMethod = RolfLectionUtil.getMethod("recreateUI", fleetPanelCls, 1);
                                fleetPanelGetListMethod = RolfLectionUtil.getMethod("getList", fleetPanelCls, 0);
                        
                                Class<?> clickAndDropHandlerCls = RolfLectionUtil.getReturnType(fleetPanelgetClickAndDropHandlerMethod);
                                fleetPanelClickAndDropHandlerGetPickedUpMemberMethod = RolfLectionUtil.getMethod("getPickedUpMember", clickAndDropHandlerCls, 0);
                                
                                Class<?> fleetPanelListCls = RolfLectionUtil.getReturnType(fleetPanelGetListMethod);
                                fleetPanelListGetItemsMethod = RolfLectionUtil.getMethod("getItems", fleetPanelListCls, 0);
                                
                                Object[] fields = cls.getDeclaredFields();
                                outer:
                                for (int k = 0; k < fields.length; k++) {
                                    Object field = fields[k];
                                    Class<?> fieldType = RolfLectionUtil.getFieldType(field);
                                    if (!UIPanelAPI.class.isAssignableFrom(fieldType)) continue;
                                    
                                    boolean hasLabelField = false;
                                    boolean hasFleetField = false;

                                    Object[] innerFields = fieldType.getDeclaredFields();
                                    for (int l = 0; l < innerFields.length; l++) {
                                        Object innerField = innerFields[l];

                                        Class<?> innerFieldType = RolfLectionUtil.getFieldType(innerField);
                                        if (CampaignFleetAPI.class.isAssignableFrom(innerFieldType)) {
                                            hasFleetField = true;
                                        }
                                        if (LabelAPI.class.isAssignableFrom(innerFieldType)) {
                                            hasLabelField = true;
                                        }
                                        if (hasFleetField && hasLabelField) {
                                            fleetTabFleetInfoPanelField = field;
                                            break outer;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    continue;

                case 32:
                    if (commDirectoryGetItemsMethod == null) {
                        outer:
                        for (int j = 0; j < methods.length; j++) {
                            Object method = methods[j];

                            if (RolfLectionUtil.getMethodName(method).equals("getItems")) {
                                commDirectoryListPanelClass = cls;
                                commDirectoryGetItemsMethod = method;

                                Object[] fields = cls.getDeclaredFields();
                                for (int k = 0; k < fields.length; k++) {
                                    Object field = fields[k];
                                    
                                    if (RolfLectionUtil.getFieldType(field).equals(Map.class)) {
                                        commDirectoryEntriesMapField = field;
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    continue;

                // case 30:
                //     if (buttonFactoryClass == null) {
                //         outer:
                //         for (int j = 0; j < methods.length; j++) {
                //             Object method = methods[j];
                //             Class<?> returnType = RolfLectionUtil.getReturnType(method);

                //             if (ButtonAPI.class.isAssignableFrom(returnType)) {
                //                 if (buttonClass == null) {
                //                     buttonClass = returnType;
                //                     buttonGetListenerMethod = RolfLectionUtil.getMethod("getListener", buttonClass, 0);
                //                     buttonSetListenerMethod = RolfLectionUtil.getMethod("setListener", buttonClass, 1);
                //                     buttonSetEnabledMethod = RolfLectionUtil.getMethod("setEnabled", buttonClass, 1);
                //                     buttonSetShortcutMethod = RolfLectionUtil.getMethodExplicit("setShortcut", buttonClass, new Class<?>[]{int.class, boolean.class});
                //                     buttonSetButtonPressedSoundMethod = RolfLectionUtil.getMethod("setButtonPressedSound", buttonClass, 1);
                //                     buttonSetActiveMethod = RolfLectionUtil.getMethod("setActive", buttonClass, 1);
                    
                //                     actionListenerInterface = RolfLectionUtil.getReturnType(buttonGetListenerMethod);
                //                     buttonListenerActionPerformedMethod = actionListenerInterface.getMethods()[0];
                    
                //                     Object buttonPressedMethod = RolfLectionUtil.getMethod("buttonPressed", buttonClass, 2);
                //                     inputEventClass = RolfLectionUtil.getMethodParamTypes(buttonPressedMethod)[0];
                //                 }

                //                 Class<?>[] paramTypes = RolfLectionUtil.getMethodParamTypes(method);
                //                 if (paramTypes.length == 2 && paramTypes[0].equals(FleetMember.class)) {
                //                     memberButtonFactoryMethod = method;
                                    
                //                     for (Object constant : paramTypes[1].getEnumConstants()) {
                //                         String constante = String.valueOf(constant);

                //                         if (constante.equals("FRIEND")) memberButtonEnums.FRIEND = constant;
                //                         else if (constante.equals("ENEMY")) memberButtonEnums.ENEMY = constant;
                //                         else memberButtonEnums.NEUTRAL = constant;
                //                         buttonFactoryClass = cls;
                //                         break outer;
                //                     }
                //                 }

                //             }
                //         }
                //     }
                //     continue;

                case 56:
                    if (CRBarClass == null) {
                        outer:
                        for (int j = 0; j < methods.length; j++) {
                            Object method = methods[j];

                            if (RolfLectionUtil.getMethodName(method).equals("setProgress")) {
                                CRBarClass = cls;
                                CRBarClassSetProgressMethod = method;

                                for (int k = 0; k < methods.length; k++) {
                                    Object methode = methods[k];

                                    if (RolfLectionUtil.getMethodName(methode).equals("forceSync")) {
                                        CRBarClassForceSyncMethod = methode;
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    continue;

                default:
                    continue;
            }
        }

        CustomPanelAPI panel = Global.getSettings().createCustom(0f, 0f, null);
        TooltipMakerAPI tt = panel.createUIElement(0f, 0f, false);
        Object tablePanel = tt.beginTable(Global.getSettings().getBasePlayerColor(), Global.getSettings().getBasePlayerColor(), Global.getSettings().getBasePlayerColor(), 1f, false, false, new Object[]{"", 1f});
        tablePanelsetItemsSelectableMethod = RolfLectionUtil.getMethod("setItemsSelectable", tablePanel, 1);
        tablePanelSelectMethod = RolfLectionUtil.getMethod("select", tablePanel, 2);

        Object row = tt.addRowWithGlow(new Color(0, 0, 0), "");
        tableRowGetButtonMethod = RolfLectionUtil.getMethod("getButton", row, 0);
        tableRowRenderMethod = RolfLectionUtil.getMethod("render", row, 1);
        tableRowCreatedField = RolfLectionUtil.getFieldByName("created", row.getClass().getSuperclass());

        for (Object field : row.getClass().getDeclaredFields()) {
            if (RolfLectionUtil.getFieldType(field).equals(Object[].class)) {
                tableRowParamsField = field;
                break;
            }
        }
    }

    /**Dummy function to call to load the class and run the static block in onApplicationLoad */
    public static void findAllClasses() {}
}