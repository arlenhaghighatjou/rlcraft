package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.blocks.te.TEMovingLightSource;
import net.minecraft.block.BlockAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMovingLightSource extends BlockAir implements ITileEntityProvider {
   public static final String NAME = "moving_light_source";
   private EntityPlayer player;

   public BlockMovingLightSource() {
      this.setUnlocalizedName("realistictorches.moving_light_source");
      this.setLightLevel(0.9F);
   }

   public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
      return false;
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return (new TEMovingLightSource()).setPlayer(this.player);
   }

   public BlockMovingLightSource setPlayer(EntityPlayer player) {
      this.player = player;
      return this;
   }
}
