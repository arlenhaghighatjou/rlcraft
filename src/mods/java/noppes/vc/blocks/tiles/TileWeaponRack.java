package noppes.vc.blocks.tiles;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileWeaponRack extends TileBasicContainer {
   public TileWeaponRack() {
   }

   public boolean isItemValidForSlot(int var1, ItemStack itemstack) {
      return itemstack != null && itemstack.getItem() instanceof ItemBlock ? false : super.isItemValidForSlot(var1, itemstack);
   }

   public int getSizeInventory() {
      return 3;
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 1));
   }

   public String getName() {
      return "tile.weapon_rack.name";
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
      int power = 0;

      for(int i = 0; i < 3; ++i) {
         if (!this.getStackInSlot(i).isEmpty()) {
            power += 5;
         }
      }

      return power;
   }

   public static class TileWeaponRack1 extends TileWeaponRack {
      public TileWeaponRack1() {
      }
   }

   public static class TileWeaponRack2 extends TileWeaponRack {
      public TileWeaponRack2() {
      }
   }

   public static class TileWeaponRack3 extends TileWeaponRack {
      public TileWeaponRack3() {
      }
   }

   public static class TileWeaponRack4 extends TileWeaponRack {
      public TileWeaponRack4() {
      }
   }

   public static class TileWeaponRack5 extends TileWeaponRack {
      public TileWeaponRack5() {
      }
   }
}
