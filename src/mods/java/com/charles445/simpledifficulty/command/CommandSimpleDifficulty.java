package com.charles445.simpledifficulty.command;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.JsonFileName;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class CommandSimpleDifficulty extends CommandBase {
   private final List<String> tabCompletionsCommands = Arrays.asList("help", "exportJson", "reloadJson", "addArmor", "addBlock", "addConsumableTemperature", "addConsumableThirst", "addDimension", "addFluid", "addHeldItem", "nbt", "setThirst", "setTemperature");
   private final String commandUsage = "/simpledifficulty help";
   private final String listOfCommands = "   help <command>\n   exportJson\n   reloadJson\n   addArmor <temperature>\n   addBlock <temperature>\n   addConsumableTemperature <group> <temperature> <duration>\n   addConsumableThirst <amount> <saturation> <thirstyChance>\n   addDimension <temperature>\n   addFluid <temperature>\n   addHeldItem <temperature>\n   nbt\n   setThirst <thirst> <saturation>\n   setTemperature <temperature>";
   private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
   private final String warn_invalidArgs = "Invalid Arguments";
   private final String warn_noItem = "Not holding an item!";
   private final String exportJsonReminder = "(Don't forget to exportJson !)";

   public CommandSimpleDifficulty() {
   }

   public List<String> getAliases() {
      return Arrays.asList("sd");
   }

   public String getName() {
      return "simpledifficulty";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getUsage(ICommandSender sender) {
      return "/simpledifficulty help";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.tabCompletionsCommands);
      } else {
         return args.length == 0 ? this.tabCompletionsCommands : Collections.emptyList();
      }
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         this.help(sender);
      } else {
         String var4 = args[0].toLowerCase(Locale.ENGLISH);
         byte var5 = -1;
         switch(var4.hashCode()) {
         case -1839141015:
            if (var4.equals("addhelditem")) {
               var5 = 8;
            }
            break;
         case -1245709823:
            if (var4.equals("reloadjson")) {
               var5 = 0;
            }
            break;
         case -1234033346:
            if (var4.equals("addarmor")) {
               var5 = 2;
            }
            break;
         case -1233287028:
            if (var4.equals("addblock")) {
               var5 = 3;
            }
            break;
         case -1229586999:
            if (var4.equals("addfluid")) {
               var5 = 7;
            }
            break;
         case 108864:
            if (var4.equals("nbt")) {
               var5 = 9;
            }
            break;
         case 3198785:
            if (var4.equals("help")) {
               var5 = 12;
            }
            break;
         case 209847964:
            if (var4.equals("exportjson")) {
               var5 = 1;
            }
            break;
         case 536795090:
            if (var4.equals("settemperature")) {
               var5 = 11;
            }
            break;
         case 647463216:
            if (var4.equals("addconsumabletemperature")) {
               var5 = 4;
            }
            break;
         case 1058085349:
            if (var4.equals("adddimension")) {
               var5 = 6;
            }
            break;
         case 1440942210:
            if (var4.equals("addconsumablethirst")) {
               var5 = 5;
            }
            break;
         case 1522832544:
            if (var4.equals("setthirst")) {
               var5 = 10;
            }
         }

         switch(var5) {
         case 0:
            this.updateJson(server, sender, args);
            break;
         case 1:
            this.exportJson(server, sender, args);
            break;
         case 2:
            this.addArmor(server, sender, args);
            break;
         case 3:
            this.addBlock(server, sender, args);
            break;
         case 4:
            this.addConsumableTemperature(server, sender, args);
            break;
         case 5:
            this.addConsumableThirst(server, sender, args);
            break;
         case 6:
            this.addDimension(server, sender, args);
            break;
         case 7:
            this.addFluid(server, sender, args);
            break;
         case 8:
            this.addHeldItem(server, sender, args);
            break;
         case 9:
            this.tagToString(server, sender, args);
            break;
         case 10:
            this.setThirst(server, sender, args);
            break;
         case 11:
            this.setTemperature(server, sender, args);
            break;
         case 12:
            this.helpCommand(server, sender, args);
            break;
         default:
            this.help(sender);
         }

      }
   }

   private void helpCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length < 2) {
         this.message(sender, "   help <command>\n   exportJson\n   reloadJson\n   addArmor <temperature>\n   addBlock <temperature>\n   addConsumableTemperature <group> <temperature> <duration>\n   addConsumableThirst <amount> <saturation> <thirstyChance>\n   addDimension <temperature>\n   addFluid <temperature>\n   addHeldItem <temperature>\n   nbt\n   setThirst <thirst> <saturation>\n   setTemperature <temperature>");
      } else {
         String var4 = args[1].toLowerCase();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case -1839141015:
            if (var4.equals("addhelditem")) {
               var5 = 9;
            }
            break;
         case -1245709823:
            if (var4.equals("reloadjson")) {
               var5 = 2;
            }
            break;
         case -1234033346:
            if (var4.equals("addarmor")) {
               var5 = 3;
            }
            break;
         case -1233287028:
            if (var4.equals("addblock")) {
               var5 = 4;
            }
            break;
         case -1229586999:
            if (var4.equals("addfluid")) {
               var5 = 8;
            }
            break;
         case 108864:
            if (var4.equals("nbt")) {
               var5 = 10;
            }
            break;
         case 3198785:
            if (var4.equals("help")) {
               var5 = 0;
            }
            break;
         case 209847964:
            if (var4.equals("exportjson")) {
               var5 = 1;
            }
            break;
         case 536795090:
            if (var4.equals("settemperature")) {
               var5 = 12;
            }
            break;
         case 647463216:
            if (var4.equals("addconsumabletemperature")) {
               var5 = 5;
            }
            break;
         case 1058085349:
            if (var4.equals("adddimension")) {
               var5 = 7;
            }
            break;
         case 1440942210:
            if (var4.equals("addconsumablethirst")) {
               var5 = 6;
            }
            break;
         case 1522832544:
            if (var4.equals("setthirst")) {
               var5 = 11;
            }
         }

         switch(var5) {
         case 0:
            this.message(sender, "If you need more help, you can contact the mod author on CurseForge or GitHub");
            return;
         case 1:
            this.message(sender, "Exports your in-game JSON changes to the config folder");
            return;
         case 2:
            this.message(sender, "Discards any unexported in-game JSON changes.\nReloads the JSON from the config folder");
            return;
         case 3:
            this.message(sender, "Adds the held armor to the armor JSON\n(changes temperature when worn)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)");
            return;
         case 4:
            this.message(sender, "Adds the held block to the block JSON\n(changes temperature when near the block)\nAdd argument --clear to remove the block from JSON first (ignores metadata and state)");
            return;
         case 5:
            this.message(sender, "Adds the held item to the consumableTemperature JSON\n(modifies temperature over time when consumed)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)");
            return;
         case 6:
            this.message(sender, "Adds the held item to the consumableThirst JSON\n(replenishes thirst when consumed)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)");
            return;
         case 7:
            this.message(sender, "Adds the dimension the player is in to the dimensionTemperature JSON");
            return;
         case 8:
            this.message(sender, "Adds the held fluid item to the fluid JSON\n(changes temperature when inside the fluid)");
            return;
         case 9:
            this.message(sender, "Adds the held item to the heldItems JSON\n(changes player temperature when held in mainhand or offhand)\nAdd argument --nbt to include NBT tag\nAdd argument --clear to remove the item from JSON first (ignores metadata and nbt)");
            return;
         case 10:
            this.message(sender, "Gets an item's NBT tag as a string for config use");
            return;
         case 11:
            this.message(sender, "Sets the player's thirst");
            return;
         case 12:
            this.message(sender, "Sets the player's temperature");
            return;
         default:
            this.message(sender, "/simpledifficulty help <command> \n(Replace <command> with a simpledifficulty command name)");
         }
      }
   }

   private void setThirst(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         try {
            if (args.length < 2) {
               this.message(sender, "Invalid Arguments <modid>");
               return;
            }

            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            IThirstCapability capability = SDCapabilities.getThirstData(player);
            capability.setThirstLevel(Integer.parseInt(args[1]));
            if (args.length >= 3) {
               capability.setThirstSaturation(Float.parseFloat(args[2]));
            }
         } catch (NumberFormatException var6) {
            this.message(sender, "Invalid Arguments <thirst> <saturation>");
            return;
         }
      }

   }

   private void setTemperature(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         try {
            if (args.length < 2) {
               this.message(sender, "Invalid Arguments <temperature>");
               return;
            }

            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
            capability.setTemperatureLevel(Integer.parseInt(args[1]));
         } catch (NumberFormatException var6) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      }

   }

   private void tagToString(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
         ItemStack stack = player.getHeldItemMainhand();
         if (stack.isEmpty()) {
            this.message(sender, "Not holding an item!");
            return;
         }

         if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            String compString = compound.toString();
            TextComponentString tc = new TextComponentString(compString);
            int metadata = this.getMetadataFromStack(stack);
            Style style = new Style();
            style.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/sdcopyidentity " + metadata + " " + compString));
            style.setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy identity to clipboard")));
            tc.setStyle(style);
            sender.sendMessage(tc);
         } else {
            this.message(sender, "This item has no NBT tag.");
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private int getMetadataFromStack(ItemStack stack) {
      return stack.getHasSubtypes() ? stack.getMetadata() : -1;
   }

   private void addBlock(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 2) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }

         try {
            float temperature = Float.parseFloat(args[1]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            Block block = Block.getBlockFromItem(stack.getItem());
            if (block == Blocks.AIR) {
               FluidStack fluidStack = FluidUtil.getFluidContained(stack);
               if (fluidStack == null) {
                  this.message(sender, "Couldn't find block for item!");
                  return;
               }

               block = fluidStack.getFluid().getBlock();
               if (block == Blocks.AIR) {
                  this.message(sender, "Couldn't find block for fluid!");
                  return;
               }

               if (block != Blocks.LAVA && block != Blocks.FLOWING_LAVA) {
                  if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                     block = Blocks.FLOWING_WATER;
                     JsonConfig.registerBlockTemperature(Blocks.WATER, temperature);
                  }
               } else {
                  block = Blocks.FLOWING_LAVA;
                  JsonConfig.registerBlockTemperature(Blocks.LAVA, temperature);
               }
            }

            if (this.hasClearArgument(args)) {
               JsonConfig.blockTemperatures.remove(block.getRegistryName().toString());
               this.message(sender, "Removed from JSON");
            }

            boolean accepted = JsonConfig.registerBlockTemperature(block, temperature);
            if (accepted) {
               this.message(sender, "Added block to " + JsonFileName.blockTemperatures + "!\n" + "(Don't forget to exportJson !)");
            } else {
               this.message(sender, "Block has properties information in the JSON, use the JSON instead!");
            }
         } catch (NumberFormatException var9) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addDimension(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 2) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }

         try {
            float temperature = Float.parseFloat(args[1]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            World world = player.world;
            if (world == null) {
               return;
            }

            JsonConfig.registerDimensionTemperature(world.provider.getDimension(), temperature);
            this.message(sender, "Added dimension to " + JsonFileName.dimensionTemperature + "!\n" + "(Don't forget to exportJson !)");
         } catch (NumberFormatException var7) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addFluid(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 2) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }

         try {
            float temperature = Float.parseFloat(args[1]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            FluidStack fluidStack = FluidUtil.getFluidContained(stack);
            if (fluidStack == null) {
               this.message(sender, "Couldn't find the item's fluid!");
               return;
            }

            JsonConfig.registerFluidTemperature(fluidStack.getFluid().getName(), temperature);
            this.message(sender, "Added fluid to " + JsonFileName.consumableThirst + "!\n" + "(Don't forget to exportJson !)");
         } catch (NumberFormatException var8) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addConsumableThirst(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 4) {
            this.message(sender, "Invalid Arguments <amount> <saturation> <thirstyChance>");
            return;
         }

         try {
            int amount = Integer.parseInt(args[1]);
            float saturation = Float.parseFloat(args[2]);
            float thirstyChance = Float.parseFloat(args[3]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            if (this.hasClearArgument(args)) {
               JsonConfig.consumableThirst.remove(this.getRegistryName(stack));
               this.message(sender, "Removed from JSON");
            }

            boolean nbtArgument = this.hasNBTArgument(args);
            if (nbtArgument && stack.hasTagCompound()) {
               JsonConfig.registerConsumableThirst(this.getRegistryName(stack), amount, saturation, thirstyChance, this.getFullIdentity(stack));
               this.message(sender, "Added consumable item with nbt to " + JsonFileName.consumableThirst + "!\n" + "(Don't forget to exportJson !)");
            } else {
               JsonConfig.registerConsumableThirst(stack, amount, saturation, thirstyChance);
               this.message(sender, "Added consumable item to " + JsonFileName.consumableThirst + "!\n" + "(Don't forget to exportJson !)");
            }
         } catch (NumberFormatException var10) {
            this.message(sender, "Invalid Arguments <amount> <saturation> <thirstyChance>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addHeldItem(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 2) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }

         try {
            float temperature = Float.parseFloat(args[1]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            if (this.hasClearArgument(args)) {
               JsonConfig.heldItemTemperatures.remove(this.getRegistryName(stack));
               this.message(sender, "Removed from JSON");
            }

            boolean nbtArgument = this.hasNBTArgument(args);
            if (nbtArgument && stack.hasTagCompound()) {
               JsonConfig.registerHeldItem(this.getRegistryName(stack), temperature, this.getFullIdentity(stack));
               this.message(sender, "Added held item with nbt to " + JsonFileName.heldItemTemperatures + "!\n" + "(Don't forget to exportJson !)");
            } else {
               JsonConfig.registerHeldItem(stack, temperature);
               this.message(sender, "Added held item to " + JsonFileName.heldItemTemperatures + "!\n" + "(Don't forget to exportJson !)");
            }
         } catch (NumberFormatException var8) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addConsumableTemperature(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 4) {
            this.message(sender, "Invalid Arguments <group> <temperature> <duration>");
            return;
         }

         try {
            String group = args[1];
            float temperature = Float.parseFloat(args[2]);
            int duration = Integer.parseInt(args[3]);
            group = group.replaceAll("\"", "");
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            if (this.hasClearArgument(args)) {
               JsonConfig.consumableTemperature.remove(this.getRegistryName(stack));
               this.message(sender, "Removed from JSON");
            }

            boolean nbtArgument = this.hasNBTArgument(args);
            if (nbtArgument && stack.hasTagCompound()) {
               JsonConfig.registerConsumableTemperature(group, this.getRegistryName(stack), temperature, duration, this.getFullIdentity(stack));
               this.message(sender, "Added consumable item with nbt to " + JsonFileName.consumableTemperature + "!\n" + "(Don't forget to exportJson !)");
            } else {
               JsonConfig.registerConsumableTemperature(group, stack, temperature, duration);
               this.message(sender, "Added consumable item to " + JsonFileName.consumableTemperature + "!\n" + "(Don't forget to exportJson !)");
            }
         } catch (NumberFormatException var10) {
            this.message(sender, "Invalid Arguments <group> <temperature> <duration>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private void addArmor(MinecraftServer server, ICommandSender sender, String[] args) {
      if (this.isAdminPlayer(sender)) {
         if (args.length < 2) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }

         try {
            float temperature = Float.parseFloat(args[1]);
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty()) {
               this.message(sender, "Not holding an item!");
               return;
            }

            if (this.hasClearArgument(args)) {
               JsonConfig.armorTemperatures.remove(this.getRegistryName(stack));
               this.message(sender, "Removed from JSON");
            }

            boolean nbtArgument = this.hasNBTArgument(args);
            if (nbtArgument && stack.hasTagCompound()) {
               JsonConfig.registerArmorTemperature(this.getRegistryName(stack), temperature, this.getFullIdentity(stack));
               this.message(sender, "Added armor with nbt to " + JsonFileName.armorTemperatures + "!\n" + "(Don't forget to exportJson !)");
            } else {
               JsonConfig.registerArmorTemperature(stack, temperature);
               this.message(sender, "Added armor to " + JsonFileName.armorTemperatures + "!\n" + "(Don't forget to exportJson !)");
            }
         } catch (NumberFormatException var8) {
            this.message(sender, "Invalid Arguments <temperature>");
            return;
         }
      } else {
         this.message(sender, "You do not have permission, or are not a player ingame!");
      }

   }

   private String getRegistryName(ItemStack stack) {
      return stack.getItem().getRegistryName().toString();
   }

   private JsonItemIdentity getFullIdentity(ItemStack stack) {
      return stack.hasTagCompound() ? new JsonItemIdentity(this.getMetadataFromStack(stack), stack.getTagCompound().toString()) : new JsonItemIdentity(this.getMetadataFromStack(stack));
   }

   private void exportJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (this.hasPermissionLevel(sender, 4)) {
         this.message(sender, "Exporting SimpleDifficulty JSON");
         String result = JsonConfigInternal.manuallyExportAll();
         this.message(sender, result);
      }

   }

   private void updateJson(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (this.hasPermissionLevel(sender, 4)) {
         this.message(sender, "Reloading SimpleDifficulty JSON");
         JsonConfigInternal.jsonErrors.clear();
         JsonConfigInternal.clearContainers();
         JsonConfigInternal.postInit(SimpleDifficulty.jsonDirectory);

         for(String s : JsonConfigInternal.jsonErrors) {
            sender.sendMessage(new TextComponentString(s));
         }
      }

   }

   private boolean isAdminPlayer(ICommandSender sender) {
      return this.hasPermissionLevel(sender, 4) && sender.getCommandSenderEntity() instanceof EntityPlayer;
   }

   private void help(ICommandSender sender) {
      sender.sendMessage(new TextComponentString(this.getUsage(sender)));
   }

   private void message(ICommandSender sender, String message) {
      sender.sendMessage(new TextComponentString(message));
   }

   private boolean hasPermissionLevel(ICommandSender sender, int permLevel) {
      return sender.canUseCommand(permLevel, "simpledifficulty");
   }

   private boolean hasNBTArgument(String[] input) {
      return this.hasArgument("--nbt", input);
   }

   private boolean hasClearArgument(String[] input) {
      return this.hasArgument("--clear", input);
   }

   private boolean hasArgument(String argument, String[] input) {
      if (input == null) {
         return false;
      } else {
         for(String s : input) {
            if (s.equals(argument)) {
               return true;
            }
         }

         return false;
      }
   }
}
