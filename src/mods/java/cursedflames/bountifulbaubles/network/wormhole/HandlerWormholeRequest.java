package cursedflames.bountifulbaubles.network.wormhole;

import cursedflames.bountifulbaubles.network.NBTPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerWormholeRequest {
   public HandlerWormholeRequest() {
   }

   public static void handleWormholeRequest(NBTPacket message, MessageContext ctx) {
      NBTTagCompound tag = message.getTag();
      if (tag.hasUniqueId("sender") && tag.hasKey("name")) {
         String name = tag.getString("name");
         ITextComponent base = new TextComponentString(name + " has requested to teleport to you. Use /wormhole, or click: ");
         ITextComponent acc = new TextComponentString(TextFormatting.GREEN + "[ACCEPT]");
         ITextComponent rej = new TextComponentString(TextFormatting.RED + "[REJECT]");
         acc.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wormhole acc " + name));
         rej.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wormhole deny " + name));
         base.appendSibling(acc).appendText(" ").appendSibling(rej);
         Minecraft.getMinecraft().player.sendMessage(base);
      }
   }
}
