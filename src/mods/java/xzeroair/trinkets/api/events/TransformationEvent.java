package xzeroair.trinkets.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.races.EntityRace;

public abstract class TransformationEvent extends Event {
   protected EntityLivingBase entity;
   protected EntityRace currentRace;
   protected EntityProperties properties;

   public TransformationEvent(EntityLivingBase entity, EntityProperties properties, EntityRace current) {
      this.entity = entity;
      this.currentRace = current;
      this.properties = properties;
   }

   public EntityProperties getEntityProperties() {
      return this.properties;
   }

   public EntityLivingBase getEntityLiving() {
      return this.entity;
   }

   public EntityRace getCurrentRace() {
      return this.currentRace;
   }

   public static class EndTransformation extends TransformationEvent {
      protected EntityRace prevRace;

      public EndTransformation(EntityLivingBase entity, EntityProperties properties, EntityRace current) {
         super(entity, properties, current);
         this.setPreviousRace(this.getCurrentRace());
      }

      public EntityRace getPreviousRace() {
         return this.prevRace;
      }

      private void setPreviousRace(EntityRace race) {
         this.prevRace = race;
      }
   }

   @Cancelable
   public static class RaceUpdateEvent extends TransformationEvent {
      protected boolean changed = false;
      protected EntityRace newRace;

      public RaceUpdateEvent(EntityLivingBase entity, EntityProperties properties, EntityRace current, EntityRace next) {
         super(entity, properties, current);
         this.setChanged(!this.getCurrentRace().equals(next));
         this.setNewRace(next);
      }

      public EntityRace getNewRace() {
         return this.newRace;
      }

      public void setNewRace(EntityRace race) {
         this.newRace = race;
      }

      public boolean raceChanged() {
         return this.changed;
      }

      public void setChanged(boolean changed) {
         this.changed = changed;
      }
   }

   public static class StartTransformation extends TransformationEvent {
      public StartTransformation(EntityLivingBase entity, EntityProperties properties, EntityRace current) {
         super(entity, properties, current);
      }
   }
}
