package rolflectionlib.ui;

import java.util.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

import org.objectweb.asm.*;

import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignState;
import com.fs.starfarer.campaign.command.AdminPickerDialog;
import com.fs.starfarer.campaign.comms.v2.EventsPanel;
import com.fs.starfarer.ui.impl.StandardTooltipV2;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.util.RolFileUtil;
import rolflectionlib.util.RolfLectionUtil;

public class UiUtil implements Opcodes {
    public static interface UiUtilInterface {
        public Object interactionDialogGetCore(Object interactionDialog);
        public Object campaignUIgetCore(Object campaignUI);
        public UIPanelAPI coreGetCurrentTab(Object core);

        public void actionPerformed(Object listener, Object inputEvent, Object uiElement);

        public void buttonSetListener(Object button, Object listener);
        public Object buttonGetListener(Object button);
        
        public Fader uiComponentGetFader(Object uiComponent);
        public Object uiComponentGetTooltip(Object uiComponent);
        public void uiComponentShowTooltip(Object uiComponent, Object tooltip);
        public void uiComponentHideTooltip(Object uiComponent, Object tooltip);
        public void setTooltipOffsetFromCenter(Object uiComponent, float xPad, float yPad);
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

        public void setMouseOverPad(Object uiComponent, float pad1, float pad2, float pad3, float pad4);
        public Fader getMouseoverHighlightFader(Object uiComponent);

        public UIPanelAPI getParent(Object uiComponent);
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
        public Object confirmDialogGetHolo(Object confirmDialog);

        public Map<ButtonAPI, Object> optionPanelGetButtonToItemMap(OptionPanelAPI optionPanel);
        public Object optionPanelItemGetOptionData(Object optionItem);
        public List<UIComponentAPI> listPanelGetItems(Object listPanel); // custom method with instanceof check listPanelClass else return null
    }

