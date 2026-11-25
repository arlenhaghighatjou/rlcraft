package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityDeathWorm.class})
public abstract class EntityDeathwormRenderMixin extends Entity {
   public EntityDeathwormRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow((this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX) * 7.0D, 0.0D, (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ) * 7.0D);
   }
}
