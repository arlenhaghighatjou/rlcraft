package com.teammetallurgy.aquaculture.items.meta;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MetaItemFood extends ItemFood {
   private ArrayList<SubItemFood> subItems;

   public MetaItemFood() {
      super(0, 0.0F, false);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   public int addSubItem(SubItemFood subItem) {
      if (this.subItems == null) {
         this.subItems = new ArrayList();
      }

      this.subItems.add(subItem);
      return this.subItems.size() - 1;
   }

   @Nonnull
   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      int i = MathHelper.clamp(stack.getItemDamage(), 0, this.subItems.size() - 1);
      String uname = ((SubItemFood)this.subItems.get(i)).getUnlocalizedName(stack);
      uname = uname.replace(" ", "_");
      return "item." + uname;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(SubItemFood subItem : this.subItems) {
            items.add(subItem.getItemStack());
         }
      }

   }

   public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItemFood)this.subItems.get(damage)).getMaxItemUseDuration(stack) : 0;
   }

   @Nonnull
   public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItemFood)this.subItems.get(damage)).getItemUseAction(stack) : EnumAction.NONE;
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      int damage = stack.getItemDamage();
      if (damage >= 0 && damage < this.subItems.size()) {
         ItemStack result = ((SubItemFood)this.subItems.get(damage)).onItemRightClick(stack, world, player, hand);
         return new ActionResult(EnumActionResult.SUCCESS, result);
      } else {
         return new ActionResult(EnumActionResult.FAIL, stack);
      }
   }

   public int getHealAmount(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItemFood)this.subItems.get(damage)).getHealAmount() : super.getHealAmount(stack);
   }

   public float getSaturationModifier(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItemFood)this.subItems.get(damage)).getSaturationModifier() : super.getSaturationModifier(stack);
   }
}
