package com.creativemd.creativecore.common.utils.type;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;

public class UUIDSupplier {
   private final EaglercraftUUID original;
   public EaglercraftUUID uuid;

   public UUIDSupplier() {
      this(EaglercraftUUID.randomUUID());
   }

   public UUIDSupplier(EaglercraftUUID uuid) {
      this.original = uuid;
      this.uuid = uuid;
   }

   public UUIDSupplier(EaglercraftUUID original, EaglercraftUUID uuid) {
      this.original = original;
      this.uuid = uuid;
   }

   public EaglercraftUUID original() {
      return this.original;
   }

   public EaglercraftUUID next() {
      this.uuid = new EaglercraftUUID(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits() + 1L);
      return this.uuid;
   }
}
