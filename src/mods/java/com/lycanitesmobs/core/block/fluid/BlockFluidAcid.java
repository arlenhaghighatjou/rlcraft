package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFluidBase;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidAcid extends BlockFluidBase {
   public BlockFluidAcid(Fluid fluid, String name) {
      super(fluid, Material.WATER, LycanitesMobs.modInfo, name);
      this.setLightOpacity(0);
      this.setLightLevel(0.25F);
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public boolean canDisplace(IBlockAccess world, BlockPos pos) {
      IBlockState blockState = world.getBlockState(pos);
      if (blockState.getBlock() == this) {
         return false;
      } else if (blockState.getMaterial() == Material.WATER) {
         if (world instanceof World) {
            ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE));
         }

         return false;
      } else if (blockState.getMaterial() == Material.LAVA) {
         if (world instanceof World) {
            ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.GRANITE));
         }

         return false;
      } else {
         return blockState.getMaterial().isLiquid() ? false : super.canDisplace(world, pos);
      }
   }

   public boolean displaceIfPossible(World world, BlockPos pos) {
      return world.getBlockState(pos).getMaterial().isLiquid() ? this.canDisplace(world, pos) : super.displaceIfPossible(world, pos);
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
            entity.attackEntityFrom(ObjectManager.getDamageSource("acid"), 1.0F);
         }

         Potion effect = ObjectManager.getEffect("penetration");
         if (effect != null && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(effect, 200, 0));
         }
      }

      super.onEntityCollidedWithBlock(world, pos, state, entity);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState blockState, World world, BlockPos pos, EaglercraftRandom random) {
      if (random.nextInt(100) == 0) {
         float f = (float)pos.getX() + random.nextFloat();
         float f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
         float f2 = (float)pos.getZ() + random.nextFloat();
         world.spawnParticle(EnumParticleTypes.CLOUD, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      super.randomDisplayTick(blockState, world, pos, random);
   }
}
