package crafttweaker.mc1120.commands;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.data.NBTConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class NamesCommand extends CraftTweakerCommand {
   private ArrayList<String> subCommands;

   public NamesCommand() {
      super("names");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct names", "/ct names", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all item names in the game to the CraftTweaker log"), SpecialMessagesChat.getClickableCommandText(" §a/ct names display unloc maxstack maxuse maxdamage rarity repaircost damageable repairable creativetabs enchantability burntime", "/ct names display unloc maxstack maxuse maxdamage rarity repaircost damageable repairable creativetabs", true), SpecialMessagesChat.getNormalMessage("  §bAdds all possible information to the output.")});
      this.subCommands = new ArrayList(2);

      for(NamesCommand.NameCommandParams para : NamesCommand.NameCommandParams.values()) {
         if (para != NamesCommand.NameCommandParams.REGISTRY_NAME) {
            this.subCommands.add(para.getCommandEquivalent());
         }
      }

   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      List<NamesCommand.NameCommandParams> paramList = new ArrayList();
      paramList.add(NamesCommand.NameCommandParams.REGISTRY_NAME);

      for(String arg : args) {
         for(NamesCommand.NameCommandParams para : NamesCommand.NameCommandParams.values()) {
            if (para.getCommandEquivalent().equals(arg)) {
               paramList.add(para);
               break;
            }
         }
      }

      List<Item> items = new ArrayList(BracketHandlerItem.getItemNames().values());
      items.sort(Commands.ITEM_COMPARATOR);
      CraftTweakerAPI.logCommand("List of all registered Items:");
      StringBuilder syntaxStringBuilder = new StringBuilder();
      HashMap<NamesCommand.NameCommandParams, Integer> timesUsed = new HashMap();

      for(NamesCommand.NameCommandParams param : paramList) {
         if (param == NamesCommand.NameCommandParams.REGISTRY_NAME) {
            syntaxStringBuilder.append("\"").append(param.toString()).append("\"");
         } else {
            syntaxStringBuilder.append(",\"").append(param.toString());
            if (!timesUsed.containsKey(param)) {
               timesUsed.put(param, Integer.valueOf(1));
            } else {
               int t = ((Integer)timesUsed.get(param)).intValue() + 1;
               timesUsed.replace(param, Integer.valueOf(t));
               syntaxStringBuilder.append(t);
            }

            syntaxStringBuilder.append("\"");
         }
      }

      CraftTweakerAPI.logCommand(syntaxStringBuilder.toString());
      int totalAmount = 0;

      for(Item item : items) {
         if (item != null) {
            NonNullList<ItemStack> list = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, list);
            if (!list.isEmpty()) {
               for(ItemStack stack : list) {
                  CraftTweakerAPI.logCommand(this.createOutputForItem(stack, paramList));
                  ++totalAmount;
               }
            } else {
               ItemStack stack = new ItemStack(item, 1, 0);
               CraftTweakerAPI.logCommand(this.createOutputForItem(stack, paramList));
            }
         }
      }

      CraftTweakerAPI.logCommand("A total of " + items.size() + " unique Items registered by CraftTweaker.");
      CraftTweakerAPI.logCommand("A total of " + totalAmount + " effective Items registered by CraftTweaker.");
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List generated", sender));
   }

   public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      List<String> currentPossibleCommands = new ArrayList();
      if (args.length <= 0) {
         return new ArrayList(this.subCommands);
      } else {
         for(String cmd : this.subCommands) {
            if (cmd.startsWith(args[args.length - 1])) {
               currentPossibleCommands.add(cmd);
            }
         }

         return currentPossibleCommands;
      }
   }

   public static String prepareForCSV(String string) {
      return string.replace("\"", "\"\"");
   }

   protected String createOutputForItem(ItemStack stack, List<NamesCommand.NameCommandParams> paramList) {
      StringBuilder sb = new StringBuilder();

      for(NamesCommand.NameCommandParams type : paramList) {
         if (type == NamesCommand.NameCommandParams.REGISTRY_NAME) {
            sb.append("\"");
         } else {
            sb.append(",\"");
         }

         sb.append(prepareForCSV(type.getInformation(stack)));
         sb.append("\"");
      }

      return sb.toString();
   }

   private static enum NameCommandParams {
      REGISTRY_NAME("23498z9384z9283z49", (stack) -> {
         StringBuilder sb = new StringBuilder();
         sb.append("<").append(stack.getItem().getRegistryName() != null ? stack.getItem().getRegistryName().toString() : "[NO REGISTRY NAME] {" + stack.getItem().getClass().toString() + "}").append(stack.getMetadata() == 0 ? "" : ":" + stack.getMetadata()).append(">");
         if (stack.serializeNBT().hasKey("tag")) {
            String nbt = NBTConverter.from(stack.serializeNBT().getTag("tag"), false).toString();
            if (nbt.length() > 0) {
               sb.append(".withTag(").append(nbt).append(")");
            }
         }

         return sb.toString();
      }),
      DISPLAY_NAME("display", ItemStack::getDisplayName),
      MOD_ID("modid", (stack) -> {
         ResourceLocation resLoc = stack.getItem().getRegistryName();
         return resLoc != null ? stack.getItem().getRegistryName().getResourceDomain() : "[NO REGISTRY NAME] {" + stack.getItem().getClass().toString() + "}";
      }),
      UNLOCALIZED("unloc", ItemStack::getUnlocalizedName),
      MAX_STACK_SIZE("maxstack", (stack) -> {
         return Integer.toString(stack.getMaxStackSize());
      }),
      MAX_ITEM_USE_DURATION("maxuse", (stack) -> {
         return Integer.toString(stack.getMaxItemUseDuration());
      }),
      MAX_ITEM_DAMAGE("maxdamage", (stack) -> {
         return Integer.toString(stack.getMaxDamage());
      }),
      RARITY("rarity", (stack) -> {
         return stack.getItem().getForgeRarity(stack).getName();
      }),
      REPAIR_COST("repaircost", (stack) -> {
         return Integer.toString(stack.getRepairCost());
      }),
      DAMAGEABLE("damageable", (stack) -> {
         return Boolean.toString(stack.getItem().isDamageable());
      }),
      REPAIRABLE("repairable", (stack) -> {
         return Boolean.toString(stack.getItem().isRepairable());
      }),
      CREATIVE_TABS("creativetabs", (stack) -> {
         StringBuilder tabString = new StringBuilder();
         tabString.append("[");

         for(CreativeTabs creativeTabs : stack.getItem().getCreativeTabs()) {
            if (tabString.length() != 1) {
               tabString.append(", ");
            }

            if (creativeTabs != null) {
               tabString.append(creativeTabs.getTabLabel());
            }
         }

         tabString.append("]");
         return tabString.toString();
      }),
      ENCHANTABILITY("enchantability", (stack) -> {
         return Integer.toString(stack.getItem().getItemEnchantability(stack));
      }),
      BURN_TIME("burntime", (stack) -> {
         return Integer.toString(TileEntityFurnace.getItemBurnTime(stack));
      }),
      FOOD_VALUE("foodvalue", (stack) -> {
         return Integer.toString(stack.getItem() instanceof ItemFood ? ((ItemFood)stack.getItem()).getHealAmount(stack) : -1);
      }),
      SATURATION_VALUE("saturationvalue", (stack) -> {
         return Float.toString(stack.getItem() instanceof ItemFood ? ((ItemFood)stack.getItem()).getSaturationModifier(stack) : -1.0F);
      });

      private String commandEquivalent;
      private Function<ItemStack, String> informationProducer;

      private NameCommandParams(String commandEquivalent, Function<ItemStack, String> informationProducer) {
         this.commandEquivalent = commandEquivalent;
         this.informationProducer = informationProducer;
      }

      public String getCommandEquivalent() {
         return this.commandEquivalent;
      }

      public String getInformation(ItemStack stack) {
         return (String)this.informationProducer.apply(stack);
      }
   }
}
