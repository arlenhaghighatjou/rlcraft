package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianArrow;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStymphalianArrow extends ItemArrow {
   public ItemStymphalianArrow() {
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName("iceandfire.stymphalian_arrow");
      this.setRegistryName("iceandfire", "stymphalian_arrow");
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
      return new EntityStymphalianArrow(worldIn, shooter);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(I18n.format("item.iceandfire.stymphalian_arrow.desc", new Object[0]));
   }
}
