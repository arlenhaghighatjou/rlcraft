package crafttweaker.api.event;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.event.PlayerInteractEvent")
@ZenRegister
public interface PlayerInteractEvent extends IPlayerEvent, IEventPositionable {
   @ZenMethod
   void damageItem(int var1);

   @ZenGetter("world")
   IWorld getWorld();

   @ZenGetter("block")
   IBlock getBlock();

   @ZenGetter("blockState")
   IBlockState getBlockState();

   @ZenGetter("dimension")
   int getDimension();

   @ZenGetter("hand")
   String getHand();

   @ZenGetter("item")
   IItemStack getUsedItem();

   @ZenGetter("face")
   default IFacing getFace() {
      CraftTweakerAPI.logError("Default method PlayerInteractEvent#getFace is not overwritten in " + this.getClass() + " please report to the author!");
      return null;
   }
}
