package me.ichun.mods.ichunutil.common.core.tracker;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.EntityLivingBase;

public class EntityTrackerRegistry {
   public ArrayList<EntityTrackerRegistry.Entry> trackerEntries = new ArrayList();

   public EntityTrackerRegistry() {
   }

   public void tick() {
      for(int i = this.trackerEntries.size() - 1; i >= 0; --i) {
         EntityTrackerRegistry.Entry entry = (EntityTrackerRegistry.Entry)this.trackerEntries.get(i);
         if (!entry.update()) {
            this.trackerEntries.remove(i);
         }
      }

   }

   public EntityTrackerRegistry.Entry getOrCreateEntry(EntityLivingBase living, int maxTrack) {
      EntityTrackerRegistry.Entry entry = null;

      for(EntityTrackerRegistry.Entry ent1 : this.trackerEntries) {
         if (ent1.entityToTrack == living) {
            entry = ent1;
            break;
         }
      }

      if (entry == null) {
         entry = new EntityTrackerRegistry.Entry(living);
         this.trackerEntries.add(entry);
      }

      entry.setMaxTrack(maxTrack);
      return entry;
   }

   public class EntityInfo {
      public EntityLivingBase tracked;
      public double posX;
      public double posY;
      public double posZ;
      public float renderYawOffset;
      public float rotationYawHead;
      public float rotationPitch;
      public float limbSwing;
      public float limbSwingAmount;
      public boolean sneaking;
      public boolean sleeping;
      public boolean sprinting;
      public boolean invisible;
      public boolean elytraFlying;
      public float height;
      public long lastTick;
      public ArrayList<EntityTrackerRegistry.IAdditionalTrackerInfo> additionalInfo = new ArrayList();

      public EntityInfo(EntityLivingBase ent) {
         this.tracked = ent;
      }

      public EntityTrackerRegistry.EntityInfo addTracker(EntityTrackerRegistry.IAdditionalTrackerInfo trackerInfo) {
         this.additionalInfo.add(trackerInfo);
         return this;
      }

      public <T extends EntityTrackerRegistry.IAdditionalTrackerInfo> T getTracker(Class<T> clz) {
         for(EntityTrackerRegistry.IAdditionalTrackerInfo tracker : this.additionalInfo) {
            if (tracker.getClass().equals(clz)) {
               return (T)tracker;
            }
         }

         return (T)null;
      }

      public void update() {
         this.posX = this.tracked.posX;
         this.posY = this.tracked.posY;
         this.posZ = this.tracked.posZ;
         this.renderYawOffset = this.tracked.renderYawOffset;
         this.rotationYawHead = this.tracked.rotationYawHead;
         this.rotationPitch = this.tracked.getTicksElytraFlying() > 4 ? (float)Math.toDegrees(-0.7853981852531433D) : this.tracked.rotationPitch;
         this.limbSwing = this.tracked.limbSwing;
         this.limbSwingAmount = this.tracked.limbSwingAmount;
         this.sneaking = this.tracked.isSneaking();
         this.sleeping = this.tracked.isPlayerSleeping();
         this.sprinting = this.tracked.isSprinting();
         this.invisible = this.tracked.isInvisible();
         this.elytraFlying = this.tracked.getTicksElytraFlying() > 4;
         this.height = this.tracked.height;
         this.lastTick = (long)this.tracked.ticksExisted;

         for(EntityTrackerRegistry.IAdditionalTrackerInfo trackers : this.additionalInfo) {
            trackers.track(this);
         }

      }

      public boolean hasSameCoords(EntityTrackerRegistry.EntityInfo info) {
         return info.posX == this.posX && info.posY == this.posY && info.posZ == this.posZ;
      }
   }

   public class Entry {
      public final EntityLivingBase entityToTrack;
      public int age;
      public int maxTrack;
      public ArrayList<Class<? extends EntityTrackerRegistry.IAdditionalTrackerInfo>> additionalInfo;
      public ArrayList<EntityTrackerRegistry.EntityInfo> trackedInfo;

      public Entry(EntityLivingBase entityToTrack) {
         this.entityToTrack = entityToTrack;
         this.maxTrack = 20;
         this.additionalInfo = new ArrayList();
         this.trackedInfo = new ArrayList();
      }

      public EntityTrackerRegistry.Entry setMaxTrack(int max) {
         if (max > this.maxTrack) {
            this.maxTrack = max;
         }

         return this;
      }

      public EntityTrackerRegistry.Entry addAdditionalTrackerInfo(Class<? extends EntityTrackerRegistry.IAdditionalTrackerInfo> tracker) {
         for(Class<? extends EntityTrackerRegistry.IAdditionalTrackerInfo> trackers : this.additionalInfo) {
            if (tracker.equals(trackers)) {
               return this;
            }
         }

         this.additionalInfo.add(tracker);

         for(EntityTrackerRegistry.EntityInfo info : this.trackedInfo) {
            try {
               info.addTracker((EntityTrackerRegistry.IAdditionalTrackerInfo)tracker.getConstructor().newInstance());
            } catch (Exception var5) {
               iChunUtil.LOGGER.warn("Error creating tracker: " + tracker.getName());
               var5.printStackTrace();
            }
         }

         return this;
      }

      public boolean update() {
         ++this.age;
         if (this.entityToTrack.isDead) {
            return false;
         } else {
            EntityTrackerRegistry.EntityInfo info = EntityTrackerRegistry.this.new EntityInfo(this.entityToTrack);

            for(Class<? extends EntityTrackerRegistry.IAdditionalTrackerInfo> tracker : this.additionalInfo) {
               try {
                  info.addTracker((EntityTrackerRegistry.IAdditionalTrackerInfo)tracker.getConstructor().newInstance());
               } catch (Exception var5) {
                  iChunUtil.LOGGER.warn("Error creating trackers: " + tracker.getName());
                  var5.printStackTrace();
               }
            }

            info.update();
            this.trackedInfo.add(0, info);

            while(this.trackedInfo.size() > this.maxTrack) {
               this.trackedInfo.remove(this.trackedInfo.size() - 1);
            }

            return true;
         }
      }
   }

   public interface IAdditionalTrackerInfo {
      void track(EntityTrackerRegistry.EntityInfo var1);
   }
}
