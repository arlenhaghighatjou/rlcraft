package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponMeleeAxe extends WeaponToolMeleeBase {
   public WeaponMeleeAxe(ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
      super(material, name, attackspeed, range, attackD, fear, id);
   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      super.onCreated(stack, worldIn, playerIn);
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      boolean flag = super.hitEntity(stack, target, attacker);
      if (flag) {
         double chance = 0.25D;
         int amp = 0;
         if (this.calling) {
            chance = 0.5D;
            amp = 1;
         }

         if (attacker.world.rand.nextDouble() < chance) {
            SRPPotions.applyStackPotion(SRPPotions.CORRO_E, target, 100, amp);
         }
      }

      return flag;
   }

   public EnumRarity getRarity(ItemStack stack) {
      return super.getRarity(stack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.YELLOW + I18n.format("tootip.srparasites.weaponm." + this.idTool, new Object[0]));
      tooltip.add(TextFormatting.RED + I18n.format("tootip.srparasites.weaponm." + this.idTool * 10, new Object[0]));
      if (this.calling && SRPConfigSystems.useOneMind) {
         tooltip.add(TextFormatting.BLACK + I18n.format("tootip.srparasites.weaponm." + this.idTool * 100, new Object[0]));
      }

   }
}
