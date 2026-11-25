package paulevs.betternether.structures.city;

import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BuildingInfo {
   public StructureCityBuilding building;
   public BlockPos pos;

   public BuildingInfo(StructureCityBuilding building, BlockPos pos) {
      this.building = building;
      this.pos = pos;
   }

   public BuildingInfo(NBTTagCompound root, List<StructureCityBuilding> buildings) {
      int[] prePos = root.getIntArray("pos");
      int preRot = root.getInteger("rotation");
      String name = root.getString("name");
      this.pos = new BlockPos(prePos[0], prePos[1], prePos[2]);
      Rotation rot = Rotation.values()[preRot];

      for(StructureCityBuilding b : buildings) {
         if (b.getName() == name && b.getRotation() == rot) {
            this.building = b;
            return;
         }
      }

      if (this.building == null) {
         this.building = (new StructureCityBuilding(name)).getRotated(rot);
      }

   }

   public NBTTagCompound toNBT() {
      NBTTagCompound root = new NBTTagCompound();
      root.setIntArray("pos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
      root.setString("name", this.building.getName());
      root.setInteger("rotation", this.building.getRotation().ordinal());
      return root;
   }
}
