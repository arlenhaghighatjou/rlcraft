package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArrowSW extends ItemArrow {
   protected float baseDamage;
   protected float rangeMultiplier;

   public ItemArrowSW(String unlocName, float baseDmg, float rngMultiplier) {
      this.setCreativeTab(CreativeTabsSW.TAB_SW_ARROWS_BOLTS);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
      this.baseDamage = baseDmg;
      this.rangeMultiplier = rngMultiplier;
   }

   public String getUnlocalizedName() {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter);
      entitytippedarrow.setPotionEffect(stack);
      return entitytippedarrow;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("projectile.base_damage", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + this.baseDamage));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("projectile.range_multiplier", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateFormattedString("modifiers.longbow.speed_multiplier.value", "tooltip", "spartanweaponry", this.rangeMultiplier)));
      tooltip.add("");
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public float getRangeMultiplier() {
      return this.rangeMultiplier;
   }
}
