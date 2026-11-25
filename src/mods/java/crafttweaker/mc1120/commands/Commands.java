package crafttweaker.mc1120.commands;

import com.google.gson.JsonPrimitive;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.mods.IMod;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IFurnaceRecipe;
import crafttweaker.api.world.IBiome;
import crafttweaker.mc1120.brackets.BracketHandlerBiomeType;
import crafttweaker.mc1120.brackets.BracketHandlerEnchantments;
import crafttweaker.mc1120.brackets.BracketHandlerPotion;
import crafttweaker.mc1120.commands.dumpzscommand.DumpZsTarget;
import crafttweaker.mc1120.commands.dumpzscommand.TargetHtml;
import crafttweaker.mc1120.commands.dumpzscommand.TargetJson;
import crafttweaker.mc1120.commands.dumpzscommand.TargetLog;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.player.MCPlayer;
import crafttweaker.mc1120.recipes.MCRecipeBase;
import crafttweaker.runtime.ScriptLoader;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class Commands {
   public static final Commands.PotionComparator POTION_COMPARATOR = new Commands.PotionComparator();
   public static final Comparator<Item> ITEM_COMPARATOR = new Commands.ItemComparator();

   public Commands() {
   }

   static void registerCommands() {
      CTChatCommand.registerCommand(new CraftTweakerCommand("foods") {
         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Foods:");

            for(IItemDefinition item : CraftTweakerAPI.game.getItems()) {
               for(IItemStack stack : item.getSubItems()) {
                  if (stack.isFood()) {
                     CraftTweakerAPI.logCommand("- " + stack.toCommandString() + " Heal amount: " + stack.getHealAmount() + " Saturation amount: " + stack.getSaturation());
                  }
               }
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of foods generated;", sender));
         }

         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct foods", "/ct foods", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all foods in the game to the crafttweaker.log")});
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("liquids") {
         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            List<ILiquidDefinition> liquids = CraftTweakerAPI.game.getLiquids();
            liquids.sort(CrafttweakerImplementationAPI.LIQUID_COMPARATOR);
            CraftTweakerAPI.logCommand("Liquids:");

            for(ILiquidDefinition liquid : liquids) {
               StringBuilder builder = (new StringBuilder("<liquid:" + liquid.getName() + ">")).append("\n");
               builder.append("- Name: ").append(liquid.getName()).append("\n");
               builder.append("- Display Name: ").append(liquid.getDisplayName()).append("\n");
               builder.append("- Luminosity:").append(liquid.getLuminosity()).append("\n");
               builder.append("- Density: ").append(liquid.getDensity()).append("\n");
               builder.append("- Temperature: ").append(liquid.getTemperature()).append("\n");
               builder.append("- Viscosity: ").append(liquid.getViscosity()).append("\n");
               builder.append("- Gaseous: ").append(liquid.isGaseous());
               CraftTweakerAPI.logCommand(builder.toString());
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of liquids generated;", sender));
         }

         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct liquids", "/ct liquids", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all liquid names in the game to the crafttweaker.log")});
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("blocks") {
         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            List<IBlockDefinition> blocks = CraftTweakerAPI.game.getBlocks();
            blocks.sort(CrafttweakerImplementationAPI.BLOCK_COMPARATOR);
            CraftTweakerAPI.logCommand("Blocks:");

            for(IBlockDefinition block : blocks) {
               CraftTweakerAPI.logCommand("<block:" + block.getId() + ">, " + block.getDisplayName());
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of blocks generated", sender));
         }

         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct blocks", "/ct blocks", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all blocks in the game to the crafttweaker log")});
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("entities") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct entities", "/ct entities", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all entity definitions in the game to the crafttweaker log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            List<IEntityDefinition> entities = CraftTweakerAPI.game.getEntities();
            entities.sort(CrafttweakerImplementationAPI.ENTITY_COMPARATOR);
            CraftTweakerAPI.logCommand("Entities:");

            for(IEntityDefinition entity : entities) {
               CraftTweakerAPI.logCommand(entity.getId() + " -- " + entity.getName());
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of Entities generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("potions") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct potions", "/ct potions", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all potions to the crafttweaker.log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            List<IPotion> potions = CraftTweakerAPI.game.getPotions();
            potions.sort(Commands.POTION_COMPARATOR);
            CraftTweakerAPI.logCommand("Potions:");
            BracketHandlerPotion.getPotionNames().forEach((k, v) -> {
               int id = Potion.REGISTRY.getIDForObject(v);
               CraftTweakerAPI.logCommand(k + " -- color: " + v.getLiquidColor() + (v.isBadEffect() ? " -- is bad effect" : " -- is good effect") + " -- PotionID: " + id);
            });
            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of Potions generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("recipeNames") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct recipeNames", "/ct recipeNames", true), SpecialMessagesChat.getNormalMessage(" §3A modid can be provided to filter results"), SpecialMessagesChat.getNormalMessage(" §3/ct recipeNames <modid>"), SpecialMessagesChat.getNormalMessage(" §3Lists all crafting recipe names in the game")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Recipe names:");
            String filter = args.length == 0 ? "" : args[0];

            for(Entry<ResourceLocation, IRecipe> entry : ForgeRegistries.RECIPES.getEntries()) {
               if (filter.isEmpty() || ((ResourceLocation)entry.getKey()).getResourceDomain().equalsIgnoreCase(filter)) {
                  CraftTweakerAPI.logCommand(((ResourceLocation)entry.getKey()).toString() + " - " + ((IRecipe)entry.getValue()).getRecipeOutput());
               }
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Recipe list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("reload") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct reload", "/ct reload", true), SpecialMessagesChat.getNormalMessage(" §3Gives information as to why reloading is not possible")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            sender.sendMessage(SpecialMessagesChat.getNormalMessage("Unfortunately reloading is not possible due to a forge change."));
            sender.sendMessage(SpecialMessagesChat.getClickableBrowserLinkText("Please read this", "https://www.reddit.com/r/feedthebeast/comments/6oxjtd/lets_talk_crafttweaker_reload/"));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("recipes") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct recipes", "/ct recipes", true), SpecialMessagesChat.getNormalMessage(" §3Lists all crafting recipes in the game"), SpecialMessagesChat.getClickableCommandText(" §a/ct recipes hand", "/ct recipes hand", true), SpecialMessagesChat.getNormalMessage("  §bLists all crafting recipes for the item in your hand"), SpecialMessagesChat.getNormalMessage("  §bAlso copies the recipes to clipboard"), SpecialMessagesChat.getClickableCommandText(" §a/ct recipes furnace", "/ct recipes furnace", true), SpecialMessagesChat.getNormalMessage("  §blists all furnace recipes in the game")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (args.length == 0) {
               sender.sendMessage(new TextComponentString("Generating recipe list, this could take a while..."));
               CraftTweakerAPI.logCommand("Recipes:");

               for(ICraftingRecipe recipe : CraftTweakerAPI.recipes.getAll()) {
                  try {
                     CraftTweakerAPI.logCommand(recipe.toCommandString());
                  } catch (Exception var11) {
                     if (recipe instanceof MCRecipeBase) {
                        MCRecipeBase recipeBase = (MCRecipeBase)recipe;
                        IItemStack out = recipeBase.getOutput();
                        CraftTweakerAPI.logError("Could not dump recipe for " + out, var11);
                     } else {
                        CraftTweakerAPI.logError("Could not dump recipe", var11);
                     }
                  }
               }

               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Recipe list generated", sender));
            } else if (args[0].equals("hand") && sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               IItemStack item = player.getCurrentItem();
               if (item != null) {
                  List<ICraftingRecipe> recipes = CraftTweakerAPI.recipes.getRecipesFor(item.anyAmount());
                  if (recipes.isEmpty()) {
                     player.sendChat("No crafting recipes found for that item");
                  } else {
                     StringBuilder recipesString = new StringBuilder();

                     for(ICraftingRecipe recipe : recipes) {
                        CraftTweakerAPI.logCommand(recipe.toCommandString());
                        player.sendChat(recipe.toCommandString());
                        recipesString.append(recipe.toCommandString()).append("\n");
                     }

                     player.copyToClipboard(recipesString.toString());
                  }
               } else {
                  player.sendChat("No item was found");
               }
            } else if (args[0].equals("furnace")) {
               sender.sendMessage(new TextComponentString("Generating furnace list, this could take a while..."));
               CraftTweakerAPI.logCommand("Furnace Recipes:");

               for(IFurnaceRecipe recipe : CraftTweakerAPI.furnace.getAll()) {
                  try {
                     CraftTweakerAPI.logCommand(recipe.toCommandString());
                  } catch (Exception var10) {
                     CraftTweakerAPI.logError("Could not dump furnace recipe", var10);
                  }
               }

               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Furnace Recipe list generated", sender));
            } else {
               sender.sendMessage(new TextComponentString("Invalid arguments for recipes command"));
            }

         }

         public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            String[] subCommands = new String[]{"hand", "furnace"};
            ArrayList<String> currentPossibleCommands = new ArrayList();

            for(String cmd : subCommands) {
               if (cmd.startsWith(args[0])) {
                  currentPossibleCommands.add(cmd);
               }
            }

            return currentPossibleCommands;
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("inventory") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct inventory", "/ct inventory", true), SpecialMessagesChat.getNormalMessage(" §3Lists all items in your inventory")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            Entity entity = sender.getCommandSenderEntity();
            if (entity instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)entity);

               for(int i = 0; i < player.getInventorySize(); ++i) {
                  IItemStack stack = player.getInventoryStack(i);
                  if (stack != null) {
                     CraftTweakerAPI.logCommand(stack.toString());
                  }
               }

               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Recipe list generated", sender));
            } else {
               sender.sendMessage(new TextComponentString("This command can only be used as a Player (inGame)"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("hand") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct hand", "/ct hand", true), SpecialMessagesChat.getNormalMessage(" §3Outputs the name of the item in your hand"), SpecialMessagesChat.getNormalMessage(" §3Also copies the name to clipboard and prints"), SpecialMessagesChat.getNormalMessage(" §3OreDict entries")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
               ItemStack heldItem = player.getHeldItemMainhand();
               if (!heldItem.isEmpty()) {
                  List<String> oreDictNames = CommandUtils.getOreDictOfItem(heldItem);
                  int meta = heldItem.getMetadata();
                  String itemName = "<" + heldItem.getItem().getRegistryName() + (meta == 0 ? "" : ":" + meta) + ">";
                  String withNBT = "";
                  if (heldItem.serializeNBT().hasKey("tag")) {
                     String nbt = NBTConverter.from(heldItem.serializeNBT().getTag("tag"), false).toString();
                     if (nbt.length() > 0) {
                        withNBT = ".withTag(" + nbt + ")";
                     }
                  }

                  ClipboardHelper.copyStringPlayer(player, itemName + withNBT);
                  ClipboardHelper.sendMessageWithCopy(player, "Item §2" + itemName + "§a" + withNBT, itemName + withNBT);
                  ILiquidStack liquidStack = CraftTweakerMC.getILiquidStack(FluidUtil.getFluidContained(heldItem));
                  if (liquidStack != null) {
                     String liquidCommandString = liquidStack.toCommandString();
                     ClipboardHelper.sendMessageWithCopy(player, "Contains Liquid §2" + liquidCommandString, liquidCommandString);
                  }

                  if (!oreDictNames.isEmpty()) {
                     sender.sendMessage(new TextComponentString("§3OreDict Entries:"));

                     for(String oreName : oreDictNames) {
                        ClipboardHelper.sendMessageWithCopy(player, "    §e- §b" + oreName, "<ore:" + oreName + ">");
                     }
                  } else {
                     sender.sendMessage(new TextComponentString("§3No OreDict Entries"));
                  }
               } else {
                  RayTraceResult rayTraceResult = CommandUtils.getPlayerLookat(player, 100.0D);
                  if (rayTraceResult != null && rayTraceResult.typeOfHit == Type.BLOCK) {
                     BlockPos blockPos = rayTraceResult.getBlockPos();
                     IBlockState block = server.getEntityWorld().getBlockState(blockPos);
                     int meta = block.getBlock().getMetaFromState(block);
                     String blockName = "<" + block.getBlock().getRegistryName() + (meta == 0 ? "" : ":" + meta) + ">";
                     ClipboardHelper.copyStringPlayer(player, blockName);
                     ClipboardHelper.sendMessageWithCopy(player, "Block §2" + blockName + " §rat §9[" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]§r", blockName);

                     try {
                        List<String> oreDictNames = CommandUtils.getOreDictOfItem(new ItemStack(block.getBlock(), 1, block.getBlock().getMetaFromState(block)));
                        if (!oreDictNames.isEmpty()) {
                           sender.sendMessage(new TextComponentString("§3OreDict Entries:"));

                           for(String oreName : oreDictNames) {
                              ClipboardHelper.sendMessageWithCopy(player, "    §e- §b" + oreName, "<ore:" + oreName + ">");
                           }
                        } else {
                           sender.sendMessage(new TextComponentString("§3No OreDict Entries"));
                        }
                     } catch (IllegalArgumentException var14) {
                        sender.sendMessage(new TextComponentString("§3No OreDict Entries"));
                     }
                  } else {
                     sender.sendMessage(new TextComponentString("§4Please hold an Item in your hand or look at a Block."));
                  }
               }
            } else {
               sender.sendMessage(new TextComponentString("This command can only be casted by a player inGame"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("json") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct json", "/ct json", true), SpecialMessagesChat.getNormalMessage(" §3Outputs the nbt of the item in your hand"), SpecialMessagesChat.getNormalMessage(" §3This format is in json format, different from the one used in ZenScript"), SpecialMessagesChat.getClickableCommandText(" §a/ct json escaped", "/ct json escaped", true), SpecialMessagesChat.getNormalMessage("  §bAutomatically escapes the string to use in json.")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
               ItemStack heldItem = player.getHeldItemMainhand();
               if (!heldItem.isEmpty()) {
                  int meta = heldItem.getMetadata();
                  String itemName = "[" + heldItem.getItem().getRegistryName() + (meta == 0 ? "" : ":" + meta) + "]";
                  String withNBT = "";
                  if (heldItem.serializeNBT().hasKey("tag")) {
                     String nbt = heldItem.serializeNBT().getTag("tag").toString();
                     if (args.length == 1) {
                        nbt = (new JsonPrimitive(nbt)).toString();
                     }

                     if (nbt.length() > 0) {
                        withNBT = " NBT: [" + nbt + "]";
                     }
                  }

                  ClipboardHelper.copyStringPlayer(player, itemName + withNBT);
                  ClipboardHelper.sendMessageWithCopy(player, "Item §2" + itemName + "§a" + withNBT, itemName + withNBT);
               } else {
                  sender.sendMessage(new TextComponentString("§4Please hold an Item in your hand."));
               }
            } else {
               sender.sendMessage(new TextComponentString("This command can only be casted by a player inGame"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("mods") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct mods", "/ct mods", true), SpecialMessagesChat.getNormalMessage(" §3Outputs all active mod IDs and versions in the game")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Mods list:");

            for(IMod mod : CraftTweakerAPI.loadedMods) {
               String message = "§5" + mod.getId() + "§r - §d" + mod.getName() + "§ - " + mod.getVersion();
               String messageWithoutColor = mod.getId() + " - " + mod.getName() + " - " + mod.getVersion();
               sender.sendMessage(new TextComponentString(message));
               CraftTweakerAPI.logCommand(messageWithoutColor);
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of Mods generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("oredict") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct oredict", "/ct oredict", true), SpecialMessagesChat.getNormalMessage(" §3Outputs all ore dictionary entries in the game to the crafttweaker log"), SpecialMessagesChat.getClickableCommandText("§a/ct oredict <name>", "/ct oredict ", false), SpecialMessagesChat.getNormalMessage("  §bOutputs all items in the given ore dictionary entry to the crafttweaker log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (args.length > 0) {
               String entryName = args[0];
               IOreDictEntry entry = CraftTweakerAPI.oreDict.get(entryName);
               if (entry.isEmpty()) {
                  sender.sendMessage(new TextComponentString("Entry doesn't exist"));
                  return;
               }

               CraftTweakerAPI.logCommand("Ore entries for " + entryName + ":");

               for(IItemStack ore : entry.getItems()) {
                  CraftTweakerAPI.logCommand("-" + ore);
               }
            } else {
               for(IOreDictEntry entry : CraftTweakerAPI.oreDict.getEntries()) {
                  if (!entry.isEmpty()) {
                     CraftTweakerAPI.logCommand("Ore entries for <ore:" + entry.getName() + "> :");

                     for(IItemStack ore : entry.getItems()) {
                        CraftTweakerAPI.logCommand("-" + ore);
                     }
                  }
               }
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("OreDict list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("seeds") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct seeds", "/ct seeds", true), SpecialMessagesChat.getNormalMessage(" §3Prints all seeds registered"), SpecialMessagesChat.getNormalMessage(" §3for tall grass")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Seeds:");

            for(WeightedItemStack seed : CraftTweakerAPI.vanilla.getSeeds().getSeeds()) {
               String itemname = "<" + seed.getStack().getName() + ":" + seed.getStack().getDamage() + ">";
               String message = "§2" + itemname + "§r - §e" + (int)seed.getChance();
               sender.sendMessage(SpecialMessagesChat.getCopyMessage(message, itemname));
               CraftTweakerAPI.logCommand("Seed: " + message);
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Seed list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("wiki") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct wiki", "/ct wiki", true), SpecialMessagesChat.getNormalMessage(" §3Opens your browser with the wiki")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            String url = "https://docs.blamejared.com/1.12";
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               player.openBrowser(url);
               sender.sendMessage(SpecialMessagesChat.getClickableBrowserLinkText(url, url));
            } else {
               sender.sendMessage(new TextComponentString(url));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("bugs") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct bugs", "/ct bugs", true), SpecialMessagesChat.getNormalMessage(" §3Opens your browser with the GitHub bug tracker")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               player.openBrowser("https://github.com/CraftTweaker/CraftTweaker/issues");
               sender.sendMessage(SpecialMessagesChat.getClickableBrowserLinkText("https://github.com/CraftTweaker/CraftTweaker/issues", "https://github.com/CraftTweaker/CraftTweaker/issues"));
            } else {
               sender.sendMessage(new TextComponentString("https://github.com/CraftTweaker/CraftTweaker/issues"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("discord") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct discord", "/ct discord", true), SpecialMessagesChat.getNormalMessage(" §3Opens your browser with a link to the Discord server")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               player.openBrowser("https://discord.blamejared.com/");
               sender.sendMessage(SpecialMessagesChat.getClickableBrowserLinkText("https://discord.blamejared.com/", "https://discord.blamejared.com/"));
            } else {
               sender.sendMessage(new TextComponentString("https://discord.blamejared.com/"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("docs") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct docs", "/ct docs", true), SpecialMessagesChat.getNormalMessage(" §3Opens your browser with the docs")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               player.openBrowser("http://docs.blamejared.com/en/");
               sender.sendMessage(SpecialMessagesChat.getClickableBrowserLinkText("http://docs.blamejared.com/en/", "http://docs.blamejared.com/en/"));
            } else {
               sender.sendMessage(new TextComponentString("http://docs.blamejared.com/en/"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("biomes") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct biomes", "/ct biomes", true), SpecialMessagesChat.getNormalMessage(" §3Lists all the biomes in the game")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Biomes:");

            for(IBiome biome : CraftTweakerAPI.game.getBiomes()) {
               CraftTweakerAPI.logCommand("-" + biome.getId() + "\t" + biome.getName());
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Biome list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("biomeTypes") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct biomeTypes", "/ct biomeTypes", true), SpecialMessagesChat.getNormalMessage(" §3Lists all the biome types in the game")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Biome Types:");
            BracketHandlerBiomeType.biomeTypes.forEach((s, iBiomeType) -> {
               CraftTweakerAPI.logCommand("-" + s);
            });
            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Biome Types list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("enchants") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct enchants", "/ct enchants", true), SpecialMessagesChat.getNormalMessage(" §3Lists all the enchants in the game.")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("Enchants:");
            BracketHandlerEnchantments.enchantments.forEach((s, iEnchant) -> {
               String name = ((Enchantment)iEnchant.getInternal()).getTranslatedName(1);
               CraftTweakerAPI.logCommand(String.format("-<enchantment:%s> - %s (at level 1, max level %d, treasure: %s, enchant id %d)", s, name, iEnchant.getMaxLevel(), iEnchant.isTreasureEnchantment() ? "yes" : "no", iEnchant.getID()));
            });
            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Enchants list generated", sender));
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("blockinfo") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct blockinfo", "/ct blockinfo", true), SpecialMessagesChat.getNormalMessage(" §3Activates or deactivates block reader. In block info mode,"), SpecialMessagesChat.getNormalMessage(" §3right-click a block to see ID, meta and tile entity data")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               MCPlayer player = new MCPlayer((EntityPlayer)sender.getCommandSenderEntity());
               if (CrafttweakerImplementationAPI.blockInfoPlayers.isEmpty()) {
                  CrafttweakerImplementationAPI.blockEventHandler = CrafttweakerImplementationAPI.events.onPlayerInteract(CrafttweakerImplementationAPI.LISTEN_BLOCK_INFO);
               }

               if (CrafttweakerImplementationAPI.blockInfoPlayers.contains(player)) {
                  CrafttweakerImplementationAPI.blockInfoPlayers.remove(player);
                  player.sendChat("Block info mode deactivated.");
               } else {
                  CrafttweakerImplementationAPI.blockInfoPlayers.add(player);
                  player.sendChat("Block info mode activated. Right-click a block to see its data.");
               }

               if (CrafttweakerImplementationAPI.blockInfoPlayers.isEmpty()) {
                  CrafttweakerImplementationAPI.blockEventHandler.close();
               }
            } else {
               sender.sendMessage(new TextComponentString("This Command can only be performed from a Player(InGame)"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("copy") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct copy", "/ct copy", false), SpecialMessagesChat.getNormalMessage(" §3Copies the provided string behind it")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            ClipboardHelper.copyCommandRun(sender, args);
         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("nbt") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct nbt", "/ct nbt", true), SpecialMessagesChat.getNormalMessage(" §3Shows the NBT of the block you are looking at"), SpecialMessagesChat.getNormalMessage(" §3or the item you are holding")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
               ItemStack heldItem = player.getHeldItemMainhand();
               if (heldItem != ItemStack.EMPTY) {
                  String itemName = "<" + heldItem.getItem().getRegistryName() + ":" + heldItem.getMetadata() + ">";
                  String nbt = "";
                  if (heldItem.serializeNBT().hasKey("tag")) {
                     nbt = NBTConverter.from(heldItem.serializeNBT().getTag("tag"), false).toString();
                  }

                  String withNBT = "";
                  if (nbt.length() > 0) {
                     withNBT = ".withTag(" + nbt + ")";
                  }

                  ClipboardHelper.copyStringPlayer(player, itemName + withNBT);
                  ClipboardHelper.sendMessageWithCopy(player, "Item §2" + itemName, itemName + withNBT);
                  if (nbt.length() > 0) {
                     sender.sendMessage(new TextComponentString("§3NBT-Data:"));
                     ClipboardHelper.sendMessageWithCopy(player, NBTUtils.getAppealingString(nbt), nbt);
                  } else {
                     sender.sendMessage(new TextComponentString("§3No NBT Data"));
                  }
               } else {
                  RayTraceResult rayTraceResult = CommandUtils.getPlayerLookat(player, 100.0D);
                  if (rayTraceResult != null && rayTraceResult.typeOfHit == Type.BLOCK) {
                     BlockPos blockPos = rayTraceResult.getBlockPos();
                     IBlockState block = server.getEntityWorld().getBlockState(blockPos);
                     ClipboardHelper.sendMessageWithCopy(player, "Block §2[" + block.getBlock().getRegistryName() + ":" + block.getBlock().getMetaFromState(block) + "] §rat §9[" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]§r", block.getBlock().getRegistryName() + ":" + block.getBlock().getMetaFromState(block));
                     TileEntity te = server.getEntityWorld().getTileEntity(blockPos);
                     if (te != null) {
                        sender.sendMessage(new TextComponentString("§3NBT-Data:"));
                        String nbt = NBTConverter.from(te.serializeNBT(), false).toString();
                        ClipboardHelper.sendMessageWithCopy(player, NBTUtils.getAppealingString(nbt), nbt);
                     } else {
                        sender.sendMessage(new TextComponentString("§3Block is no TileEntity and has no NBT"));
                     }
                  } else {
                     sender.sendMessage(new TextComponentString("§4Please hold an Item in your hand or look at a Block."));
                  }
               }
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("log") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct log", "/ct log", true), SpecialMessagesChat.getNormalMessage(" §3Sends a clickable link to open the crafttweaker.log")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("", sender));
            } else {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("Command must be executed as a Player (inGame)"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("scripts") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct scripts", "/ct scripts", true), SpecialMessagesChat.getNormalMessage(" §3Sends a clickable link to open the scripts directory")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               sender.sendMessage(SpecialMessagesChat.getFileOpenText("Click to open the §a/scripts/ §rDirectory [§6Click here to open§r]", (new File("scripts/")).getAbsolutePath()));
            } else {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("Path to the scripts: " + (new File("scripts/")).getAbsolutePath()));
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("Command must be executed as a Player (inGame) to be clickable"));
            }

         }
      });
      CTChatCommand.registerCommand(new CraftTweakerCommand("syntax") {
         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct syntax", "/ct syntax", true), SpecialMessagesChat.getNormalMessage(" §3Checks the Syntax of the scripts"), SpecialMessagesChat.getNormalMessage(" §3To see the effect you have to restart the game"), SpecialMessagesChat.getNormalMessage(" §3Will print errors of the Bracket Handler")});
         }

         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            if (args.length > 0 && args[0].equals("debug")) {
               CraftTweakerAPI.tweaker.enableDebug();
            }

            sender.sendMessage(SpecialMessagesChat.getNormalMessage("§bBeginning load of the scripts"));
            List<ScriptLoader> erroredLoaders = new ArrayList();

            for(ScriptLoader loader : CraftTweakerAPI.tweaker.getLoaders()) {
               CraftTweakerAPI.tweaker.loadScript(true, loader);
               if (loader.getLoaderStage() == ScriptLoader.LoaderStage.ERROR) {
                  erroredLoaders.add(loader);
               }
            }

            if (erroredLoaders.isEmpty()) {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("Syntax of scripts is §acorrect§r, to see the effect §erestart the game"));
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("Please be advised that §bbrackets (<>) §rmay have §4errored, see above."));
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("If no errors appeared above everything was fine."));
            } else {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("These loaders failed to load correctly:"));
               sender.sendMessage(SpecialMessagesChat.getNormalMessage((String)erroredLoaders.stream().map(ScriptLoader::getMainName).collect(Collectors.joining(", "))));
               sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("§4Syntax of the scripts is incorrect!", sender));
            }

         }

         public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            List<String> commands = new ArrayList(1);
            commands.add("debug");
            return commands;
         }
      });
      CTChatCommand.registerCommand(new GiveCommand());
      CTChatCommand.registerCommand(new DumpZsCommand(new DumpZsTarget[]{new TargetLog(), new TargetHtml(), new TargetJson()}));
      CTChatCommand.registerCommand(new HelpCommand());
      CTChatCommand.registerCommand(new NamesCommand());
      CTChatCommand.registerCommand(new CraftTweakerCommand("keyNames") {
         public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            CraftTweakerAPI.logCommand("\"KeyName\" -> keyCode");
            Field[] fields = Keyboard.class.getFields();

            try {
               for(Field field : fields) {
                  if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(Integer.TYPE) && field.getName().startsWith("KEY_") && !field.getName().endsWith("WIN")) {
                     int key = field.getInt((Object)null);
                     String name = field.getName().substring(4);
                     CraftTweakerAPI.logCommand("\"" + name + "\" -> " + key);
                  }
               }
            } catch (Exception var11) {
               ;
            }

            sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("List of Key names generated", sender));
         }

         protected void init() {
            this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("§2/ct keyNames", "/ct keyNames", true), SpecialMessagesChat.getNormalMessage(" §3Outputs a list of all LWJGL keynames to their respective KeyCodes in the game to the crafttweaker.log")});
         }
      });
   }

   private static class ItemComparator implements Comparator<Item>, Serializable {
      private static final long serialVersionUID = 5425653624832997108L;

      private ItemComparator() {
      }

      public int compare(Item o1, Item o2) {
         return o1.getRegistryName().toString().compareTo(o2.getRegistryName().toString());
      }
   }

   private static class PotionComparator implements Comparator<IPotion>, Serializable {
      private static final long serialVersionUID = -56375350098392167L;

      private PotionComparator() {
      }

      public int compare(IPotion o1, IPotion o2) {
         return o1.name().compareTo(o2.name());
      }
   }
}
