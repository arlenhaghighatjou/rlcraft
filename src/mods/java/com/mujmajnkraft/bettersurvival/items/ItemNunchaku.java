package com.mujmajnkraft.bettersurvival.items;

import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider;
import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNunchaku extends ItemCustomWeapon {
   public ItemNunchaku(ToolMaterial material) {
      super(material, ForgeConfigHandler.weapons.nunchakuDmgMod, ForgeConfigHandler.weapons.nunchakuSpd);
      this.setRegistryName("mujmajnkraftsbettersurvival", "item" + material.name().toLowerCase() + "nunchaku");
      this.setUnlocalizedName(material.name().toLowerCase() + "nunchaku");
      this.addPropertyOverride(new ResourceLocation("spinning"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn != null && entityIn.getHeldItemMainhand() == stack) {
               INunchakuCombo cap = (INunchakuCombo)entityIn.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
               if (cap != null && cap.isSpinning()) {
                  return 1.0F;
               }
            }

            return 0.0F;
         }
      });
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      int i = EnchantmentHelper.getKnockbackModifier(attacker) + 1;
      target.knockBack(attacker, -((float)i) * 0.1F, (double)MathHelper.sin(attacker.rotationYaw * (float)i * 0.017453292F), (double)(-MathHelper.cos(attacker.rotationYaw * (float)i * 0.017453292F)));
      if (attacker instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)attacker;
         INunchakuCombo combo = (INunchakuCombo)player.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
         if (combo != null) {
            int l = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.combo, stack);
            combo.setComboPower(combo.getComboPower() + 0.1F + (float)l / 20.0F);
         }
      }

      return super.hitEntity(stack, target, attacker);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String s = I18n.format("mujmajnkraftsbettersurvival.nunchaku.desc", new Object[0]);
      tooltip.add(TextFormatting.AQUA + s);
   }

   public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
      return true;
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      INunchakuCombo cap = (INunchakuCombo)player.getCapability(NunchakuComboProvider.NUNCHAKUCOMBO_CAP, (EnumFacing)null);
      if (cap != null && !cap.isSpinning()) {
         player.isSwingInProgress = false;
         return true;
      } else {
         return false;
      }
   }
}
