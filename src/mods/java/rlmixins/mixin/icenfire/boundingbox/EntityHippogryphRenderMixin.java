package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityHippogryph.class})
public abstract class EntityHippogryphRenderMixin extends Entity {
   public EntityHippogryphRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(2.0D, 1.0D, 2.0D).offset(0.0D, 0.5D, 1.0D);
   }
}
