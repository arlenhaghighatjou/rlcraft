package crafttweaker.mc1120.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class SpecialMessagesChat {
   public static final ITextComponent EMPTY_TEXTMESSAGE = new TextComponentString("");

   public SpecialMessagesChat() {
   }

   public static ITextComponent getClickableCommandText(String message, String command, boolean runDirectly) {
      Style style = new Style();
      ClickEvent click = new ClickEvent(runDirectly ? Action.RUN_COMMAND : Action.SUGGEST_COMMAND, command);
      style.setClickEvent(click);
      HoverEvent hoverEvent = new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to execute [§6" + command + "§r]"));
      style.setHoverEvent(hoverEvent);
      return (new TextComponentString(message)).setStyle(style);
   }

   public static ITextComponent getClickableBrowserLinkText(String message, String url) {
      Style style = new Style();
      ClickEvent click = new ClickEvent(Action.OPEN_URL, url);
      style.setClickEvent(click);
      style.setColor(TextFormatting.AQUA);
      style.setUnderlined(Boolean.valueOf(true));
      HoverEvent hoverEvent = new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to Open [§6" + url + "§r]"));
      style.setHoverEvent(hoverEvent);
      return (new TextComponentString(message)).setStyle(style);
   }

   public static ITextComponent getFileOpenText(String message, String filepath) {
      Style style = new Style();
      ClickEvent click = new ClickEvent(Action.OPEN_FILE, filepath);
      style.setClickEvent(click);
      HoverEvent hoverEvent = new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open [§6" + filepath + "§r]"));
      style.setHoverEvent(hoverEvent);
      return (new TextComponentString(message)).setStyle(style);
   }

   public static ITextComponent getNormalMessage(String message) {
      return new TextComponentString(message);
   }

   public static ITextComponent getLinkToCraftTweakerLog(String message, ICommandSender sender) {
      return sender.getEntityWorld().isRemote ? getNormalMessage(message + "\nSee §acrafttweaker.log §rin your minecraft dir") : getFileOpenText(message + "\nSee §acrafttweaker.log §r[§6Click here to open§r]", CTChatCommand.CRAFTTWEAKER_LOG_PATH);
   }

   public static ITextComponent getCopyMessage(String message, String copyMessage) {
      Style style = new Style();
      ClickEvent click = new ClickEvent(Action.RUN_COMMAND, "/ct copy " + copyMessage);
      style.setClickEvent(click);
      HoverEvent hoverEvent = new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy [§6" + copyMessage + "§r]"));
      style.setHoverEvent(hoverEvent);
      return (new TextComponentString(message)).setStyle(style);
   }
}
