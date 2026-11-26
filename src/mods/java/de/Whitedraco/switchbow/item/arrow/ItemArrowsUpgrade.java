package de.Whitedraco.switchbow.item.arrow;

import java.util.function.BiFunction;
import de.Whitedraco.switchbow.Initial;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArrowsUpgrade extends ItemArrow {
   private BiFunction<World, EntityLivingBase, ? extends EntityArrow> factory;
   private boolean infinity;

   public ItemArrowsUpgrade(String UnlocalizedName, BiFunction<World, EntityLivingBase, ? extends EntityArrow> factory) {
      this.setRegistryName(UnlocalizedName);
      this.setCreativeTab(Initial.tabSwitchbow);
      this.setUnlocalizedName(UnlocalizedName);
      this.factory = factory;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      return this.factory.apply(worldIn, shooter);
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
      int enchant = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow);
      return enchant <= 0 ? false : this.infinity;
   }

   public Item setInfinity(boolean infinity) {
      this.infinity = infinity;
      return this;
   }
}
