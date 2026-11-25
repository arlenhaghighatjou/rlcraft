package org.jwaresoftware.mcmods.lib.api.mod;

public interface IModInfo {
   String MOD_VERSION_CHECK_URL_PARENT = "http://jwaresoftware.org/files/mcmods/versions/";
   String MOD_CONFIG_MARKER = "wabbity_";

   String mod_id();

   default String itemid_prefix() {
      return "";
   }

   default String config_version() {
      return "1";
   }
}
