package com.teammetallurgy.aquaculture.items.meta;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MetaItem extends Item {
   ArrayList<SubItem> subItems;

   public MetaItem() {
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   public int addSubItem(SubItem subItem) {
      if (this.subItems == null) {
         this.subItems = new ArrayList();
      }

      this.subItems.add(subItem);
      return this.subItems.size() - 1;
   }

   @Nonnull
   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      int i = MathHelper.clamp(stack.getItemDamage(), 0, this.subItems.size() - 1);
      String uname = ((SubItem)this.subItems.get(i)).getUnlocalizedName(stack);
      uname = uname.replace(" ", "_");
      return "item." + uname;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(SubItem subItem : this.subItems) {
            items.add(subItem.getItemStack());
         }
      }

   }

   @Nonnull
   public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving) {
      int damage = stack.getItemDamage();
      if (entityLiving instanceof EntityPlayer && damage >= 0 && damage < this.subItems.size()) {
         EntityPlayer player = (EntityPlayer)entityLiving;
         return ((SubItem)this.subItems.get(damage)).onEaten(stack, world, player);
      } else {
         return stack;
      }
   }

   public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItem)this.subItems.get(damage)).getMaxItemUseDuration(stack) : 0;
   }

   @Nonnull
   public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
      int damage = stack.getItemDamage();
      return damage >= 0 && damage < this.subItems.size() ? ((SubItem)this.subItems.get(damage)).getItemUseAction(stack) : EnumAction.NONE;
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      int damage = itemStack.getItemDamage();
      if (damage >= 0 && damage < this.subItems.size()) {
         ItemStack result = ((SubItem)this.subItems.get(damage)).onItemRightClick(itemStack, world, player);
         return new ActionResult(EnumActionResult.SUCCESS, result);
      } else {
         return new ActionResult(EnumActionResult.FAIL, itemStack);
      }
   }
}
