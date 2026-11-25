package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.Arrow;
import com.github.fnar.minecraft.item.Potion;
import com.github.fnar.minecraft.item.RldItemStack;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

public class ArrowMapper1_12 {
   public ArrowMapper1_12() {
   }

   public ItemStack map(RldItemStack rldItemStack) {
      return this.map((Arrow)rldItemStack.getItem());
   }

   public ItemStack map(Arrow arrow) {
      return (ItemStack)arrow.getTip().map(ArrowMapper1_12::getTippedArrowStack).orElseGet(() -> {
         return new ItemStack(Items.ARROW);
      });
   }

   private static ItemStack getTippedArrowStack(Potion potion) {
      PotionType potionType = (new PotionMapper1_12()).mapToPotionType(potion.getEffect(), false, false);
      String tipString = ((ResourceLocation)PotionType.REGISTRY.getNameForObject(potionType)).toString();
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("Potion", tipString);
      arrow.setTagCompound(nbt);
      return arrow;
   }
}
