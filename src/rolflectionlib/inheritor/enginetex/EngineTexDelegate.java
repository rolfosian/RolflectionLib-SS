package rolflectionlib.inheritor.enginetex;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.combat.EngineSlotAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;

public abstract class EngineTexDelegate {
    protected List<ShipEngineAPI> engines = new ArrayList<>();
    protected List<EngineSlotAPI> engineSlots = new ArrayList<>();

    protected EngineTexInterface texWrapper = null;
    protected int[][] engineTexOrder;

    public abstract void onTexBind(int currentEngine, boolean isRolloverEngine);

    public List<ShipEngineAPI> getEngines() {
        return this.engines;
    }

    public List<EngineSlotAPI> getEngineSlots() {
        return this.engineSlots;
    }

    public EngineTexInterface getTexWrapper() {
        return this.texWrapper;
    }

    protected void setEngines(List<Object> engines) {
        for (Object eng : engines) {
            ShipEngineAPI engine = (ShipEngineAPI) eng;
            this.engines.add(engine);
            this.engineSlots.add(engine.getEngineSlot());
        }
    }

    protected void setTexWrapper(EngineTexInterface texWrapper) {
        this.texWrapper = texWrapper;
    }

    public int[] getTexIds(int idx) {
        if (this.engineTexOrder == null) return null;
        return this.engineTexOrder[idx];
    }
}
