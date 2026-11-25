package org.jwaresoftware.mcmods.armorunder;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.api.mod.IModInfo;

public final class ModInfo implements IModInfo {
   public static final ModIntegration ID = ModIntegration.ARMOR_UNDERWEAR;
   public static final String MOD_NAME = "Armor Underwear";
   public static final String MOD_ID = "armorunder";
   public static final String MOD_ITEMID_PREFIX = "auw_";
   public static final String MOD_RESOURCES_LOC_ROOT = "armorunder:";
   public static final String MOD_VERSION = "1.0.0";
   public static final String MOD_VERSION_CHECK_URL = "http://jwaresoftware.org/files/mcmods/versions/armorunder.json";
   public static final String MOD_CONFIG_VERSION = "1";
   public static final String MOD_SP_RUNTIME_CLASSID = "org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeSP";
   public static final String MOD_DS_RUNTIME_CLASSID = "org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeDS";
   private static final ModInfo _sharedINSTANCE = new ModInfo();

   public String mod_id() {
      return "armorunder";
   }

   public String itemid_prefix() {
      return "auw_";
   }

   public String config_version() {
      return "1";
   }

   public static final ModInfo getInstance() {
      return _sharedINSTANCE;
   }

   public static final ResourceLocation r(String s) {
      return new ResourceLocation("armorunder", s);
   }

   public static final String rstring(String s) {
      return "armorunder:" + s;
   }

   private ModInfo() {
   }

   static {
      Validate.isTrue(ID.modid().equals("armorunder"));
   }
}
