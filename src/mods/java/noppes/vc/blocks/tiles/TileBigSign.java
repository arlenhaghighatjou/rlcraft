package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.TextBlock;

public class TileBigSign extends TileEntity {
   public int rotation;
   public boolean canEdit = true;
   public boolean hasChanged = true;
   private String signText = "";
   public TextBlock block;

   public TileBigSign() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.rotation = compound.getInteger("SignRotation");
      this.setText(compound.getString("SignText"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("SignRotation", this.rotation);
      compound.setString("SignText", this.signText);
      return super.writeToNBT(compound);
   }

   public void setText(String text) {
      this.signText = text;
      this.hasChanged = true;
   }

   public String getText() {
      return this.signText;
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound compound = pkt.getNbtCompound();
      this.readFromNBT(compound);
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
}
