package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShieldSilver extends ItemShieldExtraMaterial implements IDamageShield {
   public ItemShieldSilver(String unlocName, int maxDamage, ToolMaterial toolMaterial, String materialOreDictName) {
      super(unlocName, maxDamage, toolMaterial, materialOreDictName);
   }

   @SideOnly(Side.CLIENT)
   public void addEffectsTooltip(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.GOLD + StringHelper.translateFormattedString("onBlock", "tooltip", "spartanshields", StringHelper.translateString("shieldSilver.desc", "tooltip")));
   }

   public void damageShield(ItemStack shieldStack, EntityPlayer player, Entity attacker, float damage) {
      if (attacker instanceof EntityLivingBase) {
         EntityLivingBase attackerLiving = (EntityLivingBase)attacker;
         if (attackerLiving.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            attackerLiving.attackEntityFrom(DamageSource.causePlayerDamage(player), 2.0F);
         }
      }

   }
}
