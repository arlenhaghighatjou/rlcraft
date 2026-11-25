package crafttweaker.mc1120.commands;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class HelpCommand extends CraftTweakerCommand {
   private final List<HelpCommand.SizedList<CraftTweakerCommand>> pages = new ArrayList();
   public static final int lineLimit = 19;
   public static final int windowWidth = 64;

   public HelpCommand() {
      super("help");
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      int page = 0;
      if (args.length > 0) {
         try {
            page = Integer.parseInt(args[0]);
         } catch (NumberFormatException var6) {
            sender.sendMessage(SpecialMessagesChat.getNormalMessage("Page number provided was invalid!"));
         }
      }

      this.sendUsage(sender, page);
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct help", "/ct help", true), SpecialMessagesChat.getNormalMessage(" \u00a73Prints out the this help page")});
   }

   public void sendUsage(ICommandSender sender, int pageNum) {
      this.calculatePages();
      HelpCommand.SizedList<CraftTweakerCommand> page = (HelpCommand.SizedList)this.pages.get(pageNum);
      sender.sendMessage(SpecialMessagesChat.getNormalMessage("Tex: " + page.getTextSize()));
      sender.sendMessage(SpecialMessagesChat.getNormalMessage(TextFormatting.DARK_BLUE + "-----------------------------"));
      int i = 0;

      for(int textSize = 19 - page.getTextSize(); i < textSize; ++i) {
         sender.sendMessage(SpecialMessagesChat.EMPTY_TEXTMESSAGE);
      }

      for(CraftTweakerCommand craftTweakerCommand : page.getList()) {
         for(ITextComponent s : craftTweakerCommand.getDescription()) {
            sender.sendMessage(s);
         }

         sender.sendMessage(SpecialMessagesChat.EMPTY_TEXTMESSAGE);
      }

      ITextComponent back = pageNum == 0 ? SpecialMessagesChat.getClickableCommandText(TextFormatting.YELLOW + "##", "/ct help " + pageNum, true) : SpecialMessagesChat.getClickableCommandText(TextFormatting.GREEN + "<<", "/ct help " + (pageNum - 1), true);
      ITextComponent forth = pageNum == this.pages.size() - 1 ? SpecialMessagesChat.getClickableCommandText(TextFormatting.YELLOW + "##", "/ct help " + pageNum, true) : SpecialMessagesChat.getClickableCommandText(TextFormatting.GREEN + ">>", "/ct help " + (pageNum + 1), true);
      back.appendSibling(SpecialMessagesChat.getClickableCommandText(TextFormatting.DARK_BLUE + " ----  " + TextFormatting.GOLD + (pageNum + 1) + "/" + this.pages.size() + TextFormatting.DARK_BLUE + "  ----- ", "/ct help " + pageNum, true));
      back.appendSibling(forth);
      sender.sendMessage(back);
   }

   private void calculatePages() {
      this.pages.clear();
      HelpCommand.SizedList<CraftTweakerCommand> currentList = new HelpCommand.SizedList<CraftTweakerCommand>();
      this.pages.add(currentList);
      int currentCount = 1;

      for(CraftTweakerCommand entry : CTChatCommand.getCraftTweakerCommands().values()) {
         int currentEntry = 1;

         for(ITextComponent iTextComponent : entry.getDescription()) {
            int width = iTextComponent.getUnformattedText().length() / 64 + 1;
            currentEntry += width;
         }

         currentCount += currentEntry;
         if (currentCount >= 19) {
            currentList.setTextSize(currentCount - currentEntry);
            currentList = new HelpCommand.SizedList<CraftTweakerCommand>();
            this.pages.add(currentList);
            currentCount = 1 + currentEntry;
         }

         currentList.add(entry);
      }

      currentList.setTextSize(currentCount);
   }

   private static class SizedList<T> {
      private int textSize;
      private List<T> list;

      private SizedList() {
         this.textSize = 0;
         this.list = new ArrayList();
      }

      public int getTextSize() {
         return this.textSize;
      }

      public void setTextSize(int textSize) {
         this.textSize = textSize;
      }

      public List<T> getList() {
         return this.list;
      }

      public boolean add(T t) {
         return this.list.add(t);
      }
   }
}
