package bl4ckscor3.mod.sit;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySit extends Entity {
   public EntitySit(World world) {
      super(world);
      this.noClip = true;
      this.height = 1.0E-4F;
      this.width = 1.0E-4F;
   }

   public EntitySit(World world, BlockPos pos) {
      super(world);
      this.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 0.25D, (double)pos.getZ() + 0.5D);
      this.noClip = true;
      this.height = 1.0E-4F;
      this.width = 1.0E-4F;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound tag) {
   }

   protected void writeEntityToNBT(NBTTagCompound tag) {
   }
}
