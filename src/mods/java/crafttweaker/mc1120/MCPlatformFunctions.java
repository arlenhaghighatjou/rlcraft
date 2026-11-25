package crafttweaker.mc1120;

import crafttweaker.IPlatformFunctions;
import crafttweaker.api.chat.IChatMessage;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.mc1120.chat.MCChatMessage;
import crafttweaker.mc1120.item.MCItemDefinition;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class MCPlatformFunctions implements IPlatformFunctions {
   public static final MCPlatformFunctions INSTANCE = new MCPlatformFunctions();

   private MCPlatformFunctions() {
   }

   public IChatMessage getMessage(String message) {
      return new MCChatMessage(message);
   }

   public IItemDefinition getItemDefinition(int id) {
      Item item = Item.getItemById(id);
      ResourceLocation res = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
      String sid = res.getResourceDomain() + ":" + res.getResourcePath();
      return new MCItemDefinition(sid, item);
   }
}
