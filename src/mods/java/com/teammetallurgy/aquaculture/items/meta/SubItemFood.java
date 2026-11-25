package com.teammetallurgy.aquaculture.items.meta;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SubItemFood {
   public int damage;
   public MetaItemFood item;
   public String unlocalizedName;
   public final int itemUseDuration;
   private final int healAmount;
   private final float saturationModifier;
   private boolean alwaysEdible;
   private int eatTime;

   public SubItemFood(MetaItemFood metaItem, int heal, float saturation) {
      this.item = metaItem;
      this.damage = this.item.addSubItem(this);
      this.itemUseDuration = 32;
      this.healAmount = heal;
      this.saturationModifier = saturation;
      this.eatTime = 32;
   }

   public SubItemFood setCreativeTab(CreativeTabs tab) {
      this.item.setCreativeTab(tab);
      return this;
   }

   public SubItemFood setTranslationKey(String name) {
      this.unlocalizedName = name;
      return this;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.getItemStack(1);
   }

   @Nonnull
   public ItemStack getItemStack(int amount) {
      return new ItemStack(this.item, amount, this.damage);
   }

   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      return this.unlocalizedName;
   }

   public SubItemFood setEatTime(int eatTime) {
      this.eatTime = eatTime;
      return this;
   }

   public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
      return this.eatTime;
   }

   public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
      return EnumAction.EAT;
   }

   @Nonnull
   public ItemStack onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
      if (player.canEat(this.alwaysEdible)) {
         player.setActiveHand(hand);
      }

      return stack;
   }

   public int getHealAmount() {
      return this.healAmount;
   }

   public float getSaturationModifier() {
      return this.saturationModifier;
   }
}
