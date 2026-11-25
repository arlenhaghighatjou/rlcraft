package vazkii.quark.misc.feature;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent.Play;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class NoteBlocksMobSounds extends Feature {
   public static final EnumFacing[] SKULL_SEARCH_FACINGS = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};

   public NoteBlocksMobSounds() {
   }

   @SubscribeEvent
   public void noteBlockPlayed(Play event) {
      BlockPos pos = event.getPos();
      if (event.getWorld().getBlockState(pos).getBlock() == Blocks.NOTEBLOCK) {
         int type = getSkullType(event.getWorld(), pos);
         if (type != -1 && type != 3) {
            event.setCanceled(true);
            SoundEvent sound = null;
            switch(type) {
            case 0:
               sound = SoundEvents.ENTITY_SKELETON_AMBIENT;
               break;
            case 1:
               sound = SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
               break;
            case 2:
               sound = SoundEvents.ENTITY_ZOMBIE_AMBIENT;
            case 3:
            default:
               break;
            case 4:
               sound = SoundEvents.ENTITY_CREEPER_PRIMED;
               break;
            case 5:
               sound = SoundEvents.ENTITY_ENDERDRAGON_AMBIENT;
            }

            if (sound != null) {
               float pitch = (float)Math.pow(2.0D, (double)(event.getVanillaNoteId() - 12) / 12.0D);
               event.getWorld().playSound((EntityPlayer)null, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, sound, SoundCategory.BLOCKS, 1.0F, pitch);
            }
         }

      }
   }

   public static int getSkullType(World world, BlockPos pos) {
      TileEntity tile = null;
      boolean can = false;

      for(EnumFacing face : SKULL_SEARCH_FACINGS) {
         BlockPos offsetPos = pos.offset(face);
         tile = world.getTileEntity(offsetPos);
         if (tile instanceof TileEntitySkull) {
            IBlockState state = world.getBlockState(offsetPos);
            if (state.getValue(BlockSkull.FACING) == face) {
               can = true;
               break;
            }
         }
      }

      return can ? ((TileEntitySkull)tile).getSkullType() : -1;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Note Block Mob Sounds";
   }
}
