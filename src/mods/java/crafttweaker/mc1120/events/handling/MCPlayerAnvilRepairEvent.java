package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerAnvilRepairEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;

public class MCPlayerAnvilRepairEvent implements PlayerAnvilRepairEvent {
   private final AnvilRepairEvent event;

   public MCPlayerAnvilRepairEvent(AnvilRepairEvent event) {
      this.event = event;
   }

   public IItemStack getItemResult() {
      return CraftTweakerMC.getIItemStack(this.event.getItemResult());
   }

   public IItemStack getItemIngredient() {
      return CraftTweakerMC.getIItemStack(this.event.getIngredientInput());
   }

   public IItemStack getItemInput() {
      return CraftTweakerMC.getIItemStack(this.event.getItemInput());
   }

   public float getBreakChance() {
      return this.event.getBreakChance();
   }

   public void setBreakChance(float breakChance) {
      this.event.setBreakChance(breakChance);
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
