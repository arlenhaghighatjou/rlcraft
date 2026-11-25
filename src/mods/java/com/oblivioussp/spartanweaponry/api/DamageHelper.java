package com.oblivioussp.spartanweaponry.api;

/** @deprecated */
@Deprecated
public class DamageHelper {
   public DamageHelper() {
   }

   /** @deprecated */
   @Deprecated
   public static float getDamage(DamageHelper.WeaponType type, float baseDamage) {
      switch(type) {
      case DAGGER:
         return 2.5F + baseDamage * 0.5F;
      case LONGSWORD:
         return 4.5F + baseDamage * 1.5F;
      case KATANA:
         return 3.5F + baseDamage * 0.5F;
      case SABER:
         return 4.5F + baseDamage * 0.5F;
      case RAPIER:
         return 2.5F + baseDamage * 0.5F;
      case GREATSWORD:
         return 6.0F + baseDamage * 2.0F;
      case HAMMER:
         return 6.0F + baseDamage;
      case WARHAMMER:
         return 4.0F + baseDamage;
      case SPEAR:
         return 4.5F + baseDamage * 0.5F;
      case HALBERD:
         return 6.5F + baseDamage * 2.5F;
      case PIKE:
         return 4.0F + baseDamage * 0.5F;
      case LANCE:
         return 4.0F + baseDamage;
      case THROWING_KNIFE:
         return 1.5F + baseDamage * 0.5F;
      case THROWING_AXE:
         return 2.0F + baseDamage;
      case JAVELIN:
         return 1.5F + baseDamage * 0.5F;
      case BOOMERANG:
         return 4.0F + baseDamage;
      case BATTLEAXE:
         return 5.0F + baseDamage * 2.0F;
      case MACE:
         return 4.0F + baseDamage;
      case GLAIVE:
         return 4.0F + baseDamage;
      case QUARTERSTAFF:
         return 1.5F + baseDamage;
      case PARRYING_DAGGER:
         return 2.0F + baseDamage * 0.5F;
      default:
         return 1.0F;
      }
   }

   /** @deprecated */
   @Deprecated
   public static enum WeaponType {
      DAGGER,
      LONGSWORD,
      KATANA,
      SABER,
      RAPIER,
      GREATSWORD,
      HAMMER,
      WARHAMMER,
      SPEAR,
      HALBERD,
      PIKE,
      LANCE,
      THROWING_KNIFE,
      THROWING_AXE,
      JAVELIN,
      BOOMERANG,
      BATTLEAXE,
      MACE,
      GLAIVE,
      QUARTERSTAFF,
      PARRYING_DAGGER;

      private WeaponType() {
      }
   }
}
