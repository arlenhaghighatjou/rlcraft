package com.mujmajnkraft.bettersurvival.items;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDagger extends ItemCustomWeapon {
   public ItemDagger(ToolMaterial material) {
      super(material, ForgeConfigHandler.weapons.daggerDmgMod, ForgeConfigHandler.weapons.daggerSpd);
      this.setRegistryName("mujmajnkraftsbettersurvival", "item" + material.name().toLowerCase() + "dagger");
      this.setUnlocalizedName(material.name().toLowerCase() + "dagger");
   }

   public float getBackstabMultiplier(EntityLivingBase user, Entity target, boolean offhand) {
      double attackerYaw = Math.toRadians((double)user.rotationYaw);
      double targetYaw = Math.toRadians((double)target.rotationYaw);
      if (Math.abs(Math.sin(attackerYaw) - Math.sin(targetYaw)) < 0.5D && Math.abs(Math.cos(attackerYaw) - Math.cos(targetYaw)) < 0.5D) {
         int l = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.assassinate, offhand ? user.getHeldItemOffhand() : user.getHeldItemMainhand());
         return 2.0F + (float)l / 3.0F;
      } else {
         return 1.0F;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String s = I18n.format("mujmajnkraftsbettersurvival.dagger.desc", new Object[0]);
      tooltip.add(TextFormatting.AQUA + s);
   }
}
