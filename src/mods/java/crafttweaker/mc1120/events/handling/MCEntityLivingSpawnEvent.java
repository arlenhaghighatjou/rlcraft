package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingSpawnEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.tileentity.IMobSpawnerBaseLogic;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.tileentity.MCMobSpawnerBaseLogic;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCEntityLivingSpawnEvent implements EntityLivingSpawnEvent {
   protected final LivingSpawnEvent event;

   public MCEntityLivingSpawnEvent(LivingSpawnEvent event) {
      this.event = event;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public float getX() {
      return this.event.getX();
   }

   public float getY() {
      return this.event.getY();
   }

   public float getZ() {
      return this.event.getZ();
   }

   public void allow() {
      this.event.setResult(Result.ALLOW);
   }

   public void deny() {
      this.event.setResult(Result.DENY);
   }

   public void pass() {
      this.event.setResult(Result.DEFAULT);
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }

   public static class MCEntityLivingExtendedSpawnEvent extends MCEntityLivingSpawnEvent implements EntityLivingSpawnEvent.EntityLivingExtendedSpawnEvent {
      private final IMobSpawnerBaseLogic spawnerBaseLogic;

      public MCEntityLivingExtendedSpawnEvent(CheckSpawn event) {
         super(event);
         this.spawnerBaseLogic = event.getSpawner() == null ? null : new MCMobSpawnerBaseLogic(event.getSpawner());
      }

      public MCEntityLivingExtendedSpawnEvent(SpecialSpawn event) {
         super(event);
         this.spawnerBaseLogic = event.getSpawner() == null ? null : new MCMobSpawnerBaseLogic(event.getSpawner());
      }

      public IMobSpawnerBaseLogic getSpawner() {
         return this.spawnerBaseLogic;
      }
   }

   public static class MCEntityLivingSpecialSpawnEvent extends MCEntityLivingSpawnEvent.MCEntityLivingExtendedSpawnEvent {
      public MCEntityLivingSpecialSpawnEvent(SpecialSpawn event) {
         super(event);
      }

      public void allow() {
         this.event.setCanceled(false);
      }

      public void deny() {
         this.event.setCanceled(true);
      }

      public void pass() {
      }
   }
}
