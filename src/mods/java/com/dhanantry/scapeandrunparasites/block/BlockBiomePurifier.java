package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBiomePurifier extends BlockBase {
   public BlockBiomePurifier(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
      super(Material.SPONGE, name, hardness, creative, tickRandom, resistance);
      this.setDefaultState(this.blockState.getBaseState());
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      boolean flag = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
      if (worldIn.isRemote) {
         return flag;
      } else {
         ItemStack head = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem());
         if (head.getItem() != Items.AIR) {
            return flag;
         } else {
            ParasiteEventWorld.killBiome(worldIn, pos, 16);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());

            for(int i = 0; i <= 3; ++i) {
               SRPMain.network.sendToAll(new SRPPacketParticle((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), 0.5F, 0.5F, (byte)2));
            }

            return flag;
         }
      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         if (worldIn.isAreaLoaded(pos, 3)) {
            int raan = 32;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow((double)raan);
            List<EntityParasiteBase> moblist = worldIn.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
            boolean oneTime = true;
            if (!moblist.isEmpty()) {
               for(EntityParasiteBase mob : moblist) {
                  if (oneTime && mob instanceof EntityPStationaryArchitect) {
                     mob.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200, 0, false, false));
                     oneTime = false;
                  }

                  mob.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 600, 1, false, false));
               }
            }

            int i1 = pos.getY();
            double l1 = (double)pos.getX();
            double i2 = (double)pos.getZ();
            boolean flag = false;
            raan = 5;
            int BGrange = raan;
            int BGheight = raan;

            for(int k2 = -1 * raan; k2 <= 1 * BGrange; ++k2) {
               for(int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                  for(int j = -1 * BGheight; j <= 1 * BGheight; ++j) {
                     double i3 = l1 + (double)k2;
                     double k = (double)(i1 + j);
                     double l = i2 + (double)l2;
                     BlockPos blockpos = new BlockPos(i3, k, l);
                     IBlockState iblockstate = worldIn.getBlockState(blockpos);
                     Block block = iblockstate.getBlock();
                     if (block == SRPBlocks.InfestedStain) {
                        worldIn.setBlockState(blockpos, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(5)));
                        worldIn.updateBlockTick(blockpos, SRPBlocks.InfestedStain.getDefaultState().withProperty(BlockInfestedStain.STAGE, Integer.valueOf(5)).getBlock(), 40, 5);
                     }

                     if (block == SRPBlocks.InfestedRubble) {
                        worldIn.setBlockState(blockpos, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(5)));
                        worldIn.updateBlockTick(blockpos, SRPBlocks.InfestedRubble.getDefaultState().withProperty(BlockInfestedRubble.STAGE, Integer.valueOf(5)).getBlock(), 40, 5);
                     }
                  }
               }
            }

         }
      }
   }
}
