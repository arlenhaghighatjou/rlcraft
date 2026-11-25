package crafttweaker.api.block;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.block.IBlockProperties")
@ZenRegister
public interface IBlockProperties {
   @ZenMethod
   int getLightValue(IBlockAccess var1, IBlockPos var2);

   @ZenGetter("canProvidePower")
   boolean canProvidePower();

   @ZenMethod
   int getWeakPower(IBlockAccess var1, IBlockPos var2, IFacing var3);

   @ZenMethod
   int getComparatorInputOverride(IWorld var1, IBlockPos var2);

   @ZenGetter("mobilityFlag")
   IMobilityFlag getMobilityFlag();

   @ZenGetter("material")
   IMaterial getMaterial();

   @ZenMethod
   boolean canEntitySpawn(IEntity var1);

   @ZenGetter("causesSuffocation")
   boolean causesSuffocation();

   @ZenMethod
   boolean doesSideBlockRendering(IBlockAccess var1, IBlockPos var2, IFacing var3);

   @ZenMethod
   IBlockState getActualState(IBlockAccess var1, IBlockPos var2);

   @ZenMethod
   float getBlockHardness(IWorld var1, IBlockPos var2);

   @ZenMethod
   int getLightOpacity(IBlockAccess var1, IBlockPos var2);

   @ZenMethod
   float getPlayerRelativeBlockHardness(IPlayer var1, IWorld var2, IBlockPos var3);

   @ZenMethod
   int getStrongPower(IBlockAccess var1, IBlockPos var2, IFacing var3);

   @ZenGetter("hasCustomBreakingProgress")
   boolean hasCustomBreakingProgress();

   @ZenGetter("blockNormalCube")
   boolean isBlockNormalCube();

   @ZenGetter("fullBlock")
   boolean isFullBlock();

   @ZenGetter("fullCube")
   boolean isFullCube();

   @ZenGetter("normalCube")
   boolean isNormalCube();

   @ZenGetter("opaqueCube")
   boolean isOpaqueCube();

   @ZenMethod
   boolean isSideSolid(IBlockAccess var1, IBlockPos var2, IFacing var3);

   @ZenGetter("useNeighborBrightness")
   boolean useNeighborBrightness();

   Object getInternal();
}
