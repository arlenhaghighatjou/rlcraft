package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.IPotionEffectEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import net.minecraftforge.event.entity.living.PotionEvent;

public class MCPotionEffectEvent implements IPotionEffectEvent {
   private final PotionEvent event;

   public MCPotionEffectEvent(PotionEvent event) {
      this.event = event;
   }

   public IPotionEffect getPotionEffect() {
      return CraftTweakerMC.getIPotionEffect(this.event.getPotionEffect());
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
