package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityCockatrice.class})
public abstract class EntityCockatriceRenderMixin extends Entity {
   public EntityCockatriceRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(1.5D, 0.5D, 1.5D);
   }
}
