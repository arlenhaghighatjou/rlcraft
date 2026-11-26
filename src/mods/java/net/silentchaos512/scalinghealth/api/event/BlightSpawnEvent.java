package net.silentchaos512.scalinghealth.api.event;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class BlightSpawnEvent extends LivingSpawnEvent {
   private BlightSpawnEvent(EntityLiving entity, World world, float x, float y, float z) {
      super(entity, world, x, y, z);
   }

   public static class Post extends BlightSpawnEvent {
      public Post(EntityLiving entity, World world, float x, float y, float z) {
         super(entity, world, x, y, z);
      }

      public boolean isCancelable() {
         return false;
      }
   }

   public static class Pre extends BlightSpawnEvent {
      public Pre(EntityLiving entity, World world, float x, float y, float z) {
         super(entity, world, x, y, z);
      }
   }
}
