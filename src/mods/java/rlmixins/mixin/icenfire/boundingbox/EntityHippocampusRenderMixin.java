package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityHippocampus.class})
public abstract class EntityHippocampusRenderMixin extends Entity {
   public EntityHippocampusRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(2.0D, 0.5D, 2.0D);
   }
}
