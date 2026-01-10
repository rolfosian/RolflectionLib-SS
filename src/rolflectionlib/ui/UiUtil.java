package rolflectionlib.ui;

import java.util.*;
import java.awt.Color;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

import org.objectweb.asm.*;

import com.fs.starfarer.title.TitleScreenState;
import com.fs.starfarer.campaign.BaseLocation;
import com.fs.starfarer.campaign.CampaignState;
import com.fs.starfarer.campaign.command.AdminPickerDialog;
import com.fs.starfarer.campaign.comms.v2.EventsPanel;
import com.fs.starfarer.ui.impl.CargoTooltipFactory;
import com.fs.starfarer.ui.impl.StandardTooltipV2;
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable;

import com.fs.starfarer.campaign.ui.UITable;
import com.fs.starfarer.coreui.refit.FighterPickerDialog;
import com.fs.starfarer.coreui.refit.WeaponPickerDialog;
import com.fs.starfarer.loading.specs.BaseWeaponSpec;
import com.fs.starfarer.loading.specs.FighterWingSpec;
import com.fs.graphics.Sprite;
import com.fs.graphics.util.Fader;

import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.ui.PositionAPI;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.util.RolFileUtil;
import rolflectionlib.util.RolfLectionUtil;

public class UiUtil implements Opcodes {
    public static interface UiUtilInterface {
        public UIPanelAPI titleScreenStateGetScreenPanel(Object titleScreenState);
        public UIPanelAPI interactionDialogGetCore(Object interactionDialog);

        public UIPanelAPI campaignUIgetCore(Object campaignUI);
        public UIPanelAPI campaignUIgetScreenPanel(Object campaignUI);
        public float campaignUIgetFactor(Object campaignUI);

        public UIPanelAPI coreGetCurrentTab(Object core);

        public EventsPanel getEventsPanel(Object intelTab); 
        public ButtonAPI intelTabGetPlanetsButton(Object intelTab);
        public UIPanelAPI intelTabGetPlanetsPanel(Object intelTab);

        public UIPanelAPI eventsPanelGetMap(EventsPanel eventsPanel);
        public UIPanelAPI mapTabGetMap(Object mapTab);

        public BaseLocation mapGetLocation(UIPanelAPI map);
        public UIPanelAPI mapGetMapTab(UIPanelAPI map);
        public Object getZoomTracker(UIPanelAPI map);
        public float getFactor(UIPanelAPI map);

        public float getMaxZoomFactor(Object zoomTracker);
        public float getZoomLevel(Object zoomTracker);

        public Object getMessageDisplay(Object campaignUI);
        public Object getCourseWidget(Object campaignUI);
        public SectorEntityToken getNextStep(Object courseWidget, SectorEntityToken target);
        public Fader getInner(Object courseWidget);
        public float getPhase(Object courseWidget);

        public void actionPerformed(Object listener, Object inputEvent, Object uiElement);

        public void buttonSetListener(Object button, Object listener);
        public Object buttonGetListener(Object button);
        public Object buttonGetRendererPanel(Object button);
        public Object buttonGetRendererCheckbox(Object button);

        public PositionAPI labelAutoSize(Object label);
        public void labelSetTooltipOffsetFromCenter(Object label, float xPad, float yPad); // TODO // not to be confused with uiComponent method with same name (not an interface method)
        public UIPanelAPI labelGetParent(Object label); // not to be confused with uiComponent method with same name (its not an interface method)
        
        public Fader uiComponentGetFader(Object uiComponent);
        public void uiComponentSetFader(Object uiComponent, Fader fader);
        public Object uiComponentGetTooltip(Object uiComponent);

        public void setTooltip(Object uiComponent, Object tooltip); // TODO
        public void setTooltipPositionRelativeToAnchor(Object uiComponent, float xPad, float yPad, Object anchor); // TODO // anchor should be instance of uicomponent

        public void showTooltip(Object uiComponent, Object tooltip);
        public void hideTooltip(Object uiComponent, Object tooltip);
        public void uiComponentsetTooltipOffsetFromCenter(Object uiComponent, float xPad, float yPad); // TODO // not to be confused with label method with same name (its not an interface method)
        
        public UIComponentAPI getContents(Object tooltip);
        
        public void setSlideData(Object uiComponent, float xOffset, float yOffset, float durationIn, float durationOut);
        public void slideIn(Object uiComponent);
        public void slideOut(Object uiComponent);
        public void forceSlideIn(Object uiComponent);
        public void forceSlideOut(Object uiComponent);
        public boolean isSliding (Object uiComponent);
        public boolean isSlidIn (Object uiComponent);
        public boolean isSlidOut (Object uiComponent);
        public boolean isSlidingIn (Object uiComponent);
        public boolean isEnabled (Object uiComponent);
        public boolean setEnabled(Object uiComponent, boolean enabled);

        public float getOpacity(Object uiComponent);
        public void setOpacity(Object uiComponent, float opacity);
        public void setMouseOverPad(Object uiComponent, float pad1, float pad2, float pad3, float pad4);
        public Fader getMouseoverHighlightFader(Object uiComponent);

        public UIPanelAPI findTopAncestor(Object uiComponent);
        public UIPanelAPI getParent(Object uiComponent);
        public void setParent(Object uiComponent, Object parent);
        public List<UIComponentAPI> getChildrenNonCopy(UIComponentAPI parent); // custom method with instanceof check uiPanelClass else return null
        public List<UIComponentAPI> getChildrenNonCopy(UIPanelAPI uiPanel); // direct cast
        public List<UIComponentAPI> getChildrenCopy(UIPanelAPI uiPanel);
        public void clearChildren(Object uiPanel);

        public void confirmDialogDismiss(Object confirmDialog, int button);
        public ButtonAPI confirmDialogGetButton(Object confirmDialog, int button);
        public LabelAPI confirmDialogGetLabel(Object confirmDialog);
        public boolean isNoiseOnConfirmDismiss(Object confirmDialog);
        public void confirmDialogShow(Object confirmDialog, float durationIn, float durationOut);
        public UIPanelAPI confirmDialogGetInnerPanel(Object confirmDialog); // custom method with instanceof check confirmDialog superclass else return null
        public UIComponentAPI confirmDialogGetHolo(Object confirmDialog);

        public Map<ButtonAPI, Object> optionPanelGetButtonToItemMap(OptionPanelAPI optionPanel);
        public Object optionPanelItemGetOptionData(Object optionItem);
        public List<UIComponentAPI> listPanelGetItems(Object listPanel); // custom method with instanceof check listPanelClass else return null
        public void listPanelAddItem(Object listPanel, UIComponentAPI toAdd);
        public void listPanelClear(Object listPanel);

        public List<Object> uiTableGetRows(UITable table);
        public void uiTableAddRow(UITable table, Object row);
        public void uiTableRemoveRow(UITable table, Object row);
        public Object uiTableGetRowForData(UITable table, Object data);
        public UIPanelAPI uiTableRowGetCol(Object row, int col);
        public ButtonAPI uiTableRowGetButton(Object row);
        public void uiTableRowSetButton(Object row, Object button);
        public Object uiTableRowGetData(Object row);
        public void uiTableRowSetData(Object row, Object data); // data type is actually java.lang.Object, no cast required

        public Object uiTableGetSelected(UITable table);
        public void uiTableSelect(UITable table, Object row, Object inputEvent);
        public void uiTableSelect(UITable table, Object row, Object inputEvent, boolean notifyDelegate);

        public UIPanelAPI uiTableGetList(UITable table);

        public void imagePanelSetRenderSchematic(Object imagePanel, boolean renderSchematic);
        public void imagePanelAutoSize(Object imagePanel);
        public void imagePanelAutoSizeToWidth(Object imagePanel, float width);
        public void imagePanelAutoSizeToHeight(Object imagePanel, float height);
        public void imagePanelSetStretch(Object imagePanel, boolean stretch);
        public Sprite imagePanelGetSprite(Object imagePanel);
        public void imagePanelSetSprite(Object imagePanel, Sprite sprite, boolean isResize);
        public String imagePanelGetSpriteName(Object imagePanel);
        public void imagePanelSetSprite(Object imagePanel, String spriteName, boolean isResize);
        public Color imagePanelGetBorderColor(Object imagePanel);
        public void imagePanelSetBorderColor(Object imagePanel, Color borderColor);
        public boolean imagePanelIsWithOutline(Object imagePanel);
        public void imagePanelSetWithOutline(Object imagePanel, boolean isWithOutline);
        public boolean imagePanelIsTexClamp(Object imagePanel);
        public void imagePanelSetTexClamp(Object imagePanel, boolean texClamp);
        public boolean imagePanelIsForceNoRounding(Object imagePanel);
        public boolean imagePanelSetForceNoRounding(Object imagePanel, boolean forceNoRounding);
        public float imagePanelGetOriginalAR(Object imagePanel);

