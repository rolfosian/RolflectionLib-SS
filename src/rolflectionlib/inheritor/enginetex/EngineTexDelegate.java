package rolflectionlib.inheritor.enginetex;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.combat.EngineSlotAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;

public abstract class EngineTexDelegate {
    protected List<ShipEngineAPI> engines = new ArrayList<>();
    protected List<EngineSlotAPI> engineSlots;

    protected EngineTexInterface texWrapper = null;
    protected int[] texIds = null;

    public abstract void onTexBind(int currentEngine);

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
            engines.add(engine);
            engineSlots.add(engine.getEngineSlot());
        }
    }

    protected void setTexWrapper(EngineTexInterface texWrapper) {
        this.texWrapper = texWrapper;
    }

    public void setTexIds(int[] texIds) {
        this.texIds = texIds;
    }
}
