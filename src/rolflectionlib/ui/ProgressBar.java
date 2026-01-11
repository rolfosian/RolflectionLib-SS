package rolflectionlib.ui;

import java.awt.Color;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

public class ProgressBar {
    private final UIPanelAPI bar;

    public UIPanelAPI getBar() {
        return this.bar;
    }

    public ProgressBar(String text, float rangeMin, float rangeMax) {
        this.bar = UiUtil.instantiator.instantiateProgressBar(text, rangeMin, rangeMax);
    }

    public void setMin(float min) {
        UiUtil.utils.progressBarSetMin(bar, min);
    }

    public float getMax() {
        return UiUtil.utils.progressBarGetMax(bar);
    }

    public void setMax(float max) {
        UiUtil.utils.progressBarSetMax(bar, max);
    }

    public LabelAPI getValue() {
        return UiUtil.utils.progressBarGetValue(bar);
    }

    public void setText(String text) {
        UiUtil.utils.progressBarSetText(bar, text);
    }

    public void setBarColor(Color color) {
        UiUtil.utils.progressBarSetBarColor(bar, color);
    }

    public void setRoundBarValue(boolean roundBarValue) {
        UiUtil.utils.progressBarSetRoundBarValue(bar, roundBarValue);
    }

    public void setProgress(float progress) {
        UiUtil.utils.progressBarSetProgress(bar, progress);
    }

    public void setShowLabelOnly(boolean showLabelOnly) {
        UiUtil.utils.progressBarSetShowLabelOnly(bar, showLabelOnly);
    }

    public void setUserAdjustable(boolean userAdjustable) {
        UiUtil.utils.progressBarSetUserAdjustable(bar, userAdjustable);
    }

    public void setShowAdjustableIndicator(boolean showAdjustableIndicator) {
        UiUtil.utils.progressBarSetShowAdjustableIndicator(bar, showAdjustableIndicator);
    }

    public float getProgress() {
        return UiUtil.utils.progressBarGetProgress(bar);
    }

    public void forceSync() {
        UiUtil.utils.progressBarForceSync(bar);
    }

    public void sizeChanged(float width, float height) {
        UiUtil.utils.progressBarSizeChanged(bar, width, height);
    }

    public void setHighlightOnMouseover(boolean highlightOnMouseover) {
        UiUtil.utils.progressBarSetHighlightOnMouseover(bar, highlightOnMouseover);
    }

    public void setBonusColor(Color color) {
        UiUtil.utils.progressBarSetBonusColor(bar, color);
    }

    public float getRangeMin() {
        return UiUtil.utils.progressBarGetRangeMin(bar);
    }

    public Color getBonusColor() {
        return UiUtil.utils.progressBarGetBonusColor(bar);
    }

    public void setScrollSpeed(float scrollSpeed) {
        UiUtil.utils.progressBarSetScrollSpeed(bar, scrollSpeed);
    }

    public void setClampCurrToMax(boolean clampCurrToMax) {
        UiUtil.utils.progressBarSetClampCurrToMax(bar, clampCurrToMax);
    }

    public float getRangeMax() {
        return UiUtil.utils.progressBarGetRangeMax(bar);
    }

    public int getNumSubivisions() {
        return UiUtil.utils.progressBarGetNumSubivisions(bar);
    }

    public boolean isShowNoText() {
        return UiUtil.utils.progressBarIsShowNoText(bar);
    }

    public void setShowValueOnly(boolean showValueOnly) {
        UiUtil.utils.progressBarSetShowValueOnly(bar, showValueOnly);
    }

    public boolean isShowLabelOnly() {
        return UiUtil.utils.progressBarIsShowLabelOnly(bar);
    }

    public float getBonusAmount() {
        return UiUtil.utils.progressBarGetBonusAmount(bar);
    }

    public void setBonusAmount(float bonusAmount) {
        UiUtil.utils.progressBarSetBonusAmount(bar, bonusAmount);
    }

