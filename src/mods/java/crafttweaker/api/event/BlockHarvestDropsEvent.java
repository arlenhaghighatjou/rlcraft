package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.player.IPlayer;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.BlockHarvestDropsEvent")
@ZenRegister
public interface BlockHarvestDropsEvent extends IBlockEvent {
   @ZenGetter("dropChance")
   float getDropChance();

   @ZenSetter("dropChance")
   void setDropChance(float var1);

   @ZenGetter("fortuneLevel")
   int getFortuneLevel();

   @ZenGetter("drops")
   List<WeightedItemStack> getDrops();

   @ZenSetter("drops")
   void setDrops(List<WeightedItemStack> var1);

   @ZenMethod
   void addItem(WeightedItemStack var1);

   @ZenGetter("silkTouch")
   boolean isSilkTouch();

   @ZenGetter("isPlayer")
   boolean isPlayer();

   @ZenGetter("player")
   IPlayer getPlayer();
}
