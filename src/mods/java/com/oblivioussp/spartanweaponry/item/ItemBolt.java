package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltTipped;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBolt extends ItemSW {
   protected float baseDamage;
   protected float rangeMultiplier;
   protected float armorPiercingFactor;

   public ItemBolt(String unlocName, float baseDmg, float rngMultiplier, float armPrcFactor) {
      super(unlocName);
      this.baseDamage = baseDmg;
      this.rangeMultiplier = rngMultiplier;
      this.armorPiercingFactor = armPrcFactor;
      this.setCreativeTab(CreativeTabsSW.TAB_SW_ARROWS_BOLTS);
   }

   public EntityBolt createBolt(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      EntityBoltTipped bolt = new EntityBoltTipped(worldIn, shooter);
      bolt.setDamage((double)this.baseDamage);
      bolt.setPotionEffect(stack);
      return bolt;
   }

   public boolean isInfinite(ItemStack stack, ItemStack crossbow, EntityPlayer player) {
      int enchant = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, crossbow);
      return enchant <= 0 ? false : this.getClass() == ItemBolt.class;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("projectile.base_damage", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + this.baseDamage));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("projectile.range_multiplier", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + StringHelper.translateFormattedString("projectile.range_multiplier.value", "tooltip", "spartanweaponry", this.rangeMultiplier)));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("projectile.armor_piercing_factor", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + StringHelper.translateFormattedString("projectile.armor_piercing_factor.value", "tooltip", "spartanweaponry", this.armorPiercingFactor * 100.0F)));
      tooltip.add("");
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public float getRangeMultiplier() {
      return this.rangeMultiplier;
   }

   public float getArmorPiercingFactor() {
      return this.armorPiercingFactor;
   }
}