    public void setNumSubivisions(int numSubivisions) {
        UiUtil.utils.progressBarSetNumSubivisions(bar, numSubivisions);
    }

    public void setShowNoText(boolean showNoText) {
        UiUtil.utils.progressBarSetShowNoText(bar, showNoText);
    }

    public Color getWidgetColor() {
        return UiUtil.utils.progressBarGetWidgetColor(bar);
    }

    public void setWidgetColor(Color color) {
        UiUtil.utils.progressBarSetWidgetColor(bar, color);
    }

    public Fader getHighlight() {
        return UiUtil.utils.progressBarGetHighlight(bar);
    }

    public void setTextColor(Color color) {
        UiUtil.utils.progressBarSetTextColor(bar, color);
    }

    public void setRangeMax(float rangeMax) {
        UiUtil.utils.progressBarSetRangeMax(bar, rangeMax);
    }

    public void setRangeMin(float rangeMin) {
        UiUtil.utils.progressBarSetRangeMin(bar, rangeMin);
    }

    public void setShowDecimalForValueOnlyMode(boolean showDecimalForValueOnlyMod) {
        UiUtil.utils.progressBarSetShowDecimalForValueOnlyMode(bar, showDecimalForValueOnlyMod);
    }

    public void setFlashOnOverflowFraction(float flashOnOverflowFraction) {
        UiUtil.utils.progressBarSetFlashOnOverflowFraction(bar, flashOnOverflowFraction);
    }

    public void setPotentialDecreaseAmount(float potentialDecreaseAmount) {
        UiUtil.utils.progressBarSetPotentialDecreaseAmount(bar, potentialDecreaseAmount);
    }

    public void setBarColorOverflow(Color color) {
        UiUtil.utils.progressBarSetBarColorOverflow(bar, color);
    }

    public Fader getBarHighlightFader() {
        return UiUtil.utils.progressBarGetBarHighlightFader(bar);
    }

    public int getRoundingIncrement() {
        return UiUtil.utils.progressBarGetRoundingIncrement(bar);
    }

    public void setRoundingIncrement(int roundingIncrement) {
        UiUtil.utils.progressBarSetRoundingIncrement(bar, roundingIncrement);
    }

    public void setLineUpTextOnCenter(boolean lineUpTextOnCenter, float offset) {
        UiUtil.utils.progressBarSetLineUpTextOnCenter(bar, lineUpTextOnCenter, offset);
    }

    public void setHighlightBrightnessOverride(float highlightBrightnessOverride) {
        UiUtil.utils.progressBarSetHighlightBrightnessOverride(bar, highlightBrightnessOverride);
    }

    public void setShowNotchOnIfBelowProgress(float showNotchOnIfBelowProgress) {
        UiUtil.utils.progressBarSetShowNotchOnIfBelowProgress(bar, showNotchOnIfBelowProgress);
    }

    public float getShowNotchOnIfBelowProgress() {
        return UiUtil.utils.progressBarGetShowNotchOnIfBelowProgress(bar);
    }

    public float getXCoordinateForProgressValue(float value) {
        return UiUtil.utils.progressBarGetXCoordinateForProgressValue(bar, value);
    }

    public void setShowPercentAndTitle(boolean showPercentAndTitle) {
        UiUtil.utils.progressBarSetShowPercentAndTitle(bar, showPercentAndTitle);
    }

    public boolean isShowAdjustableIndicator() {
        return UiUtil.utils.progressBarIsShowAdjustableIndicator(bar);
    }

    public Color getBarColor() {
        return UiUtil.utils.progressBarGetBarColor(bar);
    }

    public void setTextValueColor(Color color) {
        UiUtil.utils.progressBarSetTextValueColor(bar, color);
    }

    public void setShowPercent(boolean showPercent) {
        UiUtil.utils.progressBarSetShowPercent(bar, showPercent);
    }
}