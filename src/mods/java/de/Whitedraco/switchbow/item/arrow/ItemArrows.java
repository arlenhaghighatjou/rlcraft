package de.Whitedraco.switchbow.item.arrow;

import de.Whitedraco.switchbow.Initial;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArrows extends ItemArrow {
   private Class<? extends Entity> entityClass;
   private boolean infinity = false;

   public ItemArrows(String UnlocalizedName, Class<? extends Entity> entityClass) {
      this.setRegistryName(UnlocalizedName);
      this.setCreativeTab(Initial.tabSwitchbow);
      this.setUnlocalizedName(UnlocalizedName);
      this.entityClass = entityClass;
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      try {
         Constructor ctor = this.entityClass.getDeclaredConstructor(World.class, EntityLivingBase.class);
         ctor.setAccessible(true);
         EntityArrow arrow = (EntityArrow)ctor.newInstance(worldIn, shooter);
         return arrow;
      } catch (NoSuchMethodException var6) {
         var6.printStackTrace();
      } catch (SecurityException var7) {
         var7.printStackTrace();
      } catch (InstantiationException var8) {
         var8.printStackTrace();
      } catch (IllegalAccessException var9) {
         var9.printStackTrace();
      } catch (IllegalArgumentException var10) {
         var10.printStackTrace();
      } catch (InvocationTargetException var11) {
         var11.printStackTrace();
      }

      return null;
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
