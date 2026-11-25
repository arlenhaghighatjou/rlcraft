package com.endertech.minecraft.forge.combat;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.tools.ForgeTool;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgeWeapon extends ForgeTool {
   public static final String MODIFIER_NAME = "Weapon modifier";

   public ForgeWeapon(ForgeMod mod, UnitConfig config, ForgeMaterial material, float attackDamage, float attackSpeed, int durability) {
      super(mod, config, material, attackDamage, attackSpeed, durability);
      this.setCreativeTab(CreativeTabs.COMBAT);
   }

   public String getModifierName() {
      return "Weapon modifier";
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(1, attacker);
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
      super.addInformation(stack, world, tooltip, flag);
   }
}
