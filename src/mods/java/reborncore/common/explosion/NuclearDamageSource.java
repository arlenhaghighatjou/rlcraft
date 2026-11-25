package reborncore.common.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class NuclearDamageSource extends EntityDamageSource {
   public NuclearDamageSource(Entity entity) {
      super("nuke", entity);
   }
}
