package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJuice extends ItemDrinkBase {
   public ItemJuice() {
      this.setHasSubtypes(true);
   }

   public void runSecondaryEffect(EntityPlayer player, ItemStack stack) {
      ItemJuice.JuiceEnum type = this.getEnumForStack(stack);
      if (type == ItemJuice.JuiceEnum.GOLDEN_APPLE && ModConfig.server.miscellaneous.goldenAppleJuiceEffect) {
         player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
         player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
      }

   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(ItemJuice.JuiceEnum juice : ItemJuice.JuiceEnum.values()) {
            items.add(new ItemStack(this, 1, juice.ordinal()));
         }
      }

   }

   public String getUnlocalizedName(ItemStack stack) {
      return "item.simpledifficulty:juice_" + this.getEnumForStack(stack).toString();
   }

   public int getThirstLevel(ItemStack stack) {
      return this.getEnumForStack(stack).getThirstLevel();
   }

   public float getSaturationLevel(ItemStack stack) {
      return this.getEnumForStack(stack).getSaturation();
   }

   public float getDirtyChance(ItemStack stack) {
      return this.getEnumForStack(stack).getDirtyChance();
   }

   protected ItemJuice.JuiceEnum getEnumForStack(ItemStack stack) {
      return stack.getMetadata() >= ItemJuice.JuiceEnum.values().length ? ItemJuice.JuiceEnum.values()[0] : ItemJuice.JuiceEnum.values()[stack.getMetadata()];
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      ItemJuice.JuiceEnum type = this.getEnumForStack(stack);
      return type != ItemJuice.JuiceEnum.GOLDEN_APPLE && type != ItemJuice.JuiceEnum.GOLDEN_CARROT && type != ItemJuice.JuiceEnum.GOLDEN_MELON ? super.hasEffect(stack) : true;
   }

   public static enum JuiceEnum {
      APPLE("apple", 8, 6.4F),
      BEETROOT("beetroot", 10, 8.0F),
      CACTUS("cactus", 9, 2.7F),
      CARROT("carrot", 8, 4.8F),
      CHORUS_FRUIT("chorus_fruit", 12, 7.2F),
      GOLDEN_APPLE("golden_apple", 20, 20.0F),
      GOLDEN_CARROT("golden_carrot", 14, 14.0F),
      GOLDEN_MELON("golden_melon", 16, 16.0F),
      MELON("melon", 8, 4.0F),
      PUMPKIN("pumpkin", 7, 4.9F);

      private String name;
      private int thirst;
      private float saturation;

      public String getName() {
         return this.name;
      }

      public int getThirstLevel() {
         return this.thirst;
      }

      public float getSaturation() {
         return this.saturation;
      }

      public float getDirtyChance() {
         return 0.0F;
      }

      private JuiceEnum(String name, int thirst, float saturation) {
         this.name = name;
         this.thirst = thirst;
         this.saturation = saturation;
      }

      public String toString() {
         return this.getName();
      }
   }
}
