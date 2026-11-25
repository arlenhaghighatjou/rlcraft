package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerAnvilUpdateEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.AnvilUpdateEvent;

public class MCPlayerAnvilUpdateEvent implements PlayerAnvilUpdateEvent {
   private final AnvilUpdateEvent event;

   public MCPlayerAnvilUpdateEvent(AnvilUpdateEvent event) {
      this.event = event;
   }

   public IItemStack getLeftItem() {
      return CraftTweakerMC.getIItemStack(this.event.getLeft());
   }

   public IItemStack getRightItem() {
      return CraftTweakerMC.getIItemStack(this.event.getRight());
   }

   public IItemStack getOutputItem() {
      return CraftTweakerMC.getIItemStack(this.event.getOutput());
   }

   public void setOutputItem(IItemStack output) {
      this.event.setOutput(CraftTweakerMC.getItemStack(output));
   }

   public String getItemName() {
      return this.event.getName();
   }

   public int getXPCost() {
      return this.event.getCost();
   }

   public void setXPCost(int xp) {
      this.event.setCost(xp);
   }

   public int getMaterialCost() {
      return this.event.getMaterialCost();
   }

   public void setMaterialCost(int materialCost) {
      this.event.setMaterialCost(materialCost);
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
