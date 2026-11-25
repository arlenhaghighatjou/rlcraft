package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RespirationUtil {
   public RespirationUtil() {
   }

   @SideOnly(Side.CLIENT)
   public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
      if (state.getMaterial() == Material.WATER) {
         float f12 = 0.0F;
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase ent = (EntityLivingBase)entity;
            f12 = (float)EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.AdvancedRespiration, ent) * 0.4F;
         }

         return new Vec3d((double)(0.02F + f12), (double)(0.02F + f12), (double)(0.2F + f12));
      } else {
         return state.getMaterial() == Material.LAVA ? new Vec3d(0.6000000238418579D, 0.10000000149011612D, 0.0D) : originalColor;
      }
   }
}
