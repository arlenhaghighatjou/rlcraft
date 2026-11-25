package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.StructureSaveHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSaveStructure implements IMessage {
   private GenericStructure structureInfo;
   private String structureID;
   private SaveDirectoryData.Result saveDirectoryDataResult;

   public PacketSaveStructure() {
   }

   public PacketSaveStructure(GenericStructure structureInfo, String structureID, SaveDirectoryData.Result saveDirectoryDataResult) {
      this.structureInfo = structureInfo;
      this.structureID = structureID;
      this.saveDirectoryDataResult = saveDirectoryDataResult;
   }

   public String getStructureID() {
      return this.structureID;
   }

   public void setStructureID(String structureID) {
      this.structureID = structureID;
   }

   public GenericStructure getStructureInfo() {
      return this.structureInfo;
   }

   public void setStructureInfo(GenericStructure structureInfo) {
      this.structureInfo = structureInfo;
   }

   public SaveDirectoryData.Result getSaveDirectoryDataResult() {
      return this.saveDirectoryDataResult;
   }

   public void setSaveDirectoryDataResult(SaveDirectoryData.Result saveDirectoryDataResult) {
      this.saveDirectoryDataResult = saveDirectoryDataResult;
   }

   public void fromBytes(ByteBuf buf) {
      this.structureID = ByteBufUtils.readUTF8String(buf);
      this.structureInfo = StructureSaveHandler.INSTANCE.fromJSON(ByteBufUtils.readUTF8String(buf), (NBTTagCompound)null);
      this.saveDirectoryDataResult = SaveDirectoryData.Result.readFrom(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.structureID);
      ByteBufUtils.writeUTF8String(buf, StructureSaveHandler.INSTANCE.toJSON(this.structureInfo));
      this.saveDirectoryDataResult.writeTo(buf);
   }
}
