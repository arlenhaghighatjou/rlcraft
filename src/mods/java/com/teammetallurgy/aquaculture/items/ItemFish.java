package com.teammetallurgy.aquaculture.items;

import com.google.common.base.Preconditions;
import com.teammetallurgy.aquaculture.loot.BiomeType;
import com.teammetallurgy.aquaculture.loot.FishLoot;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFish extends Item {
   public NonNullList<ItemFish.Fish> fish;

   public ItemFish() {
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.fish = NonNullList.create();
   }

   public void addFish(String name, int filletAmount, int minWeight, int maxWeight, BiomeType biome, int rarity) {
      this.addFish(name, filletAmount, minWeight, maxWeight, new BiomeType[]{biome}, rarity);
   }

   public void addFish(String name, int filletAmount, int minWeight, int maxWeight, BiomeType[] biomes, int rarity) {
      this.fish.add(new ItemFish.Fish(name, filletAmount, minWeight, maxWeight));

      for(BiomeType biome : biomes) {
         FishLoot.instance().addFish(this.getItemStackFish(name), biome, rarity);
      }

   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      return stack.hasTagCompound() && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Prefix") ? stack.getTagCompound().getString("Prefix") + " " + super.getItemStackDisplayName(stack) : super.getItemStackDisplayName(stack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> toolTip, ITooltipFlag tooltipType) {
      if (stack.hasTagCompound() && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Weight")) {
         float weight = stack.getTagCompound().getFloat("Weight");
         DecimalFormat df = new DecimalFormat("#,###.##");
         BigDecimal bd = new BigDecimal((double)weight);
         bd = bd.round(new MathContext(3));
         if (bd.doubleValue() > 999.0D) {
            toolTip.add("Weight: " + df.format((long)((int)bd.doubleValue())) + "lb");
         } else {
            toolTip.add("Weight: " + bd + "lb");
         }
      }

   }

   public void assignRandomWeight(@Nonnull ItemStack stack, int heavyLineLvl) {
      if (!stack.isEmpty()) {
         ItemFish.Fish f = (ItemFish.Fish)this.fish.get(stack.getItemDamage());
         if (f.maxWeight != 1 || f.minWeight != 1) {
            float min = (float)f.minWeight;
            min = (float)((double)min + (double)((float)f.maxWeight - min) * 0.1D * (double)heavyLineLvl);
            float weight = (new Random()).nextFloat() * ((float)f.maxWeight - min) + min;
            if (!stack.hasTagCompound()) {
               stack.setTagCompound(new NBTTagCompound());
            }

            Preconditions.checkNotNull(stack.getTagCompound(), "tagCompound");
            stack.getTagCompound().setFloat("Weight", weight);
            if (weight <= (float)f.maxWeight * 0.1F) {
               stack.getTagCompound().setString("Prefix", "Juvenile");
            } else if (weight > (float)f.maxWeight * 0.1F && weight <= (float)f.maxWeight * 0.2F) {
               stack.getTagCompound().setString("Prefix", "Small");
            } else if (weight >= (float)f.maxWeight * 0.8F && weight < (float)f.maxWeight * 0.9F) {
               stack.getTagCompound().setString("Prefix", "Large");
            } else if (weight >= (float)f.maxWeight * 0.9F) {
               stack.getTagCompound().setString("Prefix", "Massive");
            }

         }
      }
   }

   @Nonnull
   public ItemStack getItemStackFish(String name) {
      for(int i = 0; i < this.fish.size(); ++i) {
         if (((ItemFish.Fish)this.fish.get(i)).name.equals(name)) {
            return new ItemStack(this, 1, i);
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      int i = MathHelper.clamp(stack.getItemDamage(), 0, this.fish.size());
      String uname = super.getUnlocalizedName() + "." + ((ItemFish.Fish)this.fish.get(i)).name;
      uname = uname.replace(" ", "_");
      return uname;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(int j = 0; j < this.fish.size(); ++j) {
            subItems.add(new ItemStack(this, 1, j));
         }
      }

   }

   public class Fish {
      public String name;
      public int filletAmount;
      public int minWeight;
      public int maxWeight;

      public Fish(String name, int amount, int min, int max) {
         this.name = name;
         this.filletAmount = amount;
         this.maxWeight = max;
         this.minWeight = min;
      }
   }
}
