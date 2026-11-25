package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GhostItemStack implements IItemStack {
   public static final ItemStack FAKE_STACK = new ItemStack(new Item(), 1);
   public static final IItemStack FAKE_ISTACK = new MCItemStack(FAKE_STACK);
   private static final int ANY_AMOUNT = -300;
   private static final int UNCHANGED = -600;
   private static boolean warned = false;
   private final String name;
   private final int meta;
   private int amount = -600;
   private IData tag = null;
   private String mark;
   private IItemStack item;
   private int damage;

   public GhostItemStack(String name, int meta) {
      this.item = FAKE_ISTACK;
      this.damage = -600;
      this.name = name;
      this.meta = meta;
      if (!warned) {
         CraftTweakerAPI.logInfo("Item <item:" + name + ":" + meta + "> has not been found, trying to use a ghost representative. This message will only be printed once, all subsequent missing items will be handled the same way.");
         warned = true;
      }

      this.update();
   }

   public boolean update() {
      if (FAKE_ISTACK != this.item) {
         return true;
      } else {
         IItemStack stack = BracketHandlerItem.getItem(this.name, this.meta);
         if (stack == null) {
            return false;
         } else {
            if (this.amount == -300) {
               stack = stack.anyAmount();
            } else if (this.amount != -600) {
               stack = stack.withAmount(this.amount);
            }

            if (this.damage != -600) {
               stack = stack.withDamage(this.damage);
            }

            if (this.tag != null) {
               stack = stack.withTag(this.tag);
            }

            this.item = stack;
            return true;
         }
      }
   }

   public IItemDefinition getDefinition() {
      if (this.update()) {
         return this.item.getDefinition();
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public String getName() {
      return this.update() ? this.item.getName() : this.name;
   }

   public String getDisplayName() {
      return this.update() ? this.item.getName() : this.name;
   }

   public void setDisplayName(String name) {
      if (this.update()) {
         this.item.setDisplayName(name);
      }

   }

   public int getMaxStackSize() {
      return this.update() ? this.item.getMaxStackSize() : 0;
   }

   public void setMaxStackSize(int size) {
      if (this.update()) {
         this.item.setMaxStackSize(size);
      }

   }

   public float getBlockHardness() {
      return this.update() ? this.item.getBlockHardness() : 0.0F;
   }

   public void setBlockHardness(float hardness) {
      if (this.update()) {
         this.item.setBlockHardness(hardness);
      }

   }

   public int getDamage() {
      return this.update() ? this.item.getDamage() : this.damage;
   }

   public int getMaxDamage() {
      return this.update() ? this.item.getMaxDamage() : 0;
   }

   public void setMaxDamage(int damage) {
      if (this.update()) {
         this.item.setMaxDamage(damage);
      }

   }

   public IData getTag() {
      return this.update() ? this.item.getTag() : this.tag;
   }

   public ILiquidStack getLiquid() {
      return this.update() ? this.item.getLiquid() : null;
   }

   public String getMark() {
      return this.update() ? this.item.getMark() : this.mark;
   }

   public int getAmount() {
      return this.update() ? this.item.getAmount() : this.amount;
   }

   public List<IItemStack> getItems() {
      return this.update() ? this.item.getItems() : Collections.emptyList();
   }

   public IItemStack[] getItemArray() {
      return this.update() ? this.item.getItemArray() : new IItemStack[0];
   }

   public List<ILiquidStack> getLiquids() {
      return this.update() ? this.item.getLiquids() : Collections.EMPTY_LIST;
   }

   public IItemStack amount(int amount) {
      if (this.update()) {
         return this.item.amount(amount);
      } else {
         this.amount = amount;
         return this;
      }
   }

   public IIngredient or(IIngredient ingredient) {
      if (this.update()) {
         return this.item.or(ingredient);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      if (this.update()) {
         return this.item.transformNew(transformer);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IIngredient only(IItemCondition condition) {
      if (this.update()) {
         return this.item.only(condition);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IIngredient marked(String mark) {
      if (this.update()) {
         return this.item.marked(mark);
      } else {
         this.mark = mark;
         return this;
      }
   }

   public WeightedItemStack percent(float p) {
      if (this.update()) {
         return this.item.percent(p);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public WeightedItemStack weight(float p) {
      if (this.update()) {
         return this.item.weight(p);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IIngredient anyDamage() {
      if (this.update()) {
         return this.item.anyDamage();
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IItemStack withDamage(int damage) {
      if (this.update()) {
         return this.item.withDamage(damage);
      } else {
         this.damage = damage;
         return this;
      }
   }

   public IItemStack withAmount(int amount) {
      if (this.update()) {
         return this.item.withAmount(amount);
      } else {
         this.amount = amount;
         return this;
      }
   }

   public IItemStack anyAmount() {
      if (this.update()) {
         return this.item.anyAmount();
      } else {
         this.amount = -300;
         return this;
      }
   }

   public IItemStack withTag(IData tag) {
      return (IItemStack)(this.update() ? this.item.withTag(tag, true) : this);
   }

   public IItemStack withTag(IData tag, boolean matchTagExact) {
      return (IItemStack)(this.update() ? this.item.withTag(tag, matchTagExact) : this);
   }

   public IItemStack withEmptyTag() {
      if (this.update()) {
         return this.item.withEmptyTag();
      } else {
         this.tag = NBTConverter.from(new NBTTagCompound(), true);
         return this;
      }
   }

   public IItemStack removeTag(String tag) {
      if (this.update()) {
         return this.item.removeTag(tag);
      } else {
         this.tag = this.tag.sub(new DataString(tag));
         return this;
      }
   }

   public IItemStack updateTag(IData tagUpdate) {
      if (this.update()) {
         return this.item.updateTag(tagUpdate);
      } else {
         this.tag = this.tag.update(tagUpdate);
         return this;
      }
   }

   public IItemStack updateTag(IData tagUpdate, boolean matchTagExact) {
      if (this.update()) {
         return this.item.updateTag(tagUpdate, matchTagExact);
      } else {
         this.tag = this.tag.update(tagUpdate);
         return this;
      }
   }

   public IBlock asBlock() {
      if (this.update()) {
         return this.item.asBlock();
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public List<IOreDictEntry> getOres() {
      return this.update() ? this.item.getOres() : Collections.EMPTY_LIST;
   }

   public IItemStack withDisplayName(String name) {
      return (IItemStack)(this.update() ? this.item.withDisplayName(name) : this);
   }

   public IItemStack withLore(String[] lore) {
      return (IItemStack)(this.update() ? this.item.withLore(lore) : this);
   }

   public List<String> getToolClasses() {
      return this.update() ? this.item.getToolClasses() : Collections.EMPTY_LIST;
   }

   public int getItemEnchantability() {
      return this.update() ? this.item.getItemEnchantability() : 0;
   }

   public IItemStack getContainerItem() {
      return (IItemStack)(this.update() ? this.item.getContainerItem() : this);
   }

   public boolean isBeaconPayment() {
      return this.update() && this.item.isBeaconPayment();
   }

   public boolean canPlaceOn(IBlockDefinition block) {
      return this.update() && this.item.canPlaceOn(block);
   }

   public boolean canDestroy(IBlockDefinition block) {
      return this.update() && this.item.canDestroy(block);
   }

   public boolean canHarvestBlock(IBlockState block) {
      return this.update() && this.item.canHarvestBlock(block);
   }

   public int getRepairCost() {
      return this.update() ? this.item.getRepairCost() : 0;
   }

   public void setRepairCost(int repairCost) {
      if (this.update()) {
         this.item.setRepairCost(repairCost);
      }

   }

   public boolean canEditBlocks() {
      return this.update() && this.item.canEditBlocks();
   }

   public boolean isOnItemFrame() {
      return this.update() && this.item.isOnItemFrame();
   }

   public boolean isItemEnchanted() {
      return this.update() && this.item.isItemEnchanted();
   }

   public boolean isItemDamaged() {
      return this.update() && this.item.isItemDamaged();
   }

   public boolean isDamageable() {
      return this.update() && this.item.isDamageable();
   }

   public boolean isStackable() {
      return this.update() && this.item.isStackable();
   }

   public void addEnchantment(IEnchantment enchantment) {
      if (this.update()) {
         this.item.addEnchantment(enchantment);
      }

   }

   public boolean canApplyAtEnchantingTable(IEnchantmentDefinition enchantment) {
      return this.update() && this.item.canApplyAtEnchantingTable(enchantment);
   }

   public List<IEnchantment> getEnchantments() {
      return this.update() ? this.item.getEnchantments() : Collections.EMPTY_LIST;
   }

   public boolean isItemEnchantable() {
      return this.update() && this.item.isItemEnchantable();
   }

   public boolean hasEffect() {
      return this.update() && this.item.hasEffect();
   }

   public boolean hasDisplayName() {
      return this.update() && this.item.hasDisplayName();
   }

   public void clearCustomName() {
      if (this.update()) {
         this.item.clearCustomName();
      }

   }

   public boolean hasTag() {
      return this.update() ? this.item.hasTag() : this.tag != null;
   }

   public void damageItem(int amount, IEntity entity) {
      if (this.update()) {
         this.item.damageItem(amount, entity);
      }

   }

   public int getMetadata() {
      return this.update() ? this.item.getMetadata() : this.meta;
   }

   public boolean getHasSubtypes() {
      return this.update() && this.item.getHasSubtypes();
   }

   public float getStrengthAgainstBlock(IBlockState blockState) {
      return this.update() ? this.item.getStrengthAgainstBlock(blockState) : 0.0F;
   }

   public IItemStack splitStack(int amount) {
      return (IItemStack)(this.update() ? this.item.splitStack(amount) : this);
   }

   public boolean isEmpty() {
      return !this.update() || this.item.isEmpty();
   }

   public int getItemBurnTime() {
      return this.update() ? this.item.getItemBurnTime() : 0;
   }

   public boolean showsDurabilityBar() {
      return this.update() && this.item.showsDurabilityBar();
   }

   public boolean hasCustomEntity() {
      return this.update() && this.item.hasCustomEntity();
   }

   public boolean hasContainerItem() {
      return this.update() && this.item.hasContainerItem();
   }

   public IEntityItem createEntityItem(IWorld world, int x, int y, int z) {
      if (this.update()) {
         return this.item.createEntityItem(world, x, y, z);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IEntityItem createEntityItem(IWorld world, IBlockPos pos) {
      if (this.update()) {
         return this.item.createEntityItem(world, pos);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public boolean matches(IItemStack item) {
      return this.update() && this.item.matches(item);
   }

   public boolean matchesExact(IItemStack item) {
      return this.update() && this.item.matchesExact(item);
   }

   public boolean matches(ILiquidStack liquid) {
      return this.update() && this.item.matches(liquid);
   }

   public boolean contains(IIngredient ingredient) {
      return this.update() && this.item.contains(ingredient);
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      if (this.update()) {
         return item.applyTransform(item, byPlayer);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public IItemStack applyNewTransform(IItemStack item) {
      if (this.update()) {
         return item.applyNewTransform(item);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public boolean hasNewTransformers() {
      return this.update() && this.item.hasNewTransformers();
   }

   public boolean hasTransformers() {
      return this.update() && this.item.hasTransformers();
   }

   public IIngredient transform(IItemTransformer transformer) {
      if (this.update()) {
         return this.item.transform(transformer);
      } else {
         throw new IllegalStateException("GhostItemStack not yet initialized!");
      }
   }

   public Object getInternal() {
      if (!this.update()) {
         CraftTweakerAPI.logError("Trying to access Ghost item before it's ready: <item:" + this.name + ":" + this.meta + ">");
      }

      return this.item.getInternal();
   }

   public String toCommandString() {
      return this.update() ? this.item.toCommandString() : "(GhostItemStack) <item:" + this.name + ":" + this.meta + ">";
   }
}
