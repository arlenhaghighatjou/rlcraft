package com.creativemd.creativecore.common.utils.type;

import java.util.UUID;

public class UUIDSupplier {
   private final UUID original;
   public UUID uuid;

   public UUIDSupplier() {
      this(UUID.randomUUID());
   }

   public UUIDSupplier(UUID uuid) {
      this.original = uuid;
      this.uuid = uuid;
   }

   public UUIDSupplier(UUID original, UUID uuid) {
      this.original = original;
      this.uuid = uuid;
   }

   public UUID original() {
      return this.original;
   }

   public UUID next() {
      this.uuid = new UUID(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits() + 1L);
      return this.uuid;
   }
}
