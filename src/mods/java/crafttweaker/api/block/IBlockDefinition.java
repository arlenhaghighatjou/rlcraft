package crafttweaker.api.block;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IExplosion;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.block.IBlockDefinition")
@ZenRegister
public interface IBlockDefinition {
   @ZenGetter("id")
   String getId();

   @ZenGetter("displayName")
   String getDisplayName();

   Object getInternal();

   @ZenSetter("lightOpacity")
   void setLightOpacity(int var1);

   @ZenGetter("lightOpacity")
   @ZenMethod
   default float getLightOpacity() {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightOpacity()");
      return 0.0F;
   }

   @ZenMethod
   default float getLightOpacity(IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightOpacity(state)");
      return 0.0F;
   }

   @ZenMethod
   default float getLightOpacity(IBlockState state, IWorld world, IBlockPos pos) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightOpacity(state, world, pos)");
      return 0.0F;
   }

   @ZenSetter("lightLevel")
   void setLightLevel(float var1);

   @ZenGetter("lightLevel")
   @ZenMethod
   default float getLightLevel() {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightLevel()");
      return 0.0F;
   }

   @ZenMethod
   default float getLightLevel(IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightLevel(state)");
      return 0.0F;
   }

   @ZenMethod
   default float getLightLevel(IBlockState state, IWorld world, IBlockPos pos) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getLightLevel(state, world, pos)");
      return 0.0F;
   }

   @ZenSetter("resistance")
   void setResistance(float var1);

   @ZenGetter("resistance")
   @ZenMethod
   default float getResistance() {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getResistance()");
      return 0.0F;
   }

   @ZenMethod
   default float getResistance(IWorld world, IBlockPos pos, IEntity entity, IExplosion explosion) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getResistance(world, pos, entity, explosion)");
      return 0.0F;
   }

   @ZenSetter("hardness")
   void setHardness(float var1);

   @ZenGetter("hardness")
   float getHardness();

   @ZenMethod
   void setUnbreakable();

   @ZenGetter("tickRandomly")
   boolean getTickRandomly();

   @ZenSetter("tickRandomly")
   void setTickRandomly(boolean var1);

   /** @deprecated */
   @Deprecated
   void setHarvestLevel(String var1, int var2);

   @ZenMethod
   default void setHarvestLevel(String toolClass, int level, @Optional IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::setHarvestLevel!");
   }

   @ZenGetter("harvestLevel")
   @ZenMethod
   int getHarvestLevel();

   @ZenMethod
   default int getHarvestLevel(IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + "doesn't override IBlockDefinition::setHarvestLevel! It is a bug!");
      return 0;
   }

   @ZenGetter("harvestTool")
   @ZenMethod
   String getHarvestTool();

   @ZenMethod
   default String getHarvestTool(IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + "doesn't override IBlockDefinition::setHarvestTool! It is a bug!");
      return "";
   }

   @ZenMethod
   int tickRate(IWorld var1);

   @ZenMethod
   boolean canPlaceBlockOnSide(IWorld var1, IBlockPos var2, IFacing var3);

   @ZenMethod
   boolean canPlaceBlockAt(IWorld var1, IBlockPos var2);

   @ZenGetter
   boolean canSpawnInBlock();

   @ZenGetter("unlocalizedName")
   String getUnlocalizedName();

   @ZenGetter("creativeTab")
   ICreativeTab getCreativeTabToDisplayOn();

   @ZenSetter("creativeTab")
   void setCreativeTab(ICreativeTab var1);

   @ZenGetter("defaultState")
   IBlockState getDefaultState();

   @ZenMethod
   float getSlipperiness(IBlockState var1, IBlockAccess var2, IBlockPos var3, @Optional IEntity var4);

   @ZenSetter("defaultSlipperiness")
   void setDefaultSlipperiness(float var1);

   @ZenMethod
   default IBlockState getStateFromMeta(int meta) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IBlockDefinition::getStateFromMeta!");
      return this.getDefaultState();
   }

   @ZenMethod
   default boolean isToolEffective(String type, IBlockState state) {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + "doesn't override IBlockDefinition::isToolEffective! It is a bug!");
      return false;
   }
}
