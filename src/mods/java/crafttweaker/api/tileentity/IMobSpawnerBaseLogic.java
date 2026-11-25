package crafttweaker.api.tileentity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.tileentity.IMobSpawnerBaseLogic")
@ZenRegister
public interface IMobSpawnerBaseLogic {
   @ZenGetter("entityDefinition")
   default IEntityDefinition getEntityDefinition() {
      return null;
   }

   @ZenSetter("entityDefinition")
   void setEntityDefinition(IEntityDefinition var1);

   @ZenMethod
   void updateSpawner();

   @ZenGetter("nbtData")
   IData getNbtData();

   @ZenSetter("nbtData")
   void setNbtData(IData var1);

   @ZenMethod
   void setDelayToMin();

   @ZenGetter("world")
   IWorld getWorld();

   @ZenGetter("blockPos")
   IBlockPos getBlockPos();
}
