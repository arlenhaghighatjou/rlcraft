package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEnlightenedBush extends TileEntity {
   public TileEnlightenedBush() {
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = this.getTileData().copy();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      NBTBase texture = tag.getTag("texture");
      if (texture != null) {
         this.getTileData().setTag("texture", texture);
      }

      this.readFromNBT(tag);
   }
}
