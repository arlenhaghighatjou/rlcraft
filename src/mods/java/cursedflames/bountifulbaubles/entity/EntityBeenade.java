package cursedflames.bountifulbaubles.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityBeenade extends EntityTerrariaThrowable {
   public EntityBeenade(World worldIn) {
      super(worldIn);
   }

   public EntityBeenade(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn, 0.4F);
   }

   public EntityBeenade(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   protected void onDeath() {
      for(int i = 0; i < 16; ++i) {
         EntityBee bee = this.thrower != null ? new EntityBee(this.world, this.thrower, this.posX, this.posY, this.posZ) : new EntityBee(this.world, this.posX, this.posY, this.posZ);
         bee.beeShoot(i, 0.3D);
         this.world.spawnEntity(bee);
      }

   }
}
