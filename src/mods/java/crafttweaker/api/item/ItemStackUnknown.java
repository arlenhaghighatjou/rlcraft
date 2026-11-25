package crafttweaker.api.item;

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

public final class ItemStackUnknown implements IItemStack {
   public static final ItemStackUnknown INSTANCE = new ItemStackUnknown();

   private ItemStackUnknown() {
   }

   public IItemDefinition getDefinition() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isItemBlock() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public String getName() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public String getDisplayName() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void setDisplayName(String name) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getMaxStackSize() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void setMaxStackSize(int size) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public float getBlockHardness() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void setBlockHardness(float hardness) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getDamage() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getMaxDamage() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void setMaxDamage(int damage) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IData getTag() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public ILiquidStack getLiquid() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public String getMark() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getAmount() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public List<IItemStack> getItems() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack[] getItemArray() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public List<ILiquidStack> getLiquids() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack amount(int amount) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient or(IIngredient ingredient) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient only(IItemCondition condition) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient marked(String mark) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean matches(IItemStack item) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean matchesExact(IItemStack item) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean matches(ILiquidStack liquid) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean contains(IIngredient ingredient) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack applyNewTransform(IItemStack item) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasNewTransformers() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasTransformers() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient transform(IItemTransformer transformer) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public Object getInternal() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public String toCommandString() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public WeightedItemStack percent(float p) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public WeightedItemStack weight(float p) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IIngredient anyDamage() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withDamage(int damage) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withAmount(int amount) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack anyAmount() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withTag(IData tag) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withEmptyTag() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack removeTag(String tag) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack updateTag(IData tagUpdate) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IBlock asBlock() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public List<IOreDictEntry> getOres() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withDisplayName(String name) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack withLore(String[] lore) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public List<String> getToolClasses() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getItemEnchantability() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack getContainerItem() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isBeaconPayment() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean canPlaceOn(IBlockDefinition block) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean canDestroy(IBlockDefinition block) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean canHarvestBlock(IBlockState block) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getRepairCost() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void setRepairCost(int repairCost) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean canEditBlocks() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isOnItemFrame() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isItemEnchanted() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isItemDamaged() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isDamageable() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isStackable() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void addEnchantment(IEnchantment enchantment) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean canApplyAtEnchantingTable(IEnchantmentDefinition enchantment) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public List<IEnchantment> getEnchantments() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isItemEnchantable() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasEffect() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasDisplayName() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void clearCustomName() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasTag() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public void damageItem(int amount, IEntity entity) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getMetadata() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean getHasSubtypes() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public float getStrengthAgainstBlock(IBlockState blockState) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IItemStack splitStack(int amount) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean isEmpty() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public int getItemBurnTime() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean showsDurabilityBar() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasCustomEntity() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public boolean hasContainerItem() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IEntityItem createEntityItem(IWorld world, int x, int y, int z) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IEntityItem createEntityItem(IWorld world, IBlockPos pos) {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }

   public IMutableItemStack mutable() {
      throw new IllegalStateException("ItemStackUnknown is only supposed to be used internally!");
   }
}
