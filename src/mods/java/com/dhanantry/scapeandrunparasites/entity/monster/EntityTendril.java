package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.World;

public class EntityTendril extends EntityParasiteBase {
   public EntityTendril(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
      this.tasks.removeTask(this.aiWander);
      this.tasks.removeTask(this.folow);
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 202;
   }

   public EntityTendril(World world, float width, float height) {
      super(world);
      this.setSize(width, height);
   }

   public void onUpdate() {
      super.onUpdate();
   }

   public void setSkin(int texture) {
      super.setSkin(texture);
   }

   public int getSkin() {
      return super.getSkin();
   }
}
