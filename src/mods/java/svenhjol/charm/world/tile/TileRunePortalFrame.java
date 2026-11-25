package svenhjol.charm.world.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import svenhjol.charm.world.block.BlockRunePortalFrame;
import svenhjol.charm.world.feature.EndPortalRunes;
import svenhjol.meson.MesonTile;

public class TileRunePortalFrame extends MesonTile {
   public int facing;

   public TileRunePortalFrame() {
   }

   public String getModId() {
      return "charm";
   }

   public void readTag(NBTTagCompound tag) {
      this.facing = tag.getInteger("facing");
      super.readTag(tag);
   }

   public void writeTag(NBTTagCompound tag) {
      tag.setInteger("facing", this.facing);
      super.writeTag(tag);
   }

   public void setFacing(EnumFacing facing) {
      this.facing = facing.getIndex();
      this.markDirty();
   }

   public EnumFacing getFacing() {
      EnumFacing facing = EnumFacing.getFront(this.facing);
      if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
         facing = EnumFacing.WEST;
      }

      return facing;
   }

   public void onLoad() {
      if (this.world.getBlockState(this.pos).getBlock() == EndPortalRunes.frame) {
         IBlockState state = this.world.getBlockState(this.pos).withProperty(BlockRunePortalFrame.FACING, this.getFacing());
         this.world.setBlockState(this.pos, state, 3);
      }

   }
}
