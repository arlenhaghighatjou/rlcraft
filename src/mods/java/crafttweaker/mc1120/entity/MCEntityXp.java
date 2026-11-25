package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityXp;
import net.minecraft.entity.item.EntityXPOrb;

public class MCEntityXp extends MCEntity implements IEntityXp {
   private final EntityXPOrb entityXPOrb;

   public MCEntityXp(EntityXPOrb entityXPOrb) {
      super(entityXPOrb);
      this.entityXPOrb = entityXPOrb;
   }

   public float getXp() {
      return (float)this.entityXPOrb.getXpValue();
   }

   public void setXp(int xp) {
      this.entityXPOrb.xpValue = xp;
   }
}
