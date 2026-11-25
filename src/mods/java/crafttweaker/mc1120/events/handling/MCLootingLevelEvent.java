package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.LootingLevelEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCLootingLevelEvent implements LootingLevelEvent {
   private net.minecraftforge.event.entity.living.LootingLevelEvent event;

   public MCLootingLevelEvent(net.minecraftforge.event.entity.living.LootingLevelEvent event) {
      this.event = event;
   }

   public int getLootingLevel() {
      return this.event.getLootingLevel();
   }

   public void setLootingLevel(int level) {
      this.event.setLootingLevel(level);
   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getDamageSource());
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
