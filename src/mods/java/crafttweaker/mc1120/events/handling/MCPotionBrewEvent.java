package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.IPotionBrewEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.brewing.PotionBrewEvent;

public class MCPotionBrewEvent implements IPotionBrewEvent {
   private final PotionBrewEvent event;

   public MCPotionBrewEvent(PotionBrewEvent event) {
      this.event = event;
   }

   public int getLength() {
      return this.event.getLength();
   }

   public IItemStack getItem(int index) {
      return CraftTweakerMC.getIItemStack(this.event.getItem(index));
   }

   public void setItem(int index, IItemStack stack) {
      this.event.setItem(index, CraftTweakerMC.getItemStack(stack));
   }
}
