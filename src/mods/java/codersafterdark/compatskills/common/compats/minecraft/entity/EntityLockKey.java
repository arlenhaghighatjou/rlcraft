package codersafterdark.compatskills.common.compats.minecraft.entity;

import codersafterdark.reskillable.api.data.LockKey;

public abstract class EntityLockKey implements LockKey {
   private final String entityID;

   protected EntityLockKey(String entityID) {
      this.entityID = entityID == null ? "" : entityID;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof EntityLockKey && this.entityID.equals(((EntityLockKey)obj).entityID);
   }

   public int hashCode() {
      return this.entityID.hashCode();
   }
}
