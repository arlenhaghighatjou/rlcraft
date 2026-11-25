package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class PlacedStructure implements NBTCompoundObject {
   public String structureID;
   public String generationInfoID;
   public AxisAlignedTransform2D transform;
   public BlockPos lowerCoord;
   public NBTBase instanceData;

   public PlacedStructure() {
   }

   public PlacedStructure(String structureID, String generationInfoID, AxisAlignedTransform2D transform, BlockPos lowerCoord, NBTBase instanceData) {
      this.structureID = structureID;
      this.generationInfoID = generationInfoID;
      this.transform = transform;
      this.lowerCoord = lowerCoord;
      this.instanceData = instanceData;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.structureID = compound.getString("structureID");
      this.generationInfoID = compound.hasKey(this.generationInfoID, 8) ? compound.getString("generationInfoID") : null;
      this.transform = RCAxisAlignedTransform.read(compound, "rotation", "mirrorX");
      this.lowerCoord = BlockPositions.readFromNBT("lowerCoord", compound);
      this.instanceData = compound.hasKey("instanceData", 10) ? compound.getTag("instanceData") : null;
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setString("structureID", this.structureID);
      if (this.generationInfoID != null) {
         compound.setString("generationInfoID", this.generationInfoID);
      }

      RCAxisAlignedTransform.write(compound, this.transform, "rotation", "mirrorX");
      BlockPositions.writeToNBT("lowerCoord", this.lowerCoord, compound);
      if (this.instanceData != null) {
         compound.setTag("instanceData", this.instanceData);
      }

   }
}
