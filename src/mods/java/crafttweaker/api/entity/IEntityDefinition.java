package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.util.IntegerRange;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.entity.IEntityDefinition")
@ZenRegister
public interface IEntityDefinition {
   @ZenGetter("id")
   String getId();

   @ZenGetter("name")
   String getName();

   @ZenMethod
   void addDrop(IItemStack var1, @Optional int var2, @Optional int var3, @Optional float var4);

   @ZenMethod
   void addDrop(WeightedItemStack var1, @Optional int var2, @Optional int var3);

   @ZenMethod
   void addDropFunction(IEntityDropFunction var1);

   @ZenMethod
   void addPlayerOnlyDrop(IItemStack var1, @Optional int var2, @Optional int var3, @Optional float var4);

   @ZenMethod
   void addPlayerOnlyDrop(WeightedItemStack var1, @Optional int var2, @Optional int var3);

   @ZenMethod
   void removeDrop(IItemStack var1);

   @ZenMethod
   void clearDrops();

   @ZenGetter("drops")
   List<IEntityDrop> getDrops();

   @ZenMethod
   IEntity createEntity(IWorld var1);

   @ZenMethod
   IEntity spawnEntity(IWorld var1, IBlockPos var2);

   /** @deprecated */
   @Deprecated
   Map<IItemStack, IntegerRange> getDropsToAdd();

   /** @deprecated */
   @Deprecated
   Map<IItemStack, IntegerRange> getDropsToAddPlayerOnly();

   List<IItemStack> getDropsToRemove();

   boolean shouldClearDrops();

   Object getInternal();

   List<IEntityDropFunction> getDropFunctions();
}
