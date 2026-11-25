package de.Whitedraco.switchbow.item.arrow;

import com.google.common.collect.Lists;
import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFirework;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArrowFirework extends ItemArrow {
   private boolean infinity;

   public ItemArrowFirework(String UnlocalizedName) {
      this.setRegistryName(UnlocalizedName);
      this.setCreativeTab(Initial.tabSwitchbow);
      this.setUnlocalizedName(UnlocalizedName);
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      EntityArrowFirework entityarrowlightningbolt = new EntityArrowFirework(worldIn, shooter, stack);
      entityarrowlightningbolt.setIsCritical(true);
      return entityarrowlightningbolt;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      if (stack.hasTagCompound()) {
         NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Fireworks");
         if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("Flight", 99)) {
               tooltip.add(I18n.translateToLocal("item.fireworks.flight") + " " + nbttagcompound.getByte("Flight"));
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("Explosions", 10);
            if (nbttaglist != null && !nbttaglist.hasNoTags()) {
               for(int i = 0; i < nbttaglist.tagCount(); ++i) {
                  NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                  List<String> list = Lists.newArrayList();
                  ItemFireworkCharge.addExplosionInfo(nbttagcompound1, list);
                  if (!list.isEmpty()) {
                     for(int j = 1; j < list.size(); ++j) {
                        list.set(j, "  " + (String)list.get(j));
                     }

                     tooltip.addAll(list);
                  }
               }
            }
         }
      }

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
