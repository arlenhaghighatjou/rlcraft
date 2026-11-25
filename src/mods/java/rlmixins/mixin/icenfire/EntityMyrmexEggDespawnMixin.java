package rlmixins.mixin.icenfire;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityMyrmexEgg.class})
public abstract class EntityMyrmexEggDespawnMixin extends EntityLiving {
   public EntityMyrmexEggDespawnMixin(World worldIn) {
      super(worldIn);
   }

   public boolean canDespawn() {
      return false;
   }

   public boolean isNoDespawnRequired() {
      return true;
   }
}
