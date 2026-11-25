package ivorius.reccomplex.network;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.StructureSaveHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEditStructure implements IMessage {
   protected GenericStructure structureInfo;
   protected String structureID;
   protected BlockPos lowerCoord;
   protected SaveDirectoryData saveDirectoryData;

   public PacketEditStructure() {
   }

   public PacketEditStructure(GenericStructure structureInfo, String structureID, BlockPos lowerCoord, SaveDirectoryData saveDirectoryData) {
      this.structureInfo = structureInfo;
      this.structureID = structureID;
      this.lowerCoord = lowerCoord;
      this.saveDirectoryData = saveDirectoryData;
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

   public SaveDirectoryData getSaveDirectoryData() {
      return this.saveDirectoryData;
   }

   public void setSaveDirectoryData(SaveDirectoryData saveDirectoryData) {
      this.saveDirectoryData = saveDirectoryData;
   }

   public BlockPos getLowerCoord() {
      return this.lowerCoord;
   }

   public void setLowerCoord(BlockPos lowerCoord) {
      this.lowerCoord = lowerCoord;
   }

   public void fromBytes(ByteBuf buf) {
      this.structureID = ByteBufUtils.readUTF8String(buf);
      this.structureInfo = StructureSaveHandler.INSTANCE.fromJSON(ByteBufUtils.readUTF8String(buf), (NBTTagCompound)null);
      this.lowerCoord = BlockPositions.readFromBuffer(buf);
      this.saveDirectoryData = SaveDirectoryData.readFrom(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.structureID);
      ByteBufUtils.writeUTF8String(buf, StructureSaveHandler.INSTANCE.toJSON(this.structureInfo));
      BlockPositions.writeToBuffer(this.lowerCoord, buf);
      this.saveDirectoryData.writeTo(buf);
   }
}
