package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.grapplemod;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LongFallBoots extends ItemArmor {
   public LongFallBoots(ArmorMaterial material, int type) {
      super(material, 0, EntityEquipmentSlot.FEET);
      this.setUnlocalizedName("longfallboots");
      MinecraftForge.EVENT_BUS.register(this);
      this.setCreativeTab(grapplemod.tabGrapplemod);
   }

   @SubscribeEvent
   public void onLivingHurtEvent(LivingHurtEvent event) {
      if (event.getEntity() != null && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();

         for(ItemStack armor : player.getArmorInventoryList()) {
            if (armor != null && armor.getItem() instanceof LongFallBoots && event.getSource() == DamageSource.FLY_INTO_WALL) {
               System.out.println("Flew into wall");
               event.setCanceled(true);
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingFallEvent(LivingFallEvent event) {
      if (event.getEntity() != null && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();

         for(ItemStack armor : player.getArmorInventoryList()) {
            if (armor != null && armor.getItem() instanceof LongFallBoots) {
               event.setCanceled(true);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
      if (!stack.isItemEnchanted() && GrappleConfig.getconf().longfallbootsrecipe) {
         list.add("Right click a Grappling Hook Modifier block with Feather Falling IV Diamond Boots to obtain");
      }

      list.add("Cancels fall damage when worn");
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack stack = new ItemStack(this);
         items.add(stack);
         stack = new ItemStack(this);
         stack.addEnchantment(grapplemod.wallrunenchantment, 1);
         stack.addEnchantment(grapplemod.doublejumpenchantment, 1);
         stack.addEnchantment(grapplemod.slidingenchantment, 1);
         items.add(stack);
      }

   }
}
