package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityTroll;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityTroll.class})
public abstract class EntityTrollRenderMixin extends Entity {
   public EntityTrollRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D);
   }
}
