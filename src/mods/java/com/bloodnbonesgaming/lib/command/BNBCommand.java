package com.bloodnbonesgaming.lib.command;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BNBCommand implements ICommand {
   private static final List<String> aliases = new ArrayList();
   Gson gson = new Gson();

   public BNBCommand() {
   }

   public String getName() {
      return "bnb";
   }

   public List<String> getAliases() {
      return aliases;
   }

   public String getUsage(ICommandSender sender) {
      return "bnbgaminglib.cmd.item.usage";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (sender instanceof EntityPlayer) {
         if (args.length > 0) {
            if ("hand".equalsIgnoreCase(args[0])) {
               EntityPlayer player = (EntityPlayer)sender;
               ItemStack stack = player.getHeldItemMainhand();
               if (stack != null && !stack.isEmpty()) {
                  String syntax = getStringForStackNew(stack);
                  sender.sendMessage((new TextComponentString(syntax)).setStyle((new Style()).setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/clipboard " + syntax))));
                  sender.sendMessage((new TextComponentString("[Copied To Clipboard]")).setStyle((new Style()).setColor(TextFormatting.GREEN)));
               } else {
                  sender.sendMessage(new TextComponentString("Hand must contain an item in order to print syntax."));
               }
            } else if ("hotbar".equalsIgnoreCase(args[0])) {
               EntityPlayer player = (EntityPlayer)sender;
               String syntax = "";

               for(int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                  ItemStack stack = player.inventory.getStackInSlot(i);
                  if (stack != null && !stack.isEmpty()) {
                     if (!syntax.isEmpty()) {
                        syntax = syntax.concat("\n");
                     }

                     syntax = syntax.concat(getStringForStackNew(stack));
                  }
               }

               if (!syntax.isEmpty()) {
                  sender.sendMessage((new TextComponentString(syntax)).setStyle((new Style()).setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/clipboard " + syntax))));
                  sender.sendMessage((new TextComponentString("[Copied To Clipboard]")).setStyle((new Style()).setColor(TextFormatting.GREEN)));
               } else {
                  sender.sendMessage(new TextComponentString("Hotbar must contain an item in order to print syntax."));
               }
            } else {
               if (!"block".equalsIgnoreCase(args[0])) {
                  throw new WrongUsageException("bnbgaminglib.cmd.item.usage", new Object[0]);
               }

               EntityPlayer player = (EntityPlayer)sender;
               World world = player.world;
               Vec3d vec3d = player.getPositionEyes(0.0F);
               Vec3d vec3d1 = player.getLook(0.0F);
               Vec3d vec3d2 = vec3d.addVector(vec3d1.x * 4.0D, vec3d1.y * 4.0D, vec3d1.z * 4.0D);
               RayTraceResult result = world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
               if (result != null && result.typeOfHit == Type.BLOCK && result.getBlockPos() != null) {
                  BlockPos blockpos = result.getBlockPos();
                  IBlockState state = world.getBlockState(blockpos);
                  state = state.getActualState(world, blockpos);
                  String syntax = this.getStringForBlock(state, world.getTileEntity(blockpos));
                  sender.sendMessage((new TextComponentString(syntax)).setStyle((new Style()).setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/clipboard " + syntax))));
                  sender.sendMessage((new TextComponentString("[Copied To Clipboard]")).setStyle((new Style()).setColor(TextFormatting.GREEN)));
               } else {
                  sender.sendMessage(new TextComponentString("No block found at mouseover location."));
               }
            }
         }
      } else {
         sender.sendMessage(new TextComponentString("Command can only be used by players!"));
      }

   }

   private static String getStringForStack(ItemStack stack) {
      Item item = stack.getItem();
      int meta = stack.getMetadata();
      NBTTagCompound compound = stack.getTagCompound();
      return compound != null && !compound.hasNoTags() ? "\"" + ((ResourceLocation)Item.REGISTRY.getNameForObject(item)).toString() + "\", " + meta + ", " + stack.getCount() + ", " + compound.toString() : "\"" + ((ResourceLocation)Item.REGISTRY.getNameForObject(item)).toString() + "\", " + meta + ", " + stack.getCount();
   }

   private static String getStringForStackNew(ItemStack stack) {
      Item item = stack.getItem();
      int meta = stack.getMetadata();
      int[] oreIDs = OreDictionary.getOreIDs(stack);
      List<String> ores = new ArrayList();

      for(int ore : oreIDs) {
         ores.add(OreDictionary.getOreName(ore));
      }

      Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
      NBTTagCompound compound = stack.getTagCompound();
      String string = "<";
      string = string.concat(((ResourceLocation)Item.REGISTRY.getNameForObject(item)).toString());
      if (!ores.isEmpty()) {
         string = string.concat(", ore:" + (String)ores.get(0));
      }

      string = string.concat(", meta:" + meta);
      string = string.concat(", count:" + stack.getCount());
      if (item.isDamageable()) {
         string = string.concat(", durability:" + item.getDamage(stack));
      }

      if (!enchantments.isEmpty()) {
         boolean array = enchantments.size() > 1;
         if (array) {
            string = string.concat(", enchant:[");
         } else {
            string = string.concat(", enchant:");
         }

         boolean first = true;

         for(Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
            if (first) {
               string = string.concat(Enchantment.REGISTRY.getNameForObject(enchant.getKey()) + ":" + enchant.getValue());
               first = false;
            } else {
               string = string.concat(", " + Enchantment.REGISTRY.getNameForObject(enchant.getKey()) + ":" + enchant.getValue());
            }
         }

         if (array) {
            string = string.concat("]");
         }
      }

      PotionType potiontype = PotionUtils.getPotionFromItem(stack);
      if (potiontype != null && potiontype != PotionTypes.EMPTY) {
         string = string.concat(", potion:" + PotionType.REGISTRY.getNameForObject(potiontype));
      }

      if (compound != null && !compound.hasNoTags()) {
         string = string.concat(", nbt:" + compound.toString());
      }

      return string.concat(">");
   }

   private String getStringForBlock(IBlockState state, TileEntity tile) {
      String string = "<";
      Block block = state.getBlock();
      Item item = Item.getItemFromBlock(block);
      List<String> ores = new ArrayList();
      if (item != Items.AIR) {
         ItemStack stack = new ItemStack(item, 1, block.damageDropped(state));
         int[] oreIDs = OreDictionary.getOreIDs(stack);

         for(int ore : oreIDs) {
            ores.add(OreDictionary.getOreName(ore));
         }
      }

      string = string.concat(((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString());
      if (!ores.isEmpty()) {
         string = string.concat(", ore:" + (String)ores.get(0));
      }

      string = string.concat(", meta:" + block.getMetaFromState(state));
      Map<String, String> stateMap = this.buildStateMap(state);
      if (!stateMap.isEmpty()) {
         JsonObject stateJson = new JsonObject();

         for(Entry<String, String> entry : stateMap.entrySet()) {
            stateJson.addProperty((String)entry.getKey(), (String)entry.getValue());
         }

         String stateString = this.gson.toJson(stateJson);
         string = string.concat(", state:" + stateString);
      }

      if (tile != null) {
         NBTTagCompound tileNBT = tile.serializeNBT();
         string = string.concat(", tile:" + tileNBT.toString());
      }

      return string.concat(">");
   }

   private <T extends Comparable<T>> Map<String, String> buildStateMap(IBlockState state) {
      Map<String, String> stateMap = new LinkedHashMap();
      UnmodifiableIterator unmodifiableiterator = state.getProperties().entrySet().iterator();

      while(unmodifiableiterator.hasNext()) {
         Entry<IProperty<?>, Comparable<?>> entry = (Entry)unmodifiableiterator.next();
         IProperty<T> iproperty = (IProperty)entry.getKey();
         T t = (T)entry.getValue();
         String s = iproperty.getName(t);
         stateMap.put(iproperty.getName(), s);
      }

      return stateMap;
   }

   public int compareTo(ICommand arg0) {
      return 0;
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return true;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"hand", "hotbar", "block"}) : Collections.emptyList();
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }
}
