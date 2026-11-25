package crafttweaker.mc1120.item;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.data.DataMap;
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
import crafttweaker.api.item.IMutableItemStack;
import crafttweaker.api.item.IngredientItem;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.actions.ActionSetBlockHardness;
import crafttweaker.mc1120.actions.ActionSetStackMaxDamage;
import crafttweaker.mc1120.actions.ActionSetStackSize;
import crafttweaker.mc1120.actions.ActionSetStackTranslation;
import crafttweaker.mc1120.block.MCItemBlock;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.enchantments.MCEnchantment;
import crafttweaker.mc1120.game.MCGame;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import crafttweaker.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;

public class MCItemStack implements IItemStack {
   public static final IItemStack EMPTY = new MCItemStack();
   private final ItemStack stack;
   protected final ItemStack origin;
   private final List<IItemStack> items;
   protected boolean matchTagExact;
   protected IData tag;
   private boolean wildcardSize;

   private MCItemStack() {
      this.matchTagExact = true;
      this.tag = null;
      this.stack = ItemStack.EMPTY;
      this.origin = ItemStack.EMPTY;
      this.items = Collections.singletonList(this);
   }

   public MCItemStack(ItemStack itemStack) {
      this.matchTagExact = true;
      this.tag = null;
      if (itemStack.isEmpty()) {
         throw new IllegalArgumentException("stack cannot be null");
      } else {
         this.stack = itemStack.copy();
         this.origin = itemStack;
         this.items = Collections.singletonList(this);
      }
   }

   public MCItemStack(ItemStack itemStack, boolean wildcardSize) {
      this(itemStack);
      this.wildcardSize = wildcardSize;
   }

   protected MCItemStack(ItemStack itemStack, IData tag) {
      this.matchTagExact = true;
      this.tag = null;
      if (itemStack.isEmpty()) {
         throw new IllegalArgumentException("stack cannot be null");
      } else {
         this.stack = itemStack;
         this.origin = itemStack;
         this.items = Collections.singletonList(this);
         this.tag = tag;
      }
   }

   protected MCItemStack(ItemStack itemStack, IData tag, boolean wildcardSize) {
      this.matchTagExact = true;
      this.tag = null;
      if (itemStack.isEmpty()) {
         throw new IllegalArgumentException("stack cannot be null");
      } else {
         this.stack = itemStack;
         this.origin = itemStack;
         this.items = Collections.singletonList(this);
         this.tag = tag;
         this.wildcardSize = wildcardSize;
      }
   }

   private MCItemStack(ItemStack itemStack, int unused) {
      this.matchTagExact = true;
      this.tag = null;
      if (itemStack.isEmpty()) {
         throw new IllegalArgumentException("stack cannot be null");
      } else {
         this.stack = itemStack;
         this.origin = itemStack;
         this.items = Collections.singletonList(this);
      }
   }

   public static MCItemStack createNonCopy(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() ? new MCItemStack(itemStack, -1) : null;
   }

   public IItemDefinition getDefinition() {
      return new MCItemDefinition(this.stack.getItem().getRegistryName().toString(), this.stack.getItem());
   }

   public boolean isItemBlock() {
      return this.stack.getItem() instanceof ItemBlock;
   }

   public String getName() {
      return this.stack.getUnlocalizedName();
   }

   public String getDisplayName() {
      return this.stack.getDisplayName();
   }

   public void setDisplayName(String name) {
      ActionSetStackTranslation action = new ActionSetStackTranslation(this, this.getName() + ".name", name);
      MCGame.TRANSLATION_ACTIONS.add(action);
      CraftTweakerAPI.apply(action);
   }

   public int getMaxStackSize() {
      return this.stack.getMaxStackSize();
   }

   public void setMaxStackSize(int size) {
      CraftTweakerAPI.apply(new ActionSetStackSize((ItemStack)this.getInternal(), size));
   }

   public float getBlockHardness() {
      return Block.getBlockFromItem(this.stack.getItem()).blockHardness;
   }

   public void setBlockHardness(float hardness) {
      CraftTweakerAPI.apply(new ActionSetBlockHardness(this.stack, hardness));
   }

   public int getDamage() {
      return this.stack.getItemDamage();
   }

