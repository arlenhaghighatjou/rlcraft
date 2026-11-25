package prospector.traverse.core;

import prospector.traverse.shadow.shootingstar.version.Version;

public class TraverseConstants {
   public static final String MOD_NAME = "Traverse";
   public static final String MOD_ID = "traverse";
   public static final String MOD_VERSION_MAJOR = "1";
   public static final String MOD_VERSION_MINOR = "6";
   public static final String MOD_VERSION_PATCH = "0";
   public static final Version DEV_VERSION = new Version(999, 0, 0);
   public static final String MINECRAFT_VERSIONS = "1.12.2";
   public static final String CLIENT_PROXY_CLASS = "prospector.traverse.TraverseClient";
   public static final String SERVER_PROXY_CLASS = "prospector.traverse.TraverseServer";

   public TraverseConstants() {
   }
}
