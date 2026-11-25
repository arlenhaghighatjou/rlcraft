package pl.asie.foamfix.shared;

import java.util.function.BooleanSupplier;

public class FoamFixShared {
   public static final String MOD_NAME_IDPATCH = "JustEnoughIDs/NotEnoughIDs";
   public static final String MOD_NAME_SPONGE = "SpongeForge";
   public static final FoamFixConfig config = new FoamFixConfig();
   public static boolean isCoremod = false;
   private static Boolean idPatchModPresent;
   private static Boolean spongePresent;
   public static int neDeflaterCompression;

   public FoamFixShared() {
   }

   public static boolean emitWarningIfPresent(String featureName, BooleanSupplier supplier, String modName, boolean disables) {
      if (supplier.getAsBoolean()) {
         if (disables) {
            System.err.println(featureName + " has been force-disabled - " + modName + " detected!");
         } else {
            System.err.println(featureName + " may not work correctly - " + modName + " detected!");
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean hasIdPatch() {
      if (idPatchModPresent == null) {
         try {
            idPatchModPresent = Class.forName("org.dimdev.jeid.JEIDLoadingPlugin") != null;
         } catch (ClassNotFoundException var3) {
            try {
               idPatchModPresent = Class.forName("ru.fewizz.neid.Neid") != null;
            } catch (ClassNotFoundException var2) {
               idPatchModPresent = false;
            }
         }
      }

      return false;
   }

   public static boolean hasSponge() {
      if (spongePresent == null) {
         try {
            spongePresent = Class.forName("org.spongepowered.mod.SpongeCoremod") != null;
         } catch (ClassNotFoundException var1) {
            spongePresent = false;
         }
      }

      return spongePresent.booleanValue();
   }

   public static boolean hasOptifine() {
      try {
         return Class.forName("optifine.OptiFineTweaker") != null;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }
}
