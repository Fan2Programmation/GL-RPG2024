package engine.entities.items.consumables;

import config.GameConfiguration;
import engine.dungeon.Position;

public class Health extends Consumable {

    public Health(Position position) {
        super(GameConfiguration.HEALTH_POINTS, "Health", "health", position);
    }
    
}