        public PositionAPI positionRelativeTo(PositionAPI position, PositionAPI targetRelativePosition, float var2, float var3, float var4, float var5, float var6, float var7);
        public float positionGetXAlignOffset(Object position);
        public float positionGetYAlignOffset(Object position);
        public PositionAPI positionSetXAlignOffset(Object position, float offset);
        public PositionAPI positionSetYAlignOffset(Object position, float offset);

        public void addTooltipAbove(Object tooltip, Object uiComponent);
        public void addTooltipBelow(Object tooltip, Object uiComponent);
        public void addTooltipRight(Object tooltip, Object uiComponent);
        public void addTooltipLeft(Object tooltip, Object uiComponent);
        public void addTooltipAbove(Object tooltip, Object uiComponent, float padding);
        public void addTooltipBelow(Object tooltip, Object uiComponent, float padding);
        public void addTooltipRight(Object tooltip, Object uiComponent, float padding);
        public void addTooltipLeft(Object tooltip, Object uiComponent, float padding);
    }

    // With this we can implement the above interface and generate a class at runtime to call obfuscated class methods platform agnostically without RolfLectionUtilection overhead
    private static Class<?>[] implementUiUtilInterface() throws Throwable {
        Class<?> coreClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getCore", CampaignState.class));
        Class<?> uiPanelClass = coreClass.getSuperclass().getSuperclass();
        Class<?> uiComponentClass = uiPanelClass.getSuperclass();
        Class<?> toolTipClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getTooltip", uiComponentClass));
        Class<?> interactionDialogClass = RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByName("encounterDialog", CampaignState.class));
        Class<?> buttonClass = RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByInterface(ButtonAPI.class, EventsPanel.class));
        Class<?> buttonRendererPanelClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getRendererPanel", buttonClass));
        Class<?> actionPerformedInterface = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getListener", buttonClass));
        Class<?> confirmDialogClass = AdminPickerDialog.class.getSuperclass();
        Class<?> confirmDialogHoloClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getHolo", confirmDialogClass));
        Class<?> listPanelClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getListAdmins", AdminPickerDialog.class));
        Class<?> uiComponentInterfaceA = RolfLectionUtil.getMethodParamTypes(RolfLectionUtil.getMethod("addItem", listPanelClass))[0];
        Class<?> dialogDismissedInterface = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getDelegate", confirmDialogClass.getSuperclass()));
        Class<?> labelClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getLabel", confirmDialogClass));
        Class<?> uiTableRowClass = RolfLectionUtil.getReturnType((RolfLectionUtil.getMethod("getSelected", UITable.class)));
        Class<?> inputEventClass = RolfLectionUtil.getMethodParamTypes(RolfLectionUtil.getMethod("buttonPressed", buttonClass))[0];
        Class<?> imagePanelClass = getImagePanelClass();
        Class<?> positionClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("add", listPanelClass));
        Class<?> addTooltipUiComponentClass = RolfLectionUtil.getMethodParamTypes(RolfLectionUtil.getMethod("addTooltipAbove", StandardTooltipV2Expandable.class))[0];
        Class<?> courseWidgetClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getCourseWidget", CampaignState.class));
        Class<?> messageDisplayClass = RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByName("messageDisplay", CampaignState.class));

        Class<?> mapTabClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getMap", EventsPanel.class));
        Class<?> mapClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getMap", mapTabClass));
        Class<?> intelTabClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getIntelTab", EventsPanel.class));
        Class<?> intelTabPlanetsPanelClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getPlanetsPanel", intelTabClass));
        Class<?> zoomTrackerClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getZoomTracker", mapClass));

        String[] zoomTrackerMethodNames = getZoomTrackerMethodNames(zoomTrackerClass);

        Class<?> optionPanelClass = getOptionPanelClass(interactionDialogClass);
        Class<?> optionPanelItemClass = null;
        for (Class<?> cls : optionPanelClass.getClasses()) {
            if (RolfLectionUtil.getConstructorParamTypesSingleConstructor(cls).length == 4) {
                optionPanelItemClass = cls;
                break;
            }
        }

        Class<?> showTooltipInterface = null;
        Class<?> setTooltipInterface = null;
        for (Class<?> interfc : uiComponentClass.getInterfaces()) {
            for (Object method: interfc.getMethods()) {
                switch(RolfLectionUtil.getMethodName(method)) {
                    case "showTooltip":
                        showTooltipInterface = interfc;
                        continue;
                    case "setTooltip":
                        setTooltipInterface = interfc;
                        continue;
                }
            }
        }

        String titleScreenStateInternalName = Type.getInternalName(TitleScreenState.class);
        String coreClassInternalName = Type.getInternalName(coreClass);
        String uiPanelInternalName = Type.getInternalName(uiPanelClass);
        String uiComponentInternalName = Type.getInternalName(uiComponentClass);
        String toolTipInternalName = Type.getInternalName(toolTipClass);
        String interactionDialogInternalName = Type.getInternalName(interactionDialogClass);
        String optionPanelInternalName = Type.getInternalName(optionPanelClass);
        String optionPanelItemInternalName = Type.getInternalName(optionPanelItemClass);
        String buttonClassInternalName = Type.getInternalName(buttonClass);
        String buttonRendererPanelInternalName = Type.getInternalName(buttonRendererPanelClass);
        String actionPerformedInterfaceInternalName = Type.getInternalName(actionPerformedInterface);
        String labelInternalName = Type.getInternalName(labelClass);
        String campaignStateInternalName = Type.getInternalName(CampaignState.class);
        String confirmDialogInternalName = Type.getInternalName(confirmDialogClass);
        String listPanelInternalName = Type.getInternalName(listPanelClass);
        String uiTableInternalName = Type.getInternalName(UITable.class);
        String uiTableRowInternalName = Type.getInternalName(uiTableRowClass);
        String inputEventInternalName = Type.getInternalName(inputEventClass);
        String imagePanelInternalName = Type.getInternalName(imagePanelClass);
        String spriteInternalName = Type.getInternalName(Sprite.class);
        String colorInternalName = Type.getInternalName(Color.class);
        String positionInternalName = Type.getInternalName(positionClass);
        String standardTooltipV2InternalName = Type.getInternalName(StandardTooltipV2.class);
        String standardTooltipV2ExpandableInternalName = Type.getInternalName(StandardTooltipV2Expandable.class);
        String addTooltipUiComponentInternalName = Type.getInternalName(addTooltipUiComponentClass);
        String courseWidgetInternalName = Type.getInternalName(courseWidgetClass);
        String mapTabInternalName = Type.getInternalName(mapTabClass);
        String mapClassInternalName = Type.getInternalName(mapClass);
        String intelTabInternalName = Type.getInternalName(intelTabClass);
        String zoomTrackerClassInternalName = Type.getInternalName(zoomTrackerClass);
        String confirmDialogHoloInternalName = Type.getInternalName(confirmDialogHoloClass);
        String showTooltipInterfaceInternalName = Type.getInternalName(showTooltipInterface);
        String setTooltipInterfaceInternalName = Type.getInternalName(setTooltipInterface);
        String uiComponentInterfaceAInternalName = Type.getInternalName(uiComponentInterfaceA);

        String titleScreenStateDesc = Type.getDescriptor(TitleScreenState.class);
        String coreClassDesc = Type.getDescriptor(coreClass);
        String uiPanelClassDesc = Type.getDescriptor(uiPanelClass);
        String uiPanelAPIDesc = Type.getDescriptor(UIPanelAPI.class);
        String uiComponentClassDesc = Type.getDescriptor(uiComponentClass);
        String uiComponentInterfaceADesc = Type.getDescriptor(uiComponentInterfaceA);
        String uiComponentApiDesc = Type.getDescriptor(UIComponentAPI.class);
        String buttonAPIDesc = Type.getDescriptor(ButtonAPI.class);
        String buttonClassDesc = Type.getDescriptor(buttonClass);
        String buttonRendererPanelDesc = Type.getDescriptor(buttonRendererPanelClass);
        String actionListenerInterfaceDesc = Type.getDescriptor(actionPerformedInterface);
        String tooltipDesc = Type.getDescriptor(toolTipClass);
        String labelAPIDesc = Type.getDescriptor(LabelAPI.class);
        String optionPanelApiDesc = Type.getDescriptor(OptionPanelAPI.class);
        String uiTableDesc = Type.getDescriptor(UITable.class);
        String uiTableRowDesc = Type.getDescriptor(uiTableRowClass);
        String inputEventDesc = Type.getDescriptor(inputEventClass);
        String listDesc = Type.getDescriptor(List.class);
        String mapDesc = Type.getDescriptor(Map.class);
        String listPanelDesc = Type.getDescriptor(listPanelClass);
        String imagePanelClassDesc = Type.getDescriptor(imagePanelClass);
        String positionClassDesc = Type.getDescriptor(positionClass);
        String positionAPIDesc = Type.getDescriptor(PositionAPI.class);
        String colorDesc = Type.getDescriptor(Color.class);
        String spriteDesc = Type.getDescriptor(Sprite.class);
        String stringDesc = Type.getDescriptor(String.class);
        String standardTooltipV2Desc = Type.getDescriptor(StandardTooltipV2.class);
        String addTooltipUiComponentDesc = Type.getDescriptor(addTooltipUiComponentClass);
        String mapTabDesc = Type.getDescriptor(mapTabClass);
        String sectorEntityTokenDesc = Type.getDescriptor(SectorEntityToken.class);
        String eventsPanelDesc = Type.getDescriptor(EventsPanel.class);
        String baseLocationDesc = Type.getDescriptor(BaseLocation.class);
        String faderDesc = Type.getDescriptor(Fader.class);
        String labelDesc = Type.getDescriptor(labelClass);
        String zoomTrackerDesc = Type.getDescriptor(zoomTrackerClass);
        String confirmDialogHoloDesc = Type.getDescriptor(confirmDialogHoloClass);

        // String addTooltipMethodDesc = Type.getMethodDescriptor(RolfLectionUtil.getMethod("addTooltipAbove", StandardTooltipV2Expandable.class));

        String superName = Type.getType(Object.class).getInternalName();
        String interfaceName = Type.getType(UiUtilInterface.class).getInternalName();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        // public class UiUtilInterface extends Object implements this crap
        cw.visit(
            V17,
            ACC_PUBLIC,
            "rolflectionlib/util/UiUtilInterface",
            null,
            superName,
            new String[] {interfaceName}
        );

        // public UiUtilInterface() {
        //     super(); // Object()
        // }
        MethodVisitor ctor = cw.visitMethod(
            ACC_PUBLIC,
            "<init>",
            "()V",
            null,
            null
        );
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V", false);
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(0, 0);
        ctor.visitEnd();

        // public UIPanelAPI titleScreenStateGetScreenPanel(Object titleScreenState) {
        //     return ((TitleScreenState)titleScreenState).getScreenPanel();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "titleScreenStateGetScreenPanel",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, titleScreenStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                titleScreenStateInternalName,
                "getScreenPanel",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        
        // public UIPanelAPI interactionDialogGetCore(Object interactionDialog) {
        //     return ((interactionDialogClass)interactionDialog).getCoreUI();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "interactionDialogGetCore",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, interactionDialogInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                interactionDialogInternalName,
                "getCoreUI",
                "()" + coreClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI campaignUIgetCore(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getCore();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "campaignUIgetCore",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, campaignStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                campaignStateInternalName,
                "getCore",
                "()" + coreClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI campaignUIgetScreenPanel(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getScreenPanel();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "campaignUIgetScreenPanel",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, campaignStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                campaignStateInternalName,
                "getScreenPanel",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float campaignUIgetFactor(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getFactor();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "campaignUIgetFactor",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, campaignStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                campaignStateInternalName,
                "getFactor",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI coreGetCurrentTab(Object core) {
        //     return ((coreClass)core).getCurrentTab();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "coreGetCurrentTab",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, coreClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                coreClassInternalName,
                "getCurrentTab",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI intelTabGetPlanetsPanel(Object intelTab) {
        //     return ((intelTabClass)intelTab).getPlanetsPanel();
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "intelTabGetPlanetsPanel",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, intelTabInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                intelTabInternalName,
                "getPlanetsPanel",
                "()" + Type.getDescriptor(intelTabPlanetsPanelClass),
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public ButtonAPI intelTabGetPlanetsButton(Object intelTab) {
        //     return ((intelTabClass)intelTab).getPlanetsButton();
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "intelTabGetPlanetsButton",
                "(Ljava/lang/Object;)" + buttonAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, intelTabInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                intelTabInternalName,
                "getPlanetsButton",
                "()" + buttonClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public EventsPanel getEventsPanel(Object intelTab) {
        //     return ((intelTabClass)intelTab).getEventsPanel();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getEventsPanel",
                "(Ljava/lang/Object;)" + eventsPanelDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(intelTabClass));

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(intelTabClass),
                "getEventsPanel",
                "()" + eventsPanelDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI eventsPanelGetMap(EventsPanel eventsPanel) {
        //     return ((EventsPanel)eventsPanel).getMap();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "eventsPanelGetMap",
                "(" + eventsPanelDesc + ")" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(EventsPanel.class),
                "getMap",
                "()" + mapTabDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI mapTabGetMap(Object mapTab) {
        //     return ((mapTabClass)mapTab).getMap();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "mapTabGetMap",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, mapTabInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                mapTabInternalName,
                "getMap",
                "()" + Type.getDescriptor(mapClass),
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public BaseLocation mapGetLocation(UIPanelAPI map) {
        //     return ((mapClass)map).getLocation();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "mapGetLocation",
                "(" + uiPanelAPIDesc + ")" + baseLocationDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);

            mv.visitTypeInsn(CHECKCAST, mapClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                mapClassInternalName,
                "getLocation",
                "()" + baseLocationDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isRadarMode(UIPanelAPI map) {
        //     return ((mapClass)map).isRadarMode();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isRadarMode",
                "(" + uiPanelAPIDesc + ")Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, mapClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                mapClassInternalName,
                "isRadarMode",
                "()Z",
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object getZoomTracker(UIPanelAPI map) {
        //     return ((mapClass)map).getZoomTracker();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getZoomTracker",
                "(" + uiPanelAPIDesc + ")Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, mapClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                mapClassInternalName,
                "getZoomTracker",
                "()" + zoomTrackerDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float getFactor(UIPanelAPI map) {
        //     return ((mapClass)map).getFactor();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getFactor",
                "(" + uiPanelAPIDesc + ")F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, mapClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                mapClassInternalName,
                "getFactor",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float getZoomLevel(Object zoomTracker) {
        //     return ((zoomTrackerClass)zoomTracker).zoomLevelMethodName();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getZoomLevel",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, zoomTrackerClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                zoomTrackerClassInternalName,
                zoomTrackerMethodNames[0],
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float getMaxZoomFactor(Object zoomTracker) {
        //     return ((zoomTrackerClass)zoomTracker).getMaxZoomFactorMethodName();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getMaxZoomFactor",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(zoomTrackerClass));

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(zoomTrackerClass),
                zoomTrackerMethodNames[1],
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object getMessageDisplay(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getMessageDisplay();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getMessageDisplay",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, campaignStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                campaignStateInternalName,
                "getMessageDisplay",
                "()" + Type.getDescriptor(messageDisplayClass),
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object getCourseWidget(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getCourseWidget();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getCourseWidget",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, campaignStateInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                campaignStateInternalName,
                "getCourseWidget",
                "()" + Type.getDescriptor(courseWidgetClass),
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public SectorEntityToken getNextStep(Object courseWidget, SectorEntityToken target) {
        //     return ((courseWidgetClass)courseWidget).getNextStep(target);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getNextStep",
                "(" +
                    "Ljava/lang/Object;" +
                    sectorEntityTokenDesc +
                ")" +
                sectorEntityTokenDesc,
                null,
                null
            );
            mv.visitCode();
        
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, courseWidgetInternalName);
        
            mv.visitVarInsn(ALOAD, 2);
        
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                courseWidgetInternalName,
                "getNextStep",
                "(" + sectorEntityTokenDesc + ")" + sectorEntityTokenDesc,
                false
            );
        
            mv.visitInsn(ARETURN);
        
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Fader getInner(Object courseWidget) {
        //     return ((courseWidgetClass)courseWidget).getFader();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getInner",
                "(Ljava/lang/Object;)" + faderDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, courseWidgetInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                courseWidgetInternalName,
                "getInner",
                "()" + faderDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float getPhase(Object courseWidget) {
        //     return ((courseWidgetClass)courseWidget).getPhase();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getPhase",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, courseWidgetInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                courseWidgetInternalName,
                "getPhase",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void actionPerformed(Object listener, Object inputEvent, Object uiElement) {
        //     ((actionListenerInterface)listener).actionPerformed(inputEvent, uiElement);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "actionPerformed",
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, actionPerformedInterfaceInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);

            mv.visitMethodInsn(
                INVOKEINTERFACE,
                actionPerformedInterfaceInternalName,
                "actionPerformed",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                true // interface method
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float positionGetXAlignOffset(Object position) {
        //     return ((positionClass)position).getXAlignOffset();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "positionGetXAlignOffset",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                positionInternalName,
                "getXAlignOffset",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float positionGetYlignOffset(Object position) {
        //     return ((positionClass)position).getYAlignOffset();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "positionGetYAlignOffset",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                positionInternalName,
                "getYAlignOffset",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public PositionAPI positionSetXAlignOffset(Object position, float offset) {
        //     return ((positionClass)position).setXAlignOffset(offset);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "positionSetXAlignOffset",
                "(Ljava/lang/Object;F)" + positionAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);

            mv.visitVarInsn(FLOAD, 2);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                positionInternalName,
                "setXAlignOffset",
                "(F)" + positionClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public PositionAPI positionSetYAlignOffset(Object position, float offset) {
        //     return ((positionClass)position).getYAlignOffset(offset);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "positionSetYAlignOffset",
                "(Ljava/lang/Object;F)" + positionAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);
            mv.visitVarInsn(FLOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                positionInternalName,
                "setYAlignOffset",
                "(F)" + positionClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public PositionAPI labelAutoSize(Object label) {
        //     return ((labelClass)label).autoSize();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "labelAutoSize",
                "(Ljava/lang/Object;)" + positionAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, labelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                labelInternalName,
                "autoSize",
                "()" + positionClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void buttonSetListener(Object button, Object listener) {
        //     ((buttonClass)button).setListener(listener);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "buttonSetListener",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, buttonClassInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                buttonClassInternalName,
                "setListener",
                "(" + actionListenerInterfaceDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object buttonGetListener(Object button) {
        //     ((buttonClass)button).getListener();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "buttonGetListener",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, buttonClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                buttonClassInternalName,
                "getListener",
                "()" + actionListenerInterfaceDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object buttonGetRendererPanel(Object button) {
        //     ((buttonClass)button).getRendererPanel();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "buttonGetRendererPanel",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, buttonClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                buttonClassInternalName,
                "getRendererPanel",
                "()" + buttonRendererPanelDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void buttonSetRendererPanel(Object button, Object rendererPanel) {
        //     ((buttonClass)button).setRendererPanel((rendererPanelClass)rendererPanel);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "buttonSetRendererPanel",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, buttonClassInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, buttonRendererPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                buttonClassInternalName,
                "setRendererPanel",
                "(" + buttonRendererPanelDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object uiComponentGetTooltip(Object uiComponent) {
        //     ((uiComponentClass)uiComponent).getTooltip();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiComponentGetTooltip",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "getTooltip",
                "()" + tooltipDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void showTooltip(Object uiComponent, Object tooltip) {
        //     ((showToolTipInterface)uiComponent).showTooltip(tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "showTooltip",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, showTooltipInterfaceInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEINTERFACE,
                showTooltipInterfaceInternalName,
                "showTooltip",
                "(Ljava/lang/Object;)V",
                true // interface method
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void hideTooltip(Object uiComponent, Object tooltip) {
        //     ((showToolTipInterface)uiComponent).hideTooltip(tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "hideTooltip",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, showTooltipInterfaceInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEINTERFACE,
                showTooltipInterfaceInternalName,
                "hideTooltip",
                "(Ljava/lang/Object;)V",
                true // interface method
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setTooltipOffsetFromCenter(Object uiComponent, float xPad, float yPad) {
        //     ((uiComponentClass)uiComponent).setTooltipOffsetFromCenter(xPad, yPad);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setTooltipOffsetFromCenter",
                "(Ljava/lang/Object;FF)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);
            mv.visitVarInsn(FLOAD, 2);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setTooltipOffsetFromCenter",
                "(FF)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setTooltipPositionRelativeToAnchor(Object uiComponent, float xPad, float yPad, Object anchor) {
        //     ((setTooltipInterface)uiComponent).setTooltipPositionRelativeToAnchor(xPad, yPad, (uiComponentClass)anchor);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setTooltipPositionRelativeToAnchor",
                "(Ljava/lang/Object;FFLjava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, setTooltipInterfaceInternalName);
            mv.visitVarInsn(FLOAD, 2);
            mv.visitVarInsn(FLOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitTypeInsn(CHECKCAST, uiComponentInterfaceAInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                setTooltipInterfaceInternalName,
                "setTooltipPositionRelativeToAnchor",
                "(FF" + uiComponentInterfaceADesc + ")V",
                true // interface method
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIComponentAPI getContents(Object tooltip) {
        //     return tooltip.getContents();
        // }
        {   
            String returnDesc = Type.getDescriptor(RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getContents", StandardTooltipV2.class)));
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getContents",
                "(Ljava/lang/Object;)" + uiComponentApiDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                toolTipInternalName,
                "getContents",
                "()" + returnDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setSlideData(Object uiComponent, float xOffset, float yOffset, float durationIn, float durationOut) {
        //     ((uiComponentClass)uiComponent).setSlideData(xOffset, yOffset, durationIn, durationOut);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setSlideData",
                "(Ljava/lang/Object;FFFF)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);
            mv.visitVarInsn(FLOAD, 2);
            mv.visitVarInsn(FLOAD, 3);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitVarInsn(FLOAD, 5);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setSlideData",
                "(FFFF)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void slideIn(Object uiComponent) {
        //     ((uiComponentClass)uiComponent).slideIn();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "slideIn",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "slideIn",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void slideOut(Object uiComponent) {
        //     ((uiComponentClass)uiComponent).slideOut();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "slideOut",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "slideOut",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void forceSlideIn(Object uiComponent) {
        //     ((uiComponentClass)uiComponent).forceSlideIn();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "forceSlideIn",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "forceSlideIn",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void forceSlideOut(Object uiComponent) {
        //     ((uiComponentClass)uiComponent).forceSlideOut();
        // }
        {
            Object forceSlideOutMethod = RolfLectionUtil.getMethod("forceSlideOut", uiComponentClass, 0);
            String forceSlideOutDesc = Type.getMethodDescriptor(forceSlideOutMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "forceSlideOut",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "forceSlideOut",
                forceSlideOutDesc,
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isSliding(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).isSliding();
        // }
        {
            Object isSlidingMethod = RolfLectionUtil.getMethod("isSliding", uiComponentClass, 0);
            String isSlidingDesc = Type.getMethodDescriptor(isSlidingMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isSliding",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "isSliding",
                isSlidingDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isSlidIn(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).isSlidIn();
        // }
        {
            Object isSlidInMethod = RolfLectionUtil.getMethod("isSlidIn", uiComponentClass, 0);
            String isSlidInDesc = Type.getMethodDescriptor(isSlidInMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isSlidIn",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "isSlidIn",
                isSlidInDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isSlidOut(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).isSlidOut();
        // }
        {
            Object isSlidOutMethod = RolfLectionUtil.getMethod("isSlidOut", uiComponentClass, 0);
            String isSlidOutDesc = Type.getMethodDescriptor(isSlidOutMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isSlidOut",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "isSlidOut",
                isSlidOutDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isSlidingIn(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).isSlidingIn();
        // }
        {
            Object isSlidingInMethod = RolfLectionUtil.getMethod("isSlidingIn", uiComponentClass, 0);
            String isSlidingInDesc = Type.getMethodDescriptor(isSlidingInMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isSlidingIn",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "isSlidingIn",
                isSlidingInDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isEnabled(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).isEnabled();
        // }
        {
            Object isEnabledMethod = RolfLectionUtil.getMethod("isEnabled", uiComponentClass, 0);
            String isEnabledDesc = Type.getMethodDescriptor(isEnabledMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isEnabled",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "isEnabled",
                isEnabledDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean setEnabled(Object uiComponent, boolean enabled) {
        //     return ((uiComponentClass)uiComponent).setEnabled(enabled);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setEnabled",
                "(Ljava/lang/Object;Z)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setEnabled",
                "(Z)Z",
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float getOpacity(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).getOpacity();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getOpacity",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "getOpacity",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setOpacity(Object uiComponent, float opacity) {
        //     ((uiComponentClass)uiComponent).setOpacity(opacity);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setOpacity",
                "(Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);
            mv.visitVarInsn(FLOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setOpacity",
                "(F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setMouseOverPad(Object uiComponent, float pad1, float pad2, float pad3, float pad4) {
        //     ((uiComponentClass)uiComponent).setMouseOverPad(pad1, pad2, pad3, pad4);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setMouseOverPad",
                "(Ljava/lang/Object;FFFF)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);
            mv.visitVarInsn(FLOAD, 2);
            mv.visitVarInsn(FLOAD, 3);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitVarInsn(FLOAD, 5);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setMouseOverPad",
                "(FFFF)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Fader getMouseoverHighlightFader(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).getMouseoverHighlightFader();
        // }
        {           
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getMouseoverHighlightFader",
                "(Ljava/lang/Object;)" + faderDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "getMouseoverHighlightFader",
                "()" + faderDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI findTopAncestor(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).findTopAncestor();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "findTopAncestor",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "findTopAncestor",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI labelGetParent(Object label) {
        //     return ((labelClass)label).getParent();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "labelGetParent",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, labelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                labelInternalName,
                "getParent",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI getParent(Object uiComponent) {
        //     return ((uiComponentClass)uiComponent).getParent();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getParent",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "getParent",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void setParent(Object uiComponent, Object toSet) {
        //     return ((uiComponentClass)uiComponent).setParent((uiPanelClass)toSet);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "setParent",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, uiPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "setParent",
                "(" + uiPanelClassDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public List<UIComponentAPI> getChildrenNonCopy(UIComponentAPI parent) {
        //     if (parent instanceof uiPanelClass) {
        //         return ((uiPanelClass)parent).getChildrenNonCopy();
        //     }
        //     return null;
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getChildrenNonCopy",
                "(" + uiComponentApiDesc + ")Ljava/util/List;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            
            mv.visitTypeInsn(INSTANCEOF, uiPanelInternalName);
            
            Label ifInstanceOf = new Label();
            mv.visitJumpInsn(IFNE, ifInstanceOf);
            
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            
            mv.visitLabel(ifInstanceOf);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiPanelInternalName);
            
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiPanelInternalName,
                "getChildrenNonCopy",
                "()Ljava/util/List;",
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public List<UIComponentAPI> getChildrenNonCopy(UIPanelAPI uiPanel) {
        //     return ((uiPanelClass)uiPanel).getChildrenNonCopy();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getChildrenNonCopy",
                "(" + uiPanelAPIDesc + ")Ljava/util/List;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiPanelInternalName,
                "getChildrenNonCopy",
                "()Ljava/util/List;",
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public List<UIComponentAPI> getChildrenCopy(UIPanelAPI uiPanel) {
        //     return ((uiPanelClass)uiPanel).getChildrenCopy();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getChildrenCopy",
                "(" + uiPanelAPIDesc + ")Ljava/util/List;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiPanelInternalName,
                "getChildrenCopy",
                "()Ljava/util/List;",
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void clearChildren(Object uiPanel) {
        //     return ((uiPanelClass)uiPanel).clearChildren();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "clearChildren",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiPanelInternalName,
                "clearChildren",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void confirmDialogDismiss(Object confirmDialog, int confirmOrCancel) {
        //     ((confirmDialogClass)confirmDialog).dismiss(confirmOrCancel);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogDismiss",
                "(Ljava/lang/Object;I)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);

            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "dismiss",
                "(I)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIComponentAPI confirmDialogGetHolo(Object confirmDialog) {
        //     return ((confirmDialogClass)confirmDialog).getHolo();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogGetHolo",
                "(Ljava/lang/Object;)" + uiComponentApiDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "getHolo",
                "()" + confirmDialogHoloDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public ButtonAPI confirmDialogGetButton(Object confirmDialog, int button) {
        //     return ((confirmDialogClass)confirmDialog).getButton(button);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogGetButton",
                "(Ljava/lang/Object;I)" + buttonAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "getButton",
                "(I)" + buttonClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public LabelAPI confirmDialogGetLabel(Object confirmDialog) {
        //     return ((confirmDialogClass)confirmDialog).getLabel();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogGetLabel",
                "(Ljava/lang/Object;)" + labelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "getLabel",
                "()" + labelDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean isNoiseOnConfirmDismiss(Object confirmDialog) {
        //     return ((confirmDialogClass)confirmDialog).isNoiseOnConfirmDismiss();
        // }
        {
            String isNoiseOnDismissDesc = Type.getMethodDescriptor(RolfLectionUtil.getMethod("isNoiseOnConfirmDismiss", confirmDialogClass));
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "isNoiseOnConfirmDismiss",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "isNoiseOnConfirmDismiss",
                isNoiseOnDismissDesc,
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void confirmDialogShow(Object confirmDialog, float durationIn, float durationOut) {
        //     ((confirmDialogClass)confirmDialog).show(durationIn, durationOut);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogShow",
                "(Ljava/lang/Object;FF)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);
            mv.visitVarInsn(FLOAD, 2);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "show",
                "(FF)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI confirmDialogGetInnerPanel(Object confirmDialog) {
        //     if (instanceof confirmDialogSuperClass) {
        //         return ((confirmDialgSuperClass)confirmDialog).getInnerPanel();
        //     }
        //     return null;
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogGetInnerPanel",
                "(Ljava/lang/Object;)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, confirmDialogInternalName);
            
            Label ifInstanceOf = new Label();
            mv.visitJumpInsn(IFNE, ifInstanceOf);
            
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            
            mv.visitLabel(ifInstanceOf);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, confirmDialogInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                confirmDialogInternalName,
                "getInnerPanel",
                "()" + uiPanelClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Map<ButtonAPI, Object> optionPanelGetButtonToItemMap(OptionPanelAPI optionPanel) {
        //     return ((optionPanelClass)optionPanel).getButtonToItemMap();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "optionPanelGetButtonToItemMap",
                "(" + optionPanelApiDesc + ")" + mapDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, optionPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                optionPanelInternalName,
                "getButtonToItemMap",
                "()" + mapDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object optionPanelItemGetOptionData(Object optionPanelItem) {
        //     return ((optionPanelItemClass)optionPanelItem).getOptionData();
        // }
        {
            String methodName = RolfLectionUtil.getMethodName(RolfLectionUtil.getMethodsByReturnType(optionPanelItemClass, Object.class).get(0));
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "optionPanelItemGetOptionData",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, optionPanelItemInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                optionPanelItemInternalName,
                methodName,
                "()" + Type.getDescriptor(Object.class),
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public List<UIComponent> listPanelGetItems(Object listPanel) {
        //     if (listPanel instanceof listPanelClass) {
        //         return ((listPanelClass)listPanel).getItems();
        //     }
        //     return null;
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "listPanelGetItems",
                "(Ljava/lang/Object;)" + listDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, listPanelInternalName);
            
            Label ifInstanceOf = new Label();
            mv.visitJumpInsn(IFNE, ifInstanceOf);
            
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            
            mv.visitLabel(ifInstanceOf);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, listPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                listPanelInternalName,
                "getItems",
                "()" + listDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void listPanelAddItem(Object listPanel, UIComponentAPI item) {
        //     ((listPanelClass)listPanel).addItem(item);
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "listPanelAddItem",
                "(Ljava/lang/Object;" + uiComponentApiDesc + ")V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, listPanelInternalName);
            mv.visitVarInsn(ALOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                listPanelInternalName,
                "addItem",
                "(" + uiComponentInterfaceADesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void listPanelClear(Object listPanel) {
        //     ((listPanelClass)listPanel).clear();
        // }
        {   
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "listPanelClear",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, listPanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                listPanelInternalName,
                "clear",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public List<Object> uiTableGetRows(UITable table) {
        //     return table.getRows();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableGetRows",
                "(" + uiTableDesc + ")" + listDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "getRows",
                "()" + listDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableAddRow(UITable table, Object row) {
        //     table.addRow(row);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableAddRow",
                "(" + uiTableDesc + "Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "addRow",
                "(" + uiTableRowDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableRemoveRow(UITable table, Object row) {
        //     table.removeRow(row);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRemoveRow",
                "(" + uiTableDesc + "Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "removeRow",
                "(" + uiTableRowDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object uiTableGetRowForData(UITable table, Object data) {
        //     return table.getRowForData(data);
        // }
        {
            Object getRowForDataMethod = RolfLectionUtil.getMethod("getRowForData", UITable.class, 1);
            String getRowForDataDesc = Type.getMethodDescriptor(getRowForDataMethod);
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableGetRowForData",
                "(" + uiTableDesc + "Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "getRowForData",
                getRowForDataDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI uiTableRowGetCol(Object row, int col) {
        //     return ((UITableRow)row).getCol(col);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRowGetCol",
                "(Ljava/lang/Object;I)" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableRowInternalName,
                "getCol",
                "(I)" + uiPanelAPIDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public ButtonAPI uiTableRowGetButton(Object row) {
        //     return ((UITableRow)row).getButton();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRowGetButton",
                "(Ljava/lang/Object;)" + buttonAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableRowInternalName,
                "getButton",
                "()" + buttonClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableRowSetButton(Object row, Object button) {
        //     ((UITableRow)row).setButton((Button)button);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRowSetButton",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, buttonClassInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableRowInternalName,
                "setButton",
                "(" + buttonClassDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object uiTableRowGetData(Object row) {
        //     return ((UITableRow)row).getData();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRowGetData",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableRowInternalName,
                "getData",
                "()Ljava/lang/Object;",
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableRowSetData(Object row, Object data) {
        //     ((UITableRow)row).setData(data);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableRowSetData",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);

            mv.visitVarInsn(ALOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableRowInternalName,
                "setData",
                "(Ljava/lang/Object;)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object uiTableGetSelected(UITable table) {
        //     return table.getSelected();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableGetSelected",
                "(" + uiTableDesc + ")Ljava/lang/Object;" ,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "getSelected",
                "()" + uiTableRowDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableSelect(UITable table, Object row, Object inputEvent) {
        //     table.select(row, inputEvent);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableSelect",
                "(" + uiTableDesc + "Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);
            mv.visitVarInsn(ALOAD, 3);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "select",
                "(" + uiTableRowDesc + "Ljava/lang/Object;)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void uiTableSelect(UITable table, Object row, Object inputEvent, boolean notifyDelegate) {
        //     table.select(row, inputEvent, notifyDelegate);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableSelect",
                "(" + uiTableDesc + "Ljava/lang/Object;Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, uiTableRowInternalName);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "select",
                "(" + uiTableRowDesc + "Ljava/lang/Object;Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public UIPanelAPI uiTableGetList(UITable table) {
        //     return table.getList();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiTableGetList",
                "(" + uiTableDesc + ")" + uiPanelAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiTableInternalName,
                "getList",
                "()" + listPanelDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetRenderSchematic(Object imagePanel, boolean renderSchematic) {
        //     ((imagePanelClass)imagePanel).setRenderSchematic(renderSchematic);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetRenderSchematic",
                "(Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setRenderSchematic",
                "(Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelAutoSize(Object imagePanel) {
        //     ((imagePanelClass)imagePanel).autoSize();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelAutoSize",
                "(Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "autoSize",
                "()V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelAutoSizeToWidth(Object imagePanel, float width) {
        //     ((imagePanelClass)imagePanel).autoSizeToWidth(width);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelAutoSizeToWidth",
                "(Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(FLOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "autoSizeToWidth",
                "(F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelAutoSizeToHeight(Object imagePanel, float height) {
        //     ((imagePanelClass)imagePanel).autoSizeToHeight(height);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelAutoSizeToHeight",
                "(Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(FLOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "autoSizeToHeight",
                "(F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetStretch(Object imagePanel, boolean stretch) {
        //     ((imagePanelClass)imagePanel).setStretch(stretch);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetStretch",
                "(Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setStretch",
                "(Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Sprite imagePanelGetSprite(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).getSprite();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelGetSprite",
                "(Ljava/lang/Object;)" + spriteDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "getSprite",
                "()" + spriteDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetSprite(Object imagePanel, Sprite sprite, boolean isResize) {
        //     ((imagePanelClass)imagePanel).setSprite(sprite, isResize);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetSprite",
                "(Ljava/lang/Object;" + spriteDesc + "Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 3);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setSprite",
                "(" + spriteDesc + "Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public String imagePanelGetSpriteName(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).getSpriteName();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelGetSpriteName",
                "(Ljava/lang/Object;)" + stringDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "getSpriteName",
                "()" + stringDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetSprite(Object imagePanel, String spriteName, boolean isResize) {
        //     ((imagePanelClass)imagePanel).setSprite(spriteName, isResize);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetSprite",
                "(Ljava/lang/Object;" + stringDesc + "Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 3);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setSprite",
                "(" + stringDesc + "Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Color imagePanelGetBorderColor(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).getBorderColor();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelGetBorderColor",
                "(Ljava/lang/Object;)" + colorDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "getBorderColor",
                "()" + colorDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetBorderColor(Object imagePanel, Color borderColor) {
        //     ((imagePanelClass)imagePanel).setBorderColor(borderColor);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetBorderColor",
                "(Ljava/lang/Object;" + colorDesc + ")V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ALOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setBorderColor",
                "(" + colorDesc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean imagePanelIsWithOutline(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).isWithOutline();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelIsWithOutline",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "isWithOutline",
                "()Z",
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetWithOutline(Object imagePanel, boolean isWithOutline) {
        //     ((imagePanelClass)imagePanel).setWithOutline(isWithOutline);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetWithOutline",
                "(Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setWithOutline",
                "(Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean imagePanelIsTexClamp(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).isTexClamp();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelIsTexClamp",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "isTexClamp",
                "()Z",
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetTexClamp(Object imagePanel, boolean texClamp) {
        //     ((imagePanelClass)imagePanel).setTexClamp(texClamp);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetTexClamp",
                "(Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setTexClamp",
                "(Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public boolean imagePanelIsForceNoRounding(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).isForceNoRounding();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelIsForceNoRounding",
                "(Ljava/lang/Object;)Z",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "isForceNoRounding",
                "()Z",
                false
            );

            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void imagePanelSetForceNoRounding(Object imagePanel, boolean forceNoRounding) {
        //     return ((imagePanelClass)imagePanel).setForceNoRounding(forceNoRounding);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelSetForceNoRounding",
                "(Ljava/lang/Object;Z)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);
            mv.visitVarInsn(ILOAD, 2);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "setForceNoRounding",
                "(Z)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public float imagePanelGetOriginalAR(Object imagePanel) {
        //     return ((imagePanelClass)imagePanel).getOriginalAR();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "imagePanelGetOriginalAR",
                "(Ljava/lang/Object;)F",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, imagePanelInternalName);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                imagePanelInternalName,
                "getOriginalAR",
                "()F",
                false
            );

            mv.visitInsn(FRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }


        // public PositionAPI positionRelativeTo(PositionAPI position, PositionAPI targetRelativePos float var2, float var3, float var4, float var5, float var6, float var7) {
        //     return ((positionClass)position).relativeTo((positionClass)targetRelativePos, var2, var3, var4, var5, var6, var7);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "positionRelativeTo",
                "(" + positionAPIDesc + positionAPIDesc + "FFFFFF)" + positionAPIDesc,
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, positionInternalName);
            mv.visitVarInsn(FLOAD, 3);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitVarInsn(FLOAD, 5);
            mv.visitVarInsn(FLOAD, 6);
            mv.visitVarInsn(FLOAD, 7);
            mv.visitVarInsn(FLOAD, 8);

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                positionInternalName,
                "relativeTo",
                "(" + positionClassDesc + "FFFFFF)" + positionClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipAbove(Object tooltip, Object uiComponent) {
        //     StandardTooltipV2Expandable.addTooltipAbove((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipAbove",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipAbove",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipBelow(Object tooltip, Object uiComponent) {
        //     StandardTooltipV2Expandable.addTooltipBelow((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipBelow",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipBelow",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipRight(Object tooltip, Object uiComponent) {
        //     StandardTooltipV2Expandable.addTooltipRight((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipRight",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipRight",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipLeft(Object tooltip, Object uiComponent) {
        //     StandardTooltipV2Expandable.addTooltipLeft((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipLeft",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipLeft",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + ")V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipAbove(Object tooltip, Object uiComponent, float padding) {
        //     StandardTooltipV2Expandable.addTooltipAbove((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip, padding);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipAbove",
                "(Ljava/lang/Object;Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipAbove",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + "F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipBelow(Object tooltip, Object uiComponent, float padding) {
        //     StandardTooltipV2Expandable.addTooltipBelow((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip, padding);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipBelow",
                "(Ljava/lang/Object;Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipBelow",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + "F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipRight(Object tooltip, Object uiComponent, float padding) {
        //     StandardTooltipV2Expandable.addTooltipRight((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip, padding);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipRight",
                "(Ljava/lang/Object;Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipRight",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + "F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public void addTooltipLeft(Object tooltip, Object uiComponent, float padding) {
        //     StandardTooltipV2Expandable.addTooltipLeft((addTooltipUiComponentClass)uiComponent, (StandardTooltipV2)tooltip, padding);
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "addTooltipLeft",
                "(Ljava/lang/Object;Ljava/lang/Object;F)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, addTooltipUiComponentInternalName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, standardTooltipV2InternalName);
            mv.visitVarInsn(FLOAD, 3);

            mv.visitMethodInsn(
                INVOKESTATIC,
                standardTooltipV2ExpandableInternalName,
                "addTooltipLeft",
                "(" + addTooltipUiComponentDesc + standardTooltipV2Desc + "F)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return new Class<?>[] {
            Inherit.inheritCl.define(cw.toByteArray(), "rolflectionlib.util.UiUtilInterface"),
            uiPanelClass,
            uiComponentClass,
            confirmDialogClass,
            actionPerformedInterface,
            dialogDismissedInterface,
            listPanelClass
        };
    }

    public static final UiUtilInterface utils;
    public static final Class<?> uiPanelClass;
    public static final Class<?> uiComponentClass;
    public static final Class<?> confirmDialogClass;

    public static final Class<?> actionPerformedInterface;
    public static final Class<?> dialogDismissedInterface;

    public static final Class<?> weaponPickerListClass;

    public static final VarHandle listPanelMapVarHandle;
    public static final VarHandle customPanelPluginVarHandle;

    public static final VarHandle fighterPickerHeightVarHandle;
    public static final VarHandle weaponPickerHeightVarHandle;

    public static final VarHandle tooltipFighterSpecVarHandle;
    public static final VarHandle tooltipWeaponSpecVarHandle;

    private static final CallSite dialogDismissedCallSite;
    private static final CallSite actionPerformedCallSite;

    static {
        try {
            Class<?>[] result = implementUiUtilInterface();
            utils = (UiUtilInterface) RolfLectionUtil.instantiateClass(result[0].getConstructors()[0]);

            uiPanelClass = result[1];
            uiComponentClass = result[2];
            confirmDialogClass = result[3];
            actionPerformedInterface = result[4];
            dialogDismissedInterface = result[5];

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> listPanelClass = result[6];
            listPanelMapVarHandle = MethodHandles.privateLookupIn(listPanelClass, lookup).findVarHandle(
                listPanelClass,
                RolfLectionUtil.getFieldName(RolfLectionUtil.getFieldByType(listPanelClass, Map.class)),
                Map.class
            );
            Pair<Class<?>, String[]> weaponPickerData = getWeaponPickerData(listPanelClass);
            weaponPickerListClass = weaponPickerData.one;

            Class<?> fighterTooltipClass = null;
            Class<?> weaponTooltipClass = null;
            for (Class<?> cls : CargoTooltipFactory.class.getNestMembers()) {
                if (cls.isAnonymousClass() && cls.getSuperclass() == StandardTooltipV2Expandable.class) {
                    for (Class<?> paramType : RolfLectionUtil.getConstructorParamTypesSingleConstructor(cls)) {
                        if (paramType == FighterWingSpec.class) {
                            fighterTooltipClass = cls;
                            break;
                        } else if (paramType == BaseWeaponSpec.class) {
                            weaponTooltipClass = cls;
                            break;
                        }
                    }
                }
            }
            tooltipFighterSpecVarHandle = MethodHandles.privateLookupIn(fighterTooltipClass, lookup).findVarHandle(
                fighterTooltipClass,
                RolfLectionUtil.getFieldName(RolfLectionUtil.getFieldByType(fighterTooltipClass, FighterWingSpec.class)),
                FighterWingSpec.class
            );
            tooltipWeaponSpecVarHandle = MethodHandles.privateLookupIn(weaponTooltipClass, lookup).findVarHandle(
                weaponTooltipClass,
                RolfLectionUtil.getFieldName(RolfLectionUtil.getFieldByType(weaponTooltipClass, BaseWeaponSpec.class)),
                BaseWeaponSpec.class
            );

            fighterPickerHeightVarHandle = MethodHandles.privateLookupIn(FighterPickerDialog.class, lookup).findVarHandle(
                FighterPickerDialog.class,
                weaponPickerData.two[0],
                float.class
            );
            weaponPickerHeightVarHandle = MethodHandles.privateLookupIn(WeaponPickerDialog.class, lookup).findVarHandle(
                WeaponPickerDialog.class,
                weaponPickerData.two[1],
                float.class
            );

            Class<?> customPanelClass = getCustomPanelClass();
            customPanelPluginVarHandle = MethodHandles.privateLookupIn(customPanelClass, lookup).findVarHandle(
                customPanelClass,
                RolfLectionUtil.getFieldName(RolfLectionUtil.getFieldByInterface(CustomUIPanelPlugin.class, customPanelClass)),
                CustomUIPanelPlugin.class
            );
            
            {
                Class<?> dialogDismissedParamClass = RolfLectionUtil.getMethodParamTypes(dialogDismissedInterface.getDeclaredMethods()[0])[0];

                MethodType actualSamMethodType = MethodType.methodType(void.class, dialogDismissedParamClass, int.class);
                MethodHandle implementationMethodHandle = lookup.findVirtual(DialogDismissedListenerProxy.class, "dialogDismissed", MethodType.methodType(void.class, Object.class, int.class));
                MethodType factoryType = MethodType.methodType(dialogDismissedInterface, DialogDismissedListenerProxy.class);
    
                dialogDismissedCallSite = LambdaMetafactory.metafactory(
                    lookup,
                    "dialogDismissed",
                    factoryType,
                    actualSamMethodType,
                    implementationMethodHandle,
                    actualSamMethodType
                );
            }

            {
                MethodType actualSamMethodType = MethodType.methodType(void.class, Object.class, Object.class);
                MethodHandle implementationMethodHandle = lookup.findVirtual(ActionListenerProxy.class, "actionPerformed", MethodType.methodType(void.class, Object.class, Object.class));
                MethodType factoryType = MethodType.methodType(actionPerformedInterface, ActionListenerProxy.class);
                
                actionPerformedCallSite = LambdaMetafactory.metafactory(
                    lookup,
                    "actionPerformed",
                    factoryType,
                    actualSamMethodType,
                    implementationMethodHandle,
                    actualSamMethodType
                );
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getCore(CampaignUIAPI campaignUI, InteractionDialogAPI interactionDialog) {
        return interactionDialog == null ? utils.campaignUIgetCore(campaignUI) : utils.interactionDialogGetCore(interactionDialog);
    }

    public static void setButtonHook(ButtonAPI button, Runnable runBefore, Runnable runAfter) {
        Object oldListener = utils.buttonGetListener(button);

        utils.buttonSetListener(button, new ActionListener() {
            @Override
            public void actionPerformed(Object arg0, Object arg1) {
                runBefore.run();
                utils.actionPerformed(oldListener, arg0, arg1);
                runAfter.run();
            }
        }.getProxy());
    }

    public static List<ButtonAPI> getButtonChildren(UIPanelAPI parent) {
        List<ButtonAPI> result = new ArrayList<>();
        for (UIComponentAPI child : utils.getChildrenNonCopy(parent)) {
            if (child instanceof ButtonAPI btn) result.add(btn);
        }
        return result;
    }

    public static List<UIComponentAPI> getChildrenRecursive(UIComponentAPI parentPanel) {
        List<UIComponentAPI> list = new ArrayList<>();
        collectChildren(parentPanel, list);
        return list;
    }

    private static void collectChildren(UIComponentAPI parent, List<UIComponentAPI> list) {
        List<UIComponentAPI> children = UiUtil.utils.getChildrenNonCopy(parent);

        if (children != null) {
            for (UIComponentAPI child : children) {
                list.add(child);
                collectChildren(child, list);
            }
        }
    }

    public static abstract class ActionListener {
        private final Object listener;

        public ActionListener() {
            try {
                listener = actionPerformedCallSite.getTarget().invoke(new ActionListenerProxy(this));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public abstract void actionPerformed(Object inputEvent, Object uiElement);

        public Object getProxy() {
            return this.listener;
        }
    }

    public static abstract class DialogDismissedListener {
        protected final Object listener;

        public DialogDismissedListener() {
            try {
                listener = dialogDismissedCallSite.getTarget().invoke(new DialogDismissedListenerProxy(this));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public abstract void dialogDismissed(Object arg0, int arg1);

        public Object getProxy() {
            return this.listener;
        }
    }


    private static class DialogDismissedListenerProxy {
        private final DialogDismissedListener listener;

        public DialogDismissedListenerProxy(DialogDismissedListener listener) {
            this.listener = listener;
        }
        @SuppressWarnings("unused")
        public void dialogDismissed(Object arg0, int arg1) {
            this.listener.dialogDismissed(arg0, arg1);
        };
    }

    private static class ActionListenerProxy {
        protected final ActionListener listener;

        public ActionListenerProxy(ActionListener listener) {
            this.listener = listener;
        }
        @SuppressWarnings("unused")
        public void actionPerformed(Object arg0, Object arg1) {
            listener.actionPerformed(arg0, arg1);
        }
    }

    private static Class<?> getOptionPanelClass(Class<?> interactionDialogClass) {
        final String[] fieldName = {null};

        new ClassReader(RolFileUtil.getClassBytes(interactionDialogClass)).accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!name.equals("getOptionPanel")) return null;

                return new MethodVisitor(ASM9) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == GETFIELD) {
                            fieldName[0] = fld;
                        }
                    }
                };
            }
        }, 0);

        return RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByName(fieldName[0], interactionDialogClass));
    }

    private static Class<?> getImagePanelClass() throws ClassNotFoundException {
        final String[] className = {null};

        new ClassReader(RolFileUtil.getClassBytes(StandardTooltipV2Expandable.class)).accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!name.equals("addImage") || !desc.split(";")[1].equals("FF)V")) return null;

                return new MethodVisitor(ASM9) {
                    @Override
                    public void visitTypeInsn(int opcode, String name) {
                        if (opcode == NEW) {
                            className[0] = name;
                        }
                    }
                };
            }
        }, 0);

        return Class.forName(className[0].replace("/", "."));
    }

    private static String[] getZoomTrackerMethodNames(Class<?> zoomTrackerClass) {
        final String[] foundNames = {null, null};
        final String[] maxZoomFactorFieldName = {null};

        ClassReader cr = new ClassReader(RolFileUtil.getClassBytes(zoomTrackerClass));
        cr.accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!desc.equals("()F")) return null;

                return new MethodVisitor(ASM9) {
                    int fieldGets = 0;
                    int fcmps = 0;
                    int fReturns = 0;

                    String lastFieldName;
                    String secondCompareField;
    
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == GETFIELD && fldDesc.equals("F")) {
                            fieldGets++;
                            lastFieldName = fld;
                        }
                    }
    
                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == FCMPG || opcode == FCMPL) {
                            fcmps++;
                            if (fcmps == 2) {
                                secondCompareField = lastFieldName;
                            }
                        }
                        if (opcode == FRETURN) {
                            fReturns++;
                        }
                    }
    
                    @Override
                    public void visitEnd() {
                        if (fieldGets >= 3 && fcmps >= 2 && fReturns == 1) {
                            foundNames[0] = name;
                            maxZoomFactorFieldName[0] = secondCompareField;
                        }
                    }
                };
            }
        }, 0);

        cr.accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!desc.equals("()F") || access != ACC_PUBLIC) return null;

                return new MethodVisitor(ASM9) {
                    int fieldGets = 0;
                    int fReturns = 0;
                    int visitFieldInsns = 0;
                    int visitMethodInsns = 0;

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        visitMethodInsns++;
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        visitFieldInsns++;
                        if (opcode == GETFIELD && fldDesc.equals("F") && fld.equals(maxZoomFactorFieldName[0])) {
                            fieldGets++;
                        }
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == FRETURN) {
                            fReturns++;
                        }
                    }

                    @Override
                    public void visitEnd() {
                        if (fieldGets == 1 && fReturns == 1 && visitFieldInsns == 1 & visitMethodInsns == 0) {
                            foundNames[1] = name;
                        }
                    }
                };
            }
        }, 0);

        return foundNames;
    }

    private static Class<?> getCustomPanelClass() throws ClassNotFoundException {
        final String[] names = {null};

        new ClassReader(RolFileUtil.getClassBytes(EventsPanel.class)).accept(new ClassVisitor(ASM9) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!name.equals("<init>")) return null;

                return new MethodVisitor(ASM9) {
                    private int putFields = 0;

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == PUTFIELD) putFields+= 1;

                        if (putFields == 4) {
                            names[0] = fldDesc;
                        }
                    }
                };
            }
        }, 0);

        return Class.forName(names[0].replace("/", ".").substring(1, names[0].length() - 1));
    }
    
    private static Pair<Class<?>, String[]> getWeaponPickerData(Class<?> listPanelClass) throws Exception {
        final Class<?>[] cls = {null};
        final String[] heightNames = {null, null};

        new ClassReader(RolFileUtil.getClassBytes(FighterPickerDialog.class)).accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (name.equals("updateUI"))
                return new MethodVisitor(ASM9) {
                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        if (opcode == NEW && cls[0] == null) {
                            try {
                                Class<?> clazz = Class.forName(type.replace("/", "."));
                                if (listPanelClass != clazz) cls[0] = clazz;

                            } catch (ClassNotFoundException ignored) {}
                        }
                    }
                };
                else return new MethodVisitor(ASM9) {
                    private int fieldGets = 0;
                    private int visitMethods = 0;
                    private boolean aload = false;

                    @Override
                    public void visitVarInsn(int opcode, int index) {
                        if (opcode == ALOAD) {
                            if (index == 0) {
                                aload = true;
                            } else {
                                aload = false;
                            }
                        }
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if (fieldGets == 0 && aload) {
                            switch(name) {
                                case "getHolo":
                                case "getFanOut":
                                case "getBrightness":
                                    visitMethods++;
                                default:
                                    return;
                            }
                        }
                        
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == GETFIELD) {
                            fieldGets++;
                            if (fieldGets == 1 && visitMethods == 3) {
                                heightNames[0] = fld;
                            }
                        }
                    }

                };
            }
        }, 0);

        new ClassReader(RolFileUtil.getClassBytes(WeaponPickerDialog.class)).accept(new ClassVisitor(ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                return new MethodVisitor(ASM9) {
                    private int fieldGets = 0;
                    private int visitMethods = 0;
                    private int aloads = 0;

                    @Override
                    public void visitVarInsn(int opcode, int index) {
                        if (opcode == ALOAD && index == 0) aloads++;
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if (fieldGets == 0 && aloads == 1) {
                            switch(name) {
                                case "getHolo":
                                case "getFanOut":
                                case "getBrightness":
                                    visitMethods++;
                                default:
                                    return;
                            }
                        }
                        
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == GETFIELD) {
                            fieldGets++;
                            if (fieldGets == 1 && aloads == 2 && visitMethods == 3) {
                                heightNames[1] = fld;
                            }
                        }
                    }

                };
            }
        }, 0);

        return new Pair<Class<?>, String[]>(cls[0], new String[] {heightNames[0], heightNames[1]}); 
    }

    public static void init() {} // called to load this class and generate the interface class in onApplicationLoad
}
