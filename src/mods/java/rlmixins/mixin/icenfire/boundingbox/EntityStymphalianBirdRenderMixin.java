package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityStymphalianBird.class})
public abstract class EntityStymphalianBirdRenderMixin extends Entity {
   public EntityStymphalianBirdRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(0.5D, 0.5D, 0.5D);
   }
}
