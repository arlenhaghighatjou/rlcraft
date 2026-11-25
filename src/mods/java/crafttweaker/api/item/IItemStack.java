package crafttweaker.api.item;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.item.IItemStack")
@ZenRegister
public interface IItemStack extends IIngredient {
   @ZenGetter("definition")
   IItemDefinition getDefinition();

   @ZenGetter("isItemBlock")
   default boolean isItemBlock() {
      CraftTweakerAPI.logError("Class " + this.getClass().getCanonicalName() + " doesn't override IItemStack::isItemBlock!");
      return false;
   }

   @ZenGetter("name")
   String getName();

   @ZenGetter("displayName")
   String getDisplayName();

   @ZenSetter("displayName")
   void setDisplayName(String var1);

   @ZenGetter("maxStackSize")
   int getMaxStackSize();

   @ZenSetter("maxStackSize")
   void setMaxStackSize(int var1);

   @ZenGetter("hardness")
   float getBlockHardness();

   @ZenSetter("hardness")
   void setBlockHardness(float var1);

   @ZenGetter("damage")
   int getDamage();

   @ZenGetter("maxDamage")
   int getMaxDamage();

   @ZenSetter("maxDamage")
   void setMaxDamage(int var1);

   @ZenGetter("tag")
   IData getTag();

   @ZenGetter("liquid")
   ILiquidStack getLiquid();

   @ZenMethod
   IItemStack amount(int var1);

   @ZenOperator(OperatorType.MOD)
   WeightedItemStack percent(float var1);

   @ZenMethod
   WeightedItemStack weight(float var1);

   @ZenMethod
   IIngredient anyDamage();

   @ZenMethod
   IItemStack withDamage(int var1);

   @ZenOperator(OperatorType.MUL)
   @ZenMethod
   IItemStack withAmount(int var1);

   @ZenMethod
   IItemStack anyAmount();

   @ZenMethod
   default IItemStack withTag(IData tag, @Optional(valueBoolean = true) boolean matchTagExact) {
      CraftTweakerAPI.logError("Default method IItemStack#WithTag is not overwritten in " + this.getClass() + " please report to the author!");
      return this.withTag(tag);
   }

   @ZenGetter("matchTagExact")
   default boolean getMatchTagExact() {
      CraftTweakerAPI.logError("Default method IItemStack#getMatchTagExact is not overwritten in " + this.getClass() + " please report to the author!");
      return true;
   }

   /** @deprecated */
   @Deprecated
   IItemStack withTag(IData var1);

   @ZenMethod
   IItemStack withEmptyTag();

   @ZenMethod
   IItemStack removeTag(String var1);

   @ZenMethod
   default IItemStack updateTag(IData tagUpdate, @Optional(valueBoolean = true) boolean matchTagExact) {
      CraftTweakerAPI.logError("Default method IItemStack#updateTag is not overwritten in " + this.getClass() + " please report to the author!");
      return this.updateTag(tagUpdate);
   }

   /** @deprecated */
   @Deprecated
   IItemStack updateTag(IData var1);

   @ZenCaster
   @ZenMethod
   IBlock asBlock();

   @ZenGetter("ores")
   List<IOreDictEntry> getOres();

   @ZenMethod
   IItemStack withDisplayName(String var1);

   @ZenMethod
   IItemStack withLore(String[] var1);

   @ZenGetter("toolClasses")
   List<String> getToolClasses();

   @ZenGetter("itemEnchantability")
   int getItemEnchantability();

   @ZenGetter("containerItem")
   IItemStack getContainerItem();

   @ZenGetter("isBeaconPayment")
   boolean isBeaconPayment();

   @ZenMethod
   boolean canPlaceOn(IBlockDefinition var1);

   @ZenMethod
   boolean canDestroy(IBlockDefinition var1);

   @ZenMethod
   boolean canHarvestBlock(IBlockState var1);

   @ZenGetter("repairCost")
   int getRepairCost();

   @ZenSetter("repairCost")
   void setRepairCost(int var1);

   @ZenGetter("canEditBlocks")
   boolean canEditBlocks();

   @ZenGetter("isOnItemFrame")
   boolean isOnItemFrame();

   @ZenGetter("isEnchanted")
   boolean isItemEnchanted();

   @ZenGetter("isDamaged")
   boolean isItemDamaged();

   @ZenGetter("isDamageable")
   boolean isDamageable();

   @ZenGetter("isStackable")
   boolean isStackable();

   @ZenMethod
   void addEnchantment(IEnchantment var1);

   @ZenMethod
   boolean canApplyAtEnchantingTable(IEnchantmentDefinition var1);

   @ZenGetter("enchantments")
   List<IEnchantment> getEnchantments();

   @ZenGetter("isEnchantable")
   boolean isItemEnchantable();

   @ZenGetter("hasEffect")
   boolean hasEffect();

   @ZenGetter("hasDisplayName")
   boolean hasDisplayName();

   @ZenMethod
   void clearCustomName();

   @ZenGetter("hasTag")
   boolean hasTag();

   @ZenMethod
   void damageItem(int var1, IEntity var2);

   @ZenGetter("metadata")
   int getMetadata();

   @ZenGetter("hasSubtypes")
   boolean getHasSubtypes();

   @ZenMethod
   float getStrengthAgainstBlock(IBlockState var1);

   @ZenMethod
   IItemStack splitStack(int var1);

   @ZenGetter("isEmpty")
   boolean isEmpty();

   @ZenGetter("burnTime")
   int getItemBurnTime();

   @ZenGetter("showsDurabilityBar")
   boolean showsDurabilityBar();

   @ZenGetter("hasCustomEntity")
   boolean hasCustomEntity();

   @ZenGetter("hasContainerItem")
   boolean hasContainerItem();

   @ZenMethod
   IEntityItem createEntityItem(IWorld var1, int var2, int var3, int var4);

   @ZenMethod
   default IEntityItem createEntityItem(IWorld world, float x, float y, float z) {
      return this.createEntityItem(world, (int)x, (int)y, (int)z);
   }

   @ZenMethod
   IEntityItem createEntityItem(IWorld var1, IBlockPos var2);

   @ZenMethod
   default IMutableItemStack mutable() {
      CraftTweakerAPI.logError("Default method IItemStack#mutable is not overwritten in " + this.getClass() + " please report to the author!");
      return null;
   }

   @ZenGetter("isFood")
   default boolean isFood() {
      return false;
   }

   @ZenGetter("saturation")
   default float getSaturation() {
      return 0.0F;
   }

   @ZenGetter("healAmount")
   default int getHealAmount() {
      return 0;
   }

   @ZenMethod
   default int getHarvestLevel(String toolClass) {
      return -1;
   }

   @ZenMethod
   default int getHarvestLevel(String toolClass, IPlayer player, IBlockState blockState) {
      return -1;
   }
}
