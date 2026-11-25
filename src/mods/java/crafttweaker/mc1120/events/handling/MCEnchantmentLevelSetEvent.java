package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.EnchantmentLevelSetEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;

public class MCEnchantmentLevelSetEvent implements EnchantmentLevelSetEvent {
   private net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent event;

   public MCEnchantmentLevelSetEvent(net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent event) {
      this.event = event;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public int getEnchantRow() {
      return this.event.getEnchantRow();
   }

   public int getPower() {
      return this.event.getPower();
   }

   public IItemStack getItem() {
      return CraftTweakerMC.getIItemStack(this.event.getItem());
   }

   public int getOriginalLevel() {
      return this.event.getOriginalLevel();
   }

   public int getLevel() {
      return this.event.getLevel();
   }

   public void setLevel(int level) {
      this.event.setLevel(Math.max(0, Math.min(30, level)));
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }
}
