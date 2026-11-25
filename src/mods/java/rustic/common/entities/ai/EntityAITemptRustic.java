package rustic.common.entities.ai;

import java.util.Set;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.item.Item;

public class EntityAITemptRustic extends EntityAITempt {
   public EntityAITemptRustic(EntityCreature entity, double speed, Item item, boolean scaredByMovement) {
      super(entity, speed, item, scaredByMovement);
   }

   public EntityAITemptRustic(EntityCreature entity, double speed, Set<Item> itemSet, boolean scaredByMovement) {
      super(entity, speed, scaredByMovement, itemSet);
   }
}
