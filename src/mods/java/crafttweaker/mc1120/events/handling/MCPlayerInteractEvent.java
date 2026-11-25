package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.PlayerInteractEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;

public class MCPlayerInteractEvent implements PlayerInteractEvent {
   private final net.minecraftforge.event.entity.player.PlayerInteractEvent event;

   public MCPlayerInteractEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent event) {
      this.event = event;
   }

   public void damageItem(int amount) {
      this.event.getItemStack().damageItem(amount, this.event.getEntityPlayer());
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public IBlock getBlock() {
      return this.getWorld().getBlock(this.getPosition());
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getWorld().getBlockState(this.event.getPos()));
   }

   public int getDimension() {
      return this.getWorld().getDimension();
   }

   public String getHand() {
      return String.valueOf(this.event.getHand());
   }

   public IItemStack getUsedItem() {
      return CraftTweakerMC.getIItemStack(this.event.getItemStack());
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public IFacing getFace() {
      return CraftTweakerMC.getIFacing(this.event.getFace());
   }
}
