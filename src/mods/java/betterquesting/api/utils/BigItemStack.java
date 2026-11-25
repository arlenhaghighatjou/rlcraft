package betterquesting.api.utils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.OreIngredient;

public class BigItemStack {
   private static final OreIngredient NO_ORE = new OreIngredient("");
   private final ItemStack baseStack;
   public int stackSize;
   private String oreDict;
   private OreIngredient oreIng;

   public BigItemStack(ItemStack stack) {
      this.oreDict = "";
      this.oreIng = NO_ORE;
      this.baseStack = stack.copy();
      this.stackSize = this.baseStack.getCount();
      this.baseStack.setCount(1);
   }

   public BigItemStack(@Nonnull Block block) {
      this(block, 1);
   }

   public BigItemStack(@Nonnull Block block, int amount) {
      this(block, amount, 0);
   }

   public BigItemStack(@Nonnull Block block, int amount, int damage) {
      this(Item.getItemFromBlock(block), amount, damage);
   }

   public BigItemStack(@Nonnull Item item) {
      this(item, 1);
   }

   public BigItemStack(@Nonnull Item item, int amount) {
      this(item, amount, 0);
   }

   public BigItemStack(@Nonnull Item item, int amount, int damage) {
      this.oreDict = "";
      this.oreIng = NO_ORE;
      this.baseStack = new ItemStack(item, 1, damage);
      this.stackSize = amount;
   }

   public ItemStack getBaseStack() {
      return this.baseStack;
   }

   public boolean hasOreDict() {
      return !StringUtils.isNullOrEmpty(this.oreDict) && this.oreIng.getMatchingStacks().length > 0;
   }

   @Nonnull
   public String getOreDict() {
      return this.oreDict;
   }

   @Nonnull
   public OreIngredient getOreIngredient() {
      return this.oreIng;
   }

   public BigItemStack setOreDict(@Nonnull String ore) {
      this.oreDict = ore;
      this.oreIng = ore.length() <= 0 ? NO_ORE : new OreIngredient(ore);
      return this;
   }

   public NBTTagCompound GetTagCompound() {
      return this.baseStack.getTagCompound();
   }

   public void SetTagCompound(NBTTagCompound tags) {
      this.baseStack.setTagCompound(tags);
   }

   public boolean HasTagCompound() {
      return this.baseStack.hasTagCompound();
   }

   public List<ItemStack> getCombinedStacks() {
      List<ItemStack> list = new ArrayList();

      int size;
      for(int tmp1 = Math.max(1, this.stackSize); tmp1 > 0; tmp1 -= size) {
         size = Math.min(tmp1, this.baseStack.getMaxStackSize());
         ItemStack stack = this.baseStack.copy();
         stack.setCount(size);
         list.add(stack);
      }

      return list;
   }

   public BigItemStack copy() {
      BigItemStack stack = new BigItemStack(this.baseStack.copy());
      stack.stackSize = this.stackSize;
      stack.oreDict = this.oreDict;
      stack.oreIng = this.oreIng;
      return stack;
   }

   public boolean equals(Object stack) {
      if (!(stack instanceof ItemStack)) {
         return super.equals(stack);
      } else {
         return this.baseStack.isItemEqual((ItemStack)stack) && ItemStack.areItemStackTagsEqual(this.baseStack, (ItemStack)stack);
      }
   }

   public BigItemStack(@Nonnull NBTTagCompound tags) {
      this.oreDict = "";
      this.oreIng = NO_ORE;
      NBTTagCompound itemNBT = tags.copy();
      itemNBT.setInteger("Count", 1);
      if (tags.hasKey("id", 99)) {
         itemNBT.setString("id", "" + tags.getShort("id"));
      }

      this.stackSize = tags.getInteger("Count");
      this.setOreDict(tags.getString("OreDict"));
      this.baseStack = new ItemStack(itemNBT);
      if (tags.getShort("Damage") < 0) {
         this.baseStack.setItemDamage(32767);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      this.baseStack.writeToNBT(tags);
      tags.setInteger("Count", this.stackSize);
      tags.setString("OreDict", this.oreDict);
      return tags;
   }
}