   public IData getTag() {
      if (this.tag == null) {
         if (this.stack.getTagCompound() == null) {
            return DataMap.EMPTY;
         }

         this.tag = NBTConverter.from(this.stack.getTagCompound(), true);
      }

      return this.tag;
   }

   public int getMaxDamage() {
      return this.stack.getMaxDamage();
   }

   public void setMaxDamage(int damage) {
      CraftTweakerAPI.apply(new ActionSetStackMaxDamage(this.stack, damage));
   }

   public ILiquidStack getLiquid() {
      FluidStack liquid = FluidUtil.getFluidContained(this.stack);
      return liquid == null ? null : new MCLiquidStack(liquid);
   }

   public IIngredient anyDamage() {
      ItemStack result = new ItemStack(this.stack.getItem(), this.stack.getCount(), 32767);
      result.setTagCompound(this.stack.getTagCompound());
      return new MCItemStack(result, this.tag);
   }

   public IItemStack withDamage(int damage) {
      ItemStack result = this.stack.copy();
      result.setItemDamage(damage);
      return new MCItemStack(result, this.tag);
   }

   public IItemStack anyAmount() {
      ItemStack result = this.stack.copy();
      result.setCount(1);
      return new MCItemStack(result, this.tag, true);
   }

   public IItemStack withAmount(int amount) {
      ItemStack result = this.stack.copy();
      result.setCount(amount);
      return new MCItemStack(result, this.tag);
   }

   public IItemStack withTag(IData tag, boolean matchTagExact) {
      ItemStack result = this.stack.copy();
      if (tag == null) {
         result.setTagCompound((NBTTagCompound)null);
      } else {
         result.setTagCompound((NBTTagCompound)NBTConverter.from(tag));
      }

      MCItemStack out = new MCItemStack(result, tag);
      out.matchTagExact = matchTagExact;
      return out;
   }

   public boolean getMatchTagExact() {
      return this.matchTagExact;
   }

   public IItemStack withTag(IData tag) {
      return this.withTag(tag, true);
   }

   public IItemStack updateTag(IData tagUpdate) {
      return this.updateTag(tagUpdate, true);
   }

   public IItemStack withEmptyTag() {
      ItemStack result = new ItemStack(this.stack.getItem(), this.stack.getCount(), this.stack.getItemDamage());
      result.setTagCompound(new NBTTagCompound());
      return new MCItemStack(result, NBTConverter.from(new NBTTagCompound(), true));
   }

   public IItemStack updateTag(IData tagUpdate, boolean matchTagExact) {
      if (this.tag == null) {
         if (this.stack.getTagCompound() == null) {
            return this.withTag(tagUpdate, matchTagExact);
         }

         this.tag = NBTConverter.from(this.stack.getTagCompound(), true);
      }

      IData updated = this.tag.update(tagUpdate);
      return this.withTag(updated, matchTagExact);
   }

   public IItemStack removeTag(String tag) {
      ItemStack result = this.stack.copy();
      if (tag == null) {
         result.setTagCompound((NBTTagCompound)null);
      } else {
         result.getTagCompound().removeTag(tag);
      }

      IData dataTag = NBTConverter.from(result.getTagCompound(), false);
      return new MCItemStack(result, dataTag);
   }

   public String getMark() {
      return null;
   }

   public int getAmount() {
      return this.stack.getCount();
   }

   public List<IItemStack> getItems() {
      return this.items;
   }

   public IItemStack[] getItemArray() {
      return (IItemStack[])this.getItems().toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      return (List)this.items.stream().filter(Objects::nonNull).map(CraftTweakerMC::getItemStack).map(FluidUtil::getFluidContained).filter(Objects::nonNull).map(CraftTweakerMC::getILiquidStack).collect(Collectors.toList());
   }

   public IItemStack amount(int amount) {
      return this.withAmount(amount);
   }

   public WeightedItemStack percent(float chance) {
      return new WeightedItemStack(this, chance * 0.01F);
   }

