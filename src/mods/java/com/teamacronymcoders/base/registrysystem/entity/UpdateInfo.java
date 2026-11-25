package com.teamacronymcoders.base.registrysystem.entity;

public class UpdateInfo {
   private int trackingRange = 64;
   private int updateFrequency = 1;
   private boolean sendVelocityUpdates = true;

   public UpdateInfo() {
   }

   public int getTrackingRange() {
      return this.trackingRange;
   }

   public void setTrackingRange(int trackingRange) {
      this.trackingRange = trackingRange;
   }

   public int getUpdateFrequency() {
      return this.updateFrequency;
   }

   public void setUpdateFrequency(int updateFrequency) {
      this.updateFrequency = updateFrequency;
   }

   public boolean isSendVelocityUpdates() {
      return this.sendVelocityUpdates;
   }

   public void setSendVelocityUpdates(boolean sendVelocityUpdates) {
      this.sendVelocityUpdates = sendVelocityUpdates;
   }
}
