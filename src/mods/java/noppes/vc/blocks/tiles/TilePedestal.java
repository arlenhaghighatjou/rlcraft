package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TilePedestal extends TileBasicContainer {
   public TilePedestal() {
   }

   public String getName() {
      return "tile.pedestal.name";
   }

   public int getSizeInventory() {
      return 1;
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 1));
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeToNBT(compound);
      compound.removeTag("ExtraData");
      return compound;
   }

   public int powerProvided() {
      return this.getStackInSlot(0).isEmpty() ? 0 : 15;
   }

   public static class TilePedestal1 extends TilePedestal {
      public TilePedestal1() {
      }
   }

   public static class TilePedestal2 extends TilePedestal {
      public TilePedestal2() {
      }
   }

   public static class TilePedestal3 extends TilePedestal {
      public TilePedestal3() {
      }
   }

   public static class TilePedestal4 extends TilePedestal {
      public TilePedestal4() {
      }
   }
}
