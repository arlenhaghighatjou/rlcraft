package crafttweaker.api.world;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.util.Position3f;
import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.world.IWorld")
@ZenRegister
public interface IWorld extends IBlockAccess {
   @ZenMethod
   int getBrightness(IBlockPos var1);

   @ZenMethod
   int getBrightness(int var1, int var2, int var3);

   @ZenMethod
   IBiome getBiome(Position3f var1);

   @ZenMethod
   IBiome getBiome(IBlockPos var1);

   @ZenGetter("worldInfo")
   @ZenMethod
   IWorldInfo getWorldInfo();

   @ZenGetter("remote")
   @ZenMethod
   boolean isRemote();

   @ZenGetter("raining")
   @ZenMethod
   boolean isRaining();

   @ZenGetter("dayTime")
   @ZenMethod
   boolean isDayTime();

   @ZenGetter("time")
   @ZenMethod
   long getWorldTime();

   @ZenGetter("surfaceWorld")
   @ZenMethod
   boolean isSurfaceWorld();

   @ZenGetter("moonPhase")
   @ZenMethod
   int getMoonPhase();

   @ZenGetter("dimension")
   @ZenMethod
   int getDimension();

   @ZenGetter("dimensionType")
   @ZenMethod
   String getDimensionType();

   @ZenMethod
   IBlock getBlock(int var1, int var2, int var3);

   @ZenMethod
   IBlock getBlock(IBlockPos var1);

   @ZenGetter("worldType")
   @ZenMethod
   String getWorldType();

   @ZenMethod
   boolean setBlockState(IBlockState var1, IBlockPos var2);

   @ZenMethod
   boolean setBlockState(IBlockState var1, IData var2, IBlockPos var3);

   @ZenGetter("provider")
   @ZenMethod
   IWorldProvider getProvider();

   Object getInternal();

   @ZenMethod
   boolean spawnEntity(IEntity var1);

   @ZenMethod
   default void removeEntity(IEntity entity) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.removeEntity, tell the author to fix that.");
   }

   @ZenMethod
   default IRayTraceResult rayTraceBlocks(IVector3d begin, IVector3d ray, @Optional boolean stopOnLiquid, @Optional boolean ignoreBlockWithoutBoundingBox, @Optional(valueBoolean = true) boolean returnLastUncollidableBlock) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.getRayTrace, tell the author to fix that.");
      return null;
   }

   @ZenMethod
   default List<IEntity> getEntitiesInArea(Position3f start, @Optional Position3f end) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.getEntitiesInArea, tell the author to fix that.");
      return new ArrayList();
   }

   @ZenMethod
   default IItemStack getPickedBlock(IBlockPos pos, IRayTraceResult rayTraceResult, IPlayer player) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.getPickedBlock, tell the author to fix that.");
      return null;
   }

   @ZenMethod
   default IExplosion createExplosion(IEntity exploder, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.createExplosion, tell the author to fix that.");
      return null;
   }

   @ZenMethod
   default IExplosion performExplosion(IEntity exploder, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.performExplosion, tell the author to fix that.");
      return null;
   }

   @ZenMethod
   default IExplosion performExplosion(IExplosion explosion) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not override IWorld.performExplosion, tell the author to fix that.");
      return null;
   }
}
