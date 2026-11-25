package com.tmtravlr.potioncore.potion;

public class PotionTeleportSpawn extends PotionCorePotion {
   public static final String NAME = "teleport_spawn";
   public static final String TAG_NAME = "potion core - spawn teleport";
   public static final String TAG_X = "potion core - spawn teleport x";
   public static final String TAG_Y = "potion core - spawn teleport y";
   public static final String TAG_Z = "potion core - spawn teleport z";
   public static final PotionTeleportSpawn INSTANCE = new PotionTeleportSpawn();
   public static int teleportDelay = 10;

   public PotionTeleportSpawn() {
      super("teleport_spawn", false, 10049023);
   }

   public boolean isInstant() {
      return false;
   }
}