   public WeightedItemStack weight(float chance) {
      return new WeightedItemStack(this, chance);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientItem(this, (String)null, ArrayUtil.EMPTY_CONDITIONS, new IItemTransformerNew[]{transformer}, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientItem(this, (String)null, new IItemCondition[]{condition}, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient marked(String mark) {
      return new IngredientItem(this, mark, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      if (item == null) {
         return false;
      } else {
         ItemStack internal = (ItemStack)item.getInternal();
         if (this.stack.hasTagCompound() && this.matchTagExact) {
            return this.matchesExact(item);
         } else {
            return !internal.isEmpty() && !this.stack.isEmpty() && internal.getItem() == this.stack.getItem() && (this.wildcardSize || internal.getCount() >= this.stack.getCount()) && (this.stack.getItemDamage() == 32767 || internal.getItemDamage() == 32767 || this.stack.getItemDamage() == internal.getItemDamage() || !this.stack.getHasSubtypes() && !this.stack.getItem().isDamageable());
         }
      }
   }

   public boolean matchesExact(IItemStack item) {
      if (item == null) {
         return false;
      } else {
         ItemStack internal = (ItemStack)item.getInternal();
         NBTTagCompound internalTag = internal.getTagCompound();
         NBTTagCompound stackTag = this.stack.getTagCompound();
         if (internal.hasTagCompound() != this.stack.hasTagCompound()) {
            return false;
         } else {
            boolean itemMatches = this.stack.getItem() == internal.getItem() && (this.stack.getItemDamage() == 32767 || internal.getItemDamage() == 32767 || this.stack.getMetadata() == internal.getMetadata());
            if (itemMatches) {
               if (internalTag == null && stackTag == null) {
                  return true;
               }

               if (!NBTConverter.from(internalTag, true).contains(NBTConverter.from(stackTag, true))) {
                  return false;
               }
            }

            return itemMatches;
         }
      }
   }

   public boolean matches(ILiquidStack liquid) {
      return false;
   }

   public boolean contains(IIngredient ingredient) {
      if (ingredient == null) {
         return false;
      } else {
         List<IItemStack> iitems = ingredient.getItems();
         return iitems != null && iitems.size() == 1 && this.matches((IItemStack)iitems.get(0));
      }
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      return item;
   }

   public boolean hasNewTransformers() {
      return false;
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientItem(this, (String)null, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, new IItemTransformer[]{transformer});
   }

   public Object getInternal() {
      return this.stack;
   }

   public IBlock asBlock() {
      ResourceLocation name = (ResourceLocation)Block.REGISTRY.getNameForObject(Block.getBlockFromItem(this.stack.getItem()));
      if (Block.REGISTRY.containsKey(name)) {
         return new MCItemBlock(this.stack);
      } else {
         throw new ClassCastException("This item is not a block");
      }
   }

   public List<IOreDictEntry> getOres() {
      List<IOreDictEntry> result = new ArrayList();

      label32:
      for(String key : OreDictionary.getOreNames()) {
         Iterator var6 = OreDictionary.getOres(key).iterator();

         while(true) {
            if (!var6.hasNext()) {
               continue label32;
            }

            ItemStack is = (ItemStack)var6.next();
            if (is.getItem() == this.stack.getItem() && (is.getItemDamage() == 32767 || is.getItemDamage() == this.stack.getItemDamage())) {
               break;
            }
         }

         result.add(CraftTweakerAPI.oreDict.get(key));
      }

      return result;
   }

   public IItemStack withDisplayName(String name) {
      NBTTagCompound tagComp;
      if (this.stack.hasTagCompound() && this.stack.getTagCompound() != null) {
         tagComp = this.stack.getTagCompound();
      } else {
         tagComp = new NBTTagCompound();
      }

      NBTTagCompound display;
      if (tagComp.hasKey("display") && tagComp.getTag("display") instanceof NBTTagCompound) {
         display = (NBTTagCompound)tagComp.getTag("display");
      } else {
         display = new NBTTagCompound();
      }

      display.setString("Name", name);
      tagComp.setTag("display", display);
      ItemStack newStack = this.stack.copy();
      newStack.setTagCompound(tagComp);
      return new MCItemStack(newStack);
   }

   public IItemStack withLore(String[] lore) {
      NBTTagCompound tagComp;
      if (this.stack.hasTagCompound() && this.stack.getTagCompound() != null) {
         tagComp = this.stack.getTagCompound();
      } else {
         tagComp = new NBTTagCompound();
      }

      NBTTagCompound display;
      if (tagComp.hasKey("display") && tagComp.getTag("display") instanceof NBTTagCompound) {
         display = (NBTTagCompound)tagComp.getTag("display");
      } else {
         display = new NBTTagCompound();
      }

      NBTTagList loreList;
      if (display.hasKey("Lore") && display.getTag("Lore") instanceof NBTTagList) {
         loreList = (NBTTagList)display.getTag("Lore");
      } else {
         loreList = new NBTTagList();
      }

      for(String s : lore) {
         loreList.appendTag(new NBTTagString(s));
      }

      display.setTag("Lore", loreList);
      tagComp.setTag("display", display);
      ItemStack newStack = this.stack.copy();
      newStack.setTagCompound(tagComp);
      return new MCItemStack(newStack);
   }

   public List<String> getToolClasses() {
      return new ArrayList(this.stack.getItem().getToolClasses(this.stack));
   }

   public int getItemEnchantability() {
      return this.stack.getItem().getItemEnchantability(this.stack);
   }

   public IItemStack getContainerItem() {
      return new MCItemStack(this.stack.getItem().getContainerItem(this.stack));
   }

   public boolean hasContainerItem() {
      return this.stack.getItem().hasContainerItem(this.stack);
   }

   public IEntityItem createEntityItem(IWorld world, int x, int y, int z) {
      return CraftTweakerMC.getIEntityItem(new EntityItem(CraftTweakerMC.getWorld(world), (double)x, (double)y, (double)z, this.stack));
   }

   public IEntityItem createEntityItem(IWorld world, float x, float y, float z) {
      return CraftTweakerMC.getIEntityItem(new EntityItem(CraftTweakerMC.getWorld(world), (double)x, (double)y, (double)z, this.stack));
   }

   public IEntityItem createEntityItem(IWorld world, IBlockPos pos) {
      return this.createEntityItem(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean isBeaconPayment() {
      return this.stack.getItem().isBeaconPayment(this.stack);
   }

   public boolean canPlaceOn(IBlockDefinition block) {
      return this.stack.canPlaceOn((Block)block.getInternal());
   }

   public boolean canDestroy(IBlockDefinition block) {
      return this.stack.canDestroy((Block)block.getInternal());
   }

   public boolean canHarvestBlock(IBlockState block) {
      return this.stack.canHarvestBlock((net.minecraft.block.state.IBlockState)block.getInternal());
   }

   public int getRepairCost() {
      return this.stack.getRepairCost();
   }

   public void setRepairCost(int repairCost) {
      this.stack.setRepairCost(repairCost);
   }

   public boolean canEditBlocks() {
      return false;
   }

   public boolean isOnItemFrame() {
      return this.stack.isOnItemFrame();
   }

   public boolean isItemEnchanted() {
      return this.stack.isItemEnchanted();
   }

   public boolean isItemDamaged() {
      return this.stack.isItemDamaged();
   }

   public boolean isDamageable() {
      return this.stack.isItemStackDamageable();
   }

   public boolean isStackable() {
      return this.stack.isStackable();
   }

   public void addEnchantment(IEnchantment enchantment) {
      this.stack.addEnchantment((Enchantment)enchantment.getDefinition().getInternal(), enchantment.getLevel());
   }

   public boolean canApplyAtEnchantingTable(IEnchantmentDefinition enchantment) {
      return this.stack.getItem().canApplyAtEnchantingTable(this.stack, (Enchantment)enchantment.getInternal());
   }

   public List<IEnchantment> getEnchantments() {
      ArrayList<IEnchantment> output = new ArrayList();
      if (!this.stack.isEmpty() && this.stack.hasTagCompound()) {
         IData tag = NBTConverter.from(this.stack.getTagCompound(), false);
         if (tag.contains(new DataString("ench"))) {
            List<IData> enchantmentList = tag.memberGet("ench").asList();
            enchantmentList.stream().filter((ench) -> {
               return ench.contains(new DataString("id")) && ench.contains(new DataString("lvl"));
            }).forEach((ench) -> {
               output.add(new MCEnchantment(ench.memberGet("id").asInt(), ench.memberGet("lvl").asInt()));
            });
         }

         return output;
      } else {
         return output;
      }
   }

   public boolean isItemEnchantable() {
      return this.stack.isItemEnchantable();
   }

   public boolean hasEffect() {
      return this.stack.hasEffect();
   }

   public boolean hasDisplayName() {
      return this.stack.hasDisplayName();
   }

   public void clearCustomName() {
      this.stack.clearCustomName();
   }

   public boolean hasTag() {
      return this.stack.hasTagCompound();
   }

   public void damageItem(int amount, IEntity entity) {
      CraftTweakerAPI.logWarning("Cannot damage IItemStack, make it mutable with `mutable` first!");
   }

   public int getMetadata() {
      return this.stack.getMetadata();
   }

   public boolean getHasSubtypes() {
      return this.stack.getHasSubtypes();
   }

   public float getStrengthAgainstBlock(IBlockState blockState) {
      return this.stack.getDestroySpeed((net.minecraft.block.state.IBlockState)blockState.getInternal());
   }

   public IItemStack splitStack(int amount) {
      return new MCItemStack(this.stack.splitStack(amount));
   }

   public boolean isEmpty() {
      return this.stack.isEmpty();
   }

   public int getItemBurnTime() {
      return TileEntityFurnace.getItemBurnTime(this.stack);
   }

   public boolean showsDurabilityBar() {
      return this.stack.getItem().showDurabilityBar(this.stack);
   }

   public boolean hasCustomEntity() {
      return this.stack.getItem().hasCustomEntity(this.stack);
   }

   public IMutableItemStack mutable() {
      return this.origin.isEmpty() ? null : new MCMutableItemStack(this.origin);
   }

   public int hashCode() {
      int hash = 7;
      hash = 41 * hash + this.stack.getItem().hashCode();
      hash = 41 * hash + this.stack.getItemDamage();
      hash = 41 * hash + this.stack.getCount();
      hash = 41 * hash + (this.stack.getTagCompound() == null ? 0 : this.stack.getTagCompound().hashCode());
      hash = 41 * hash + (this.wildcardSize ? 1 : 0);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         MCItemStack other = (MCItemStack)obj;
         return this.stack.getItem() == other.stack.getItem() && this.stack.getItemDamage() == other.stack.getItemDamage() && this.stack.getCount() == other.stack.getCount() && (this.stack.getTagCompound() == other.stack.getTagCompound() || !this.stack.equals(other.stack)) && this.wildcardSize == other.wildcardSize;
      }
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      result.append('<');
      result.append(Item.REGISTRY.getNameForObject(this.stack.getItem()));
      if (this.stack.getItemDamage() == 32767) {
         result.append(":*");
      } else if (this.stack.getItemDamage() > 0) {
         result.append(':').append(this.stack.getItemDamage());
      }

      result.append('>');
      if (this.stack.getTagCompound() != null) {
         result.append(".withTag(");
         result.append(NBTConverter.from(this.stack.getTagCompound(), this.wildcardSize).toString());
         result.append(")");
      }

      if (!this.wildcardSize && this.stack.getCount() > 1) {
         result.append(" * ").append(this.stack.getCount());
      }

      return result.toString();
   }

   public String toCommandString() {
      return this.toString();
   }

   public boolean isFood() {
      return this.stack.getItem() instanceof ItemFood;
   }

   public float getSaturation() {
      if (this.isFood()) {
         ItemFood item = (ItemFood)this.stack.getItem();
         return item.getSaturationModifier(this.stack);
      } else {
         return 0.0F;
      }
   }

   public int getHealAmount() {
      if (this.isFood()) {
         ItemFood item = (ItemFood)this.stack.getItem();
         return item.getHealAmount(this.stack);
      } else {
         return 0;
      }
   }

   public int getHarvestLevel(String toolClass) {
      return this.stack.getItem().getHarvestLevel(this.stack, toolClass, (EntityPlayer)null, (net.minecraft.block.state.IBlockState)null);
   }

   public int getHarvestLevel(String toolClass, IPlayer player, IBlockState blockState) {
      return this.stack.getItem().getHarvestLevel(this.stack, toolClass, CraftTweakerMC.getPlayer(player), CraftTweakerMC.getBlockState(blockState));
   }
}
