package c4.comforts.common.entities;

import c4.comforts.common.tileentities.TileEntityHammock;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityRest extends Entity {
   public EntityRest(World world) {
      super(world);
      this.noClip = true;
      this.width = 1.0E-4F;
      this.height = 0.0625F;
   }

   public EntityRest(World world, BlockPos pos) {
      this(world);
      this.setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.1825D;
   }

   public void onUpdate() {
      if (this.world.getTileEntity(this.getPosition()) == null || !(this.world.getTileEntity(this.getPosition()) instanceof TileEntityHammock)) {
         this.setDead();
      }

      super.onUpdate();
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
   }
}
