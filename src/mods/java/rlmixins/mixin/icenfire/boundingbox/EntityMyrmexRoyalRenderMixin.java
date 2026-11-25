package rlmixins.mixin.icenfire.boundingbox;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityMyrmexRoyal.class})
public abstract class EntityMyrmexRoyalRenderMixin extends Entity {
   public EntityMyrmexRoyalRenderMixin(World worldIn) {
      super(worldIn);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(1.0D, 1.0D, 1.0D);
   }
}
