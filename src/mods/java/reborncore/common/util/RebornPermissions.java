package reborncore.common.util;

import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class RebornPermissions {
   public static final String WRENCH_BLOCK = "block.wrench";
   public static final String TAP_BLOCK = "block.tap";

   public RebornPermissions() {
   }

   public static void init() {
      PermissionAPI.registerNode("block.wrench", DefaultPermissionLevel.ALL, "Node for wrenching blocks");
      PermissionAPI.registerNode("block.tap", DefaultPermissionLevel.ALL, "Node for taping blocks with the tree tap");
   }
}