    // With this we can implement the above interface and generate a class at runtime to call obfuscated class methods platform agnostically without reflection overhead
    private static Class<?>[] implementUiUtilInterface() throws Throwable {
        Class<?> coreClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getCore", CampaignState.class));
        Class<?> uiPanelClass = coreClass.getSuperclass().getSuperclass();
        Class<?> uiComponentClass = uiPanelClass.getSuperclass();
        Class<?> toolTipClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getTooltip", uiComponentClass));
        Class<?> interactionDialogClass = RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByName("encounterDialog", CampaignState.class));
        Class<?> buttonClass = RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByInterface(ButtonAPI.class, EventsPanel.class));
        Class<?> actionPerformedInterface = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getListener", buttonClass));
        Class<?> confirmDialogClass = AdminPickerDialog.class.getSuperclass();
        Class<?> listPanelClass = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getListAdmins", AdminPickerDialog.class));
        Class<?> dialogDismissedInterface = RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getDelegate", confirmDialogClass.getSuperclass()));

        Class<?> optionPanelClass = getOptionPanelClass(interactionDialogClass);
        Class<?> optionPanelItemClass = null;
        for (Class<?> cls : optionPanelClass.getClasses()) {
            if (RolfLectionUtil.getConstructorParamTypesSingleConstructor(cls).length == 4) {
                optionPanelItemClass = cls;
                break;
            }
        }

        String coreClassInternalName = Type.getInternalName(coreClass);
        String uiPanelInternalName = Type.getInternalName(uiPanelClass);
        String uiComponentInternalName = Type.getInternalName(uiComponentClass);
        String toolTipInternalName = Type.getInternalName(toolTipClass);
        String interactionDialogInternalName = Type.getInternalName(interactionDialogClass);
        String optionPanelInternalName = Type.getInternalName(optionPanelClass);
        String optionPanelItemInternalName = Type.getInternalName(optionPanelItemClass);
        String buttonClassInternalName = Type.getInternalName(buttonClass);
        String actionPerformedInterfaceInternalName = Type.getInternalName(actionPerformedInterface);
        String campaignStateInternalName = Type.getInternalName(CampaignState.class);
        String confirmDialogInternalName = Type.getInternalName(confirmDialogClass);
        String listPanelInternalName = Type.getInternalName(listPanelClass);

        String coreClassDesc = Type.getDescriptor(coreClass);
        String uiPanelClassDesc = Type.getDescriptor(uiPanelClass);
        String uiPanelAPIDesc = Type.getDescriptor(UIPanelAPI.class);
        String uiComponentApiDesc = Type.getDescriptor(UIComponentAPI.class);
        String buttonAPIDesc = Type.getDescriptor(ButtonAPI.class);
        String buttonClassDesc = Type.getDescriptor(buttonClass);
        String actionListenerInterfaceDesc = Type.getDescriptor(actionPerformedInterface);
        String tooltipDesc = Type.getDescriptor(toolTipClass);
        String labelAPIDesc = Type.getDescriptor(LabelAPI.class);
        String optionPanelApiDesc = Type.getDescriptor(OptionPanelAPI.class);

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
        
        // public Object interactionDialogGetCore(Object interactionDialog) {
        //     return ((interactionDialogClass)interactionDialog).getCoreUI();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "interactionDialogGetCore",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
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

        // public Object campaignUIgetCore(Object campaignUI) {
        //     return ((CampaignState)campaignUI).getCore();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "campaignUIgetCore",
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
                "getCore",
                "()" + coreClassDesc,
                false
            );

            mv.visitInsn(ARETURN);

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
                "()" + Type.getDescriptor(uiPanelClass),
                false
            );

            mv.visitInsn(ARETURN);

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

        // public Object uiComponentShowTooltip(Object uiComponent, Object tooltip) {
        //     ((uiComponentClass)uiComponent).showTooltip();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiComponentShowTooltip",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "showTooltip",
                "(Ljava/lang/Object;)V",
                false
            );

            mv.visitInsn(RETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        // public Object uiComponentHideTooltip(Object button, Object tooltip) {
        //     ((uiComponentClass)uiComponent).hideTooltip();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "uiComponentHideTooltip",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                null,
                null
            );
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, uiComponentInternalName);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                uiComponentInternalName,
                "hideTooltip",
                "(Ljava/lang/Object;)V",
                false
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

        // public void setMouseOverPad(Object uiComponent, float pad1, float pad2, float pad3, float pad4) {
        //     ((uiComponentClass)uiComponent).setMouseOverPad(pad1, pad2, pad3, pad4);
        // }
        {
            Object setMouseOverPadMethod = RolfLectionUtil.getMethod("setMouseOverPad", uiComponentClass, 4);
            String setMouseOverPadDesc = Type.getMethodDescriptor(setMouseOverPadMethod);
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
                setMouseOverPadDesc,
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
            Object getMouseoverHighlightFaderMethod = RolfLectionUtil.getMethod("getMouseoverHighlightFader", uiComponentClass);
            String getMouseoverHighlightFaderDesc = Type.getMethodDescriptor(getMouseoverHighlightFaderMethod);
            String faderDesc = Type.getDescriptor(Fader.class);
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
                getMouseoverHighlightFaderDesc,
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

        // public List<UIComponentAPI> getChildrenCopy(Object uiPanel) {
        //     return ((uiPanelClass)uiPanel).getChildrenCopy();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "getChildrenCopy",
                "(Ljava/lang/Object;)Ljava/util/List;",
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
            String labelDesc = Type.getDescriptor(RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getLabel", confirmDialogClass)));
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

        // public Object confirmDialogGetHolo(Object confirmDialog) {
        //     return ((confirmDialogClass)confirmDialog).getHolo();
        // }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "confirmDialogGetHolo",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
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
                "()" + Type.getDescriptor(RolfLectionUtil.getReturnType(RolfLectionUtil.getMethod("getHolo", confirmDialogClass))),
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
            String mapDesc = Type.getDescriptor(Map.class);
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

        // public UIPanelAPI listPanelGetItems(Object confirmDialog) {
        //     if (confirmDialog instanceof confirmDialogSuperClass) {
        //         return ((confirmDialgSuperClass)confirmDialog).getInnerPanel();
        //     }
        //     return null;
        // }
        {   
            String listDesc = Type.getDescriptor(List.class);
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

    public static final VarHandle listPanelMapVarHandle;

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
        ClassReader cr = new ClassReader(RolFileUtil.getClassBytes(interactionDialogClass));
        final String[] fieldName = {null};

        cr.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!name.equals("getOptionPanel")) return null;

                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == Opcodes.GETFIELD) {
                            fieldName[0] = fld;
                        }
                    }
                };
            }
        }, 0);

        return RolfLectionUtil.getFieldType(RolfLectionUtil.getFieldByName(fieldName[0], interactionDialogClass));
    }

    public static void init() {} // called to load this class and generate the interface class in onApplicationLoad
}
