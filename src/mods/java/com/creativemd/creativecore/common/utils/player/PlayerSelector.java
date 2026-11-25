package com.creativemd.creativecore.common.utils.player;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.gui.GuiPlayerSelectorButton;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.utils.mc.PlayerUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class PlayerSelector {
   private static HashMap<String, Class<? extends PlayerSelector>> selectorTypes = new HashMap();

   public PlayerSelector() {
   }

   public static void registerSelectorType(String id, Class<? extends PlayerSelector> clazz) {
      selectorTypes.put(id, clazz);
   }

   public static Class<? extends PlayerSelector> get(String id) {
      return (Class)selectorTypes.get(id);
   }

   public static String get(Class<? extends PlayerSelector> clazz) {
      for(Entry<String, Class<? extends PlayerSelector>> entry : selectorTypes.entrySet()) {
         if (entry.getValue() == clazz) {
            return (String)entry.getKey();
         }
      }

      throw new RuntimeException("Could not find player selector id for " + clazz);
   }

   public static PlayerSelector read(NBTTagCompound nbt) {
      Class<? extends PlayerSelector> clazz = get(nbt.getString("id"));
      if (clazz == null) {
         throw new RuntimeException("Could not find player selector for " + nbt.getString("id"));
      } else {
         try {
            PlayerSelector selector = (PlayerSelector)clazz.getConstructor().newInstance();
            selector.readFromNBT(nbt);
            return selector;
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var3) {
            throw new RuntimeException("PlayerSelector type " + nbt.getString("id") + " does not have an empty constructor!");
         }
      }
   }

   public abstract boolean is(EntityPlayer var1);

   public abstract void readFromNBT(NBTTagCompound var1);

   protected abstract void write(NBTTagCompound var1);

   public abstract String info();

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      this.write(nbt);
      nbt.setString("id", get(this.getClass()));
      return nbt;
   }

   static {
      registerSelectorType("and", PlayerSelector.PlayerSelectorAnd.class);
      registerSelectorType("or", PlayerSelector.PlayerSelectorOr.class);
      registerSelectorType("not", PlayerSelector.PlayerSelectorNot.class);
      registerSelectorType("level", PlayerSelector.PlayerSelectorLevel.class);
      registerSelectorType("mode", PlayerSelector.PlayerSelectorGamemode.class);
      registerSelectorType("command", PlayerSelector.PlayerSelectorCommand.class);
      registerSelectorType("selector", PlayerSelector.PlayerSelectorCommandSelector.class);
      ConfigTypeConveration.registerSpecialType((x) -> {
         return PlayerSelector.class.isAssignableFrom(x);
      }, new ConfigTypeConveration.SimpleConfigTypeConveration<PlayerSelector>() {
         public PlayerSelector readElement(PlayerSelector defaultValue, boolean loadDefault, JsonElement element) {
            if (element.isJsonPrimitive() && ((JsonPrimitive)element).isString()) {
               try {
                  return PlayerSelector.read(JsonToNBT.getTagFromJson(element.getAsString()));
               } catch (NBTException var5) {
                  var5.printStackTrace();
               }
            }

            return defaultValue;
         }

         public JsonElement writeElement(PlayerSelector value, PlayerSelector defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value.writeToNBT(new NBTTagCompound()).toString());
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            parent.addControl(new GuiPlayerSelectorButton("data", 0, 0, Math.min(150, parent.width - 50), 14, new PlayerSelector.PlayerSelectorLevel(0)));
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(PlayerSelector value, GuiParent parent) {
            GuiPlayerSelectorButton button = (GuiPlayerSelectorButton)parent.get("data");
            button.set(value);
         }

         @SideOnly(Side.CLIENT)
         protected PlayerSelector saveValue(GuiParent parent, Class clazz) {
            GuiPlayerSelectorButton button = (GuiPlayerSelectorButton)parent.get("data");
            return button.get();
         }

         public PlayerSelector set(ConfigKey.ConfigKeyField key, PlayerSelector value) {
            return value;
         }
      });
   }

   public static class PlayerSelectorAnd extends PlayerSelector {
      public PlayerSelector[] selectors;

      public PlayerSelectorAnd() {
      }

      public PlayerSelectorAnd(PlayerSelector... selector) {
         this.selectors = selector;
      }

      public boolean is(EntityPlayer player) {
         for(int i = 0; i < this.selectors.length; ++i) {
            if (!this.selectors[i].is(player)) {
               return false;
            }
         }

         return true;
      }

      public void readFromNBT(NBTTagCompound nbt) {
         NBTTagList list = nbt.getTagList("selectors", 10);
         this.selectors = new PlayerSelector[list.tagCount()];

         for(int i = 0; i < this.selectors.length; ++i) {
            this.selectors[i] = PlayerSelector.read(list.getCompoundTagAt(i));
         }

      }

      protected void write(NBTTagCompound nbt) {
         NBTTagList list = new NBTTagList();

         for(int i = 0; i < this.selectors.length; ++i) {
            list.appendTag(this.selectors[i].writeToNBT(new NBTTagCompound()));
         }

         nbt.setTag("selectors", list);
      }

      public String info() {
         String text = "[";

         for(int i = 0; i < this.selectors.length; ++i) {
            if (i > 0) {
               text = text + "&";
            }

            text = text + this.selectors[i].info();
         }

         return text + "]";
      }
   }

   public static class PlayerSelectorCommand extends PlayerSelector {
      public String command;

      public PlayerSelectorCommand() {
      }

      public PlayerSelectorCommand(String command) {
         this.command = command;
      }

      public boolean is(EntityPlayer player) {
         if (player.getServer() == null) {
            return false;
         } else {
            ICommand command = (ICommand)player.getServer().getCommandManager().getCommands().get(this.command);
            return command != null ? command.checkPermission(player.getServer(), player) : false;
         }
      }

      public void readFromNBT(NBTTagCompound nbt) {
         this.command = nbt.getString("command");
      }

      protected void write(NBTTagCompound nbt) {
         nbt.setString("command", this.command);
      }

      public String info() {
         return "/" + this.command;
      }
   }

   public static class PlayerSelectorCommandSelector extends PlayerSelector {
      public String pattern;

      public PlayerSelectorCommandSelector() {
      }

      public PlayerSelectorCommandSelector(String pattern) {
         this.pattern = pattern;
      }

      public boolean is(EntityPlayer player) {
         try {
            return EntitySelector.getPlayers(player, this.pattern).contains(player);
         } catch (CommandException var3) {
            return false;
         }
      }

      public void readFromNBT(NBTTagCompound nbt) {
         this.pattern = nbt.getString("pattern");
      }

      protected void write(NBTTagCompound nbt) {
         nbt.setString("pattern", this.pattern);
      }

      public String info() {
         return this.pattern;
      }
   }

   public static class PlayerSelectorGamemode extends PlayerSelector {
      public GameType type;

      public PlayerSelectorGamemode() {
      }

      public PlayerSelectorGamemode(GameType type) {
         this.type = type;
      }

      public boolean is(EntityPlayer player) {
         return PlayerUtils.getGameType(player) == this.type;
      }

      public void readFromNBT(NBTTagCompound nbt) {
         this.type = GameType.getByID(nbt.getInteger("mode"));
      }

      protected void write(NBTTagCompound nbt) {
         nbt.setInteger("mode", this.type.getID());
      }

      public String info() {
         return this.type.getName();
      }
   }

   public static class PlayerSelectorLevel extends PlayerSelector {
      public int permissionLevel;

      public PlayerSelectorLevel() {
      }

      public PlayerSelectorLevel(int permissionLevel) {
         this.permissionLevel = permissionLevel;
      }

      public boolean is(EntityPlayer player) {
         if (player instanceof EntityPlayerMP) {
            UserListOpsEntry entry = (UserListOpsEntry)player.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
            if (entry != null) {
               return entry.getPermissionLevel() >= this.permissionLevel;
            } else {
               return player.getServer().getOpPermissionLevel() >= this.permissionLevel;
            }
         } else {
            return true;
         }
      }

      public void readFromNBT(NBTTagCompound nbt) {
         this.permissionLevel = nbt.getInteger("level");
      }

      protected void write(NBTTagCompound nbt) {
         nbt.setInteger("level", this.permissionLevel);
      }

      public String info() {
         return "level>=" + this.permissionLevel;
      }
   }

   public static class PlayerSelectorNot extends PlayerSelector {
      public PlayerSelector selector;

      public PlayerSelectorNot() {
      }

      public PlayerSelectorNot(PlayerSelector selector) {
         this.selector = selector;
      }

      public boolean is(EntityPlayer player) {
         return !this.selector.is(player);
      }

      public void readFromNBT(NBTTagCompound nbt) {
         this.selector = PlayerSelector.read(nbt.getCompoundTag("child"));
      }

      protected void write(NBTTagCompound nbt) {
         nbt.setTag("child", this.selector.writeToNBT(new NBTTagCompound()));
      }

      public String info() {
         return "!" + this.selector.info();
      }
   }

   public static class PlayerSelectorOr extends PlayerSelector {
      public PlayerSelector[] selectors;

      public PlayerSelectorOr() {
      }

      public PlayerSelectorOr(PlayerSelector... selector) {
         this.selectors = selector;
      }

      public boolean is(EntityPlayer player) {
         for(int i = 0; i < this.selectors.length; ++i) {
            if (this.selectors[i].is(player)) {
               return true;
            }
         }

         return false;
      }

      public void readFromNBT(NBTTagCompound nbt) {
         NBTTagList list = nbt.getTagList("selectors", 10);
         this.selectors = new PlayerSelector[list.tagCount()];

         for(int i = 0; i < this.selectors.length; ++i) {
            this.selectors[i] = PlayerSelector.read(list.getCompoundTagAt(i));
         }

      }

      protected void write(NBTTagCompound nbt) {
         NBTTagList list = new NBTTagList();

         for(int i = 0; i < this.selectors.length; ++i) {
            list.appendTag(this.selectors[i].writeToNBT(new NBTTagCompound()));
         }

         nbt.setTag("selectors", list);
      }

      public String info() {
         String text = "[";

         for(int i = 0; i < this.selectors.length; ++i) {
            if (i > 0) {
               text = text + "|";
            }

            text = text + this.selectors[i].info();
         }

         return text + "]";
      }
   }
}
