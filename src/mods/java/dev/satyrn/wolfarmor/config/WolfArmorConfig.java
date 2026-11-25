package dev.satyrn.wolfarmor.config;

import com.google.common.collect.Maps;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.config.settings.BooleanSetting;
import dev.satyrn.wolfarmor.api.config.settings.Setting;
import dev.satyrn.wolfarmor.common.network.packets.ConfigSyncMessage;
import dev.satyrn.wolfarmor.config.settings.WolfFoodStatsSetting;
import dev.satyrn.wolfarmor.config.settings.WolfInventorySizeSetting;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import dev.satyrn.wolfarmor.util.WolfInventorySize;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WolfArmorConfig {
   private final Setting<Boolean> chestEnabled = (new BooleanSetting(true)).setCategory("general").setName("backpack").setComment("Enables or disables wolf backpacks.").setRequiresWorldReload().setSynchronizes(true);
   private final Setting<WolfInventorySize> chestSize = (new WolfInventorySizeSetting(new WolfInventorySize((byte)3, (byte)2))).setCategory("general").setName("backpack_size").setComment("Sets the horizontal and vertical size of wolves' backpacks.").setSynchronizes(true).setRequiresWorldReload().setConfigWidgetClassName("dev.satyrn.wolfarmor.client.gui.config.WolfInventorySizeWidget");
   private final Setting<Boolean> enableRecipes = (new BooleanSetting(true)).setCategory("general").setName("allow_crafting").setComment("Allows or disallows players to craft the wolf armor items.").setRequiresMinecraftRestart();
   public WolfArmorConfig.BehaviorCategory behavior;
   @SideOnly(Side.CLIENT)
   public WolfArmorConfig.ClientCategory client;
   private Configuration configuration;
   private final Map<String, Setting<?>> settings = Maps.newLinkedHashMap();
   @SideOnly(Side.CLIENT)
   private boolean connected;

   public WolfArmorConfig() {
   }

   private void addSettingsFromClass(Object object) {
      for(Field field : object.getClass().getDeclaredFields()) {
         if (Setting.class.isAssignableFrom(field.getType())) {
            Setting<?> setting;
            try {
               setting = (Setting)field.get(object);
            } catch (IllegalAccessException var8) {
               throw new RuntimeException(var8);
            }

            this.settings.put(setting.getFullName(), setting);
         }
      }

   }

   public Collection<Setting<?>> getSettings() {
      return this.settings.values();
   }

   public Setting<?> getSetting(String fullName) {
      return (Setting)this.settings.get(fullName);
   }

   public void load() {
      if (this.settings.size() != 0) {
         this.configuration.load();
         this.getSettings().forEach((setting) -> {
            setting.loadFromConfiguration(this.configuration);
         });
      }
   }

   public void save() {
      this.getSettings().forEach((setting) -> {
         setting.saveToConfiguration(this.configuration);
      });
      this.configuration.save();
   }

   public boolean getChestEnabled() {
      return ((Boolean)this.chestEnabled.getCurrentValue()).booleanValue();
   }

   public boolean getHowlAtMoonEnabled() {
      return ((Boolean)this.behavior.howlAtMoonEnabled.getCurrentValue()).booleanValue();
   }

   public boolean getAutoHealEnabled() {
      return ((Boolean)this.behavior.autoHealEnabled.getCurrentValue()).booleanValue();
   }

   @SideOnly(Side.CLIENT)
   public boolean getArmorRendered() {
      return ((Boolean)this.client.armorRendered.getCurrentValue()).booleanValue();
   }

   public boolean getChestRendered() {
      return ((Boolean)this.client.chestRendered.getCurrentValue()).booleanValue();
   }

   public WolfInventorySize getChestSize() {
      return this.chestSize.getCurrentValue();
   }

   public WolfFoodStatsLevel getFoodStatsLevel() {
      return this.behavior.useFoodStats.getCurrentValue();
   }

   public boolean getCanStarve() {
      return ((Boolean)this.behavior.wolvesCanStarve.getCurrentValue()).booleanValue();
   }

   @SideOnly(Side.CLIENT)
   public boolean getStatsRendered() {
      return ((Boolean)this.client.wolfStatsRendered.getCurrentValue()).booleanValue();
   }

   @SideOnly(Side.CLIENT)
   public boolean getStatsInGui() {
      return ((Boolean)this.client.statsInGui.getCurrentValue()).booleanValue();
   }

   public boolean getEnableCrafting() {
      return ((Boolean)this.enableRecipes.getCurrentValue()).booleanValue();
   }

   public void initialize(String configDirectory) {
      File directory = new File(configDirectory);
      if (!directory.exists()) {
         WolfArmorMod.getLogger().debug(String.format("Creating new configuration directory '%s'", configDirectory));
         if (!directory.mkdirs()) {
            WolfArmorMod.getLogger().error("Unable to create new configuration directory!");
            throw new RuntimeException("Failed to create configuration directory");
         }
      }

      File configFile = new File(directory.getPath() + "/" + "wolfarmor" + ".cfg");
      if (!configFile.exists()) {
         WolfArmorMod.getLogger().debug("Config file not found; a new configuration file will be initialized.");
      }

      this.configuration = new Configuration(configFile);
      this.addSettingsFromClass(this);

      for(Field field : this.getClass().getDeclaredFields()) {
         if (field.getType().getName().endsWith("Category")) {
            try {
               field.set(this, field.getType().getConstructors()[0].newInstance());
               this.addSettingsFromClass(field.get(this));
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException var9) {
               throw new RuntimeException(var9);
            }
         }
      }

      this.load();
      if (this.configuration.hasChanged()) {
         this.save();
      }

   }

   @SideOnly(Side.CLIENT)
   public void sync(@Nonnull NBTTagCompound data) {
      if (this.connected) {
         for(String key : data.getKeySet()) {
            NBTBase tag = data.getTag(key);
            Setting<?> setting = this.getSetting(key);
            if (setting != null && setting.getSynchronizes()) {
               setting.readSynchronized(tag);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onClientConnect(ClientConnectedToServerEvent event) {
      this.connected = true;
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
      if (this.connected) {
         this.getSettings().forEach(Setting::onDisconnect);
         this.connected = false;
      }
   }

   @SubscribeEvent
   public void onConfigChanged(@Nonnull OnConfigChangedEvent event) {
      if (event.getModID().equals("wolfarmor")) {
         if (event.isWorldRunning()) {
            WolfArmorMod.getNetworkChannel().sendToAllWhere(ConfigSyncMessage.create(), WolfArmorConfig::isNotServerOwner);
         }

         this.save();
      }
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      if (!isNotServerOwner(event.player) && event.player instanceof EntityPlayerMP) {
         WolfArmorMod.getNetworkChannel().sendTo(ConfigSyncMessage.create(), (EntityPlayerMP)event.player);
      }
   }

   private static boolean isNotServerOwner(EntityPlayer player) {
      return player.getName().equalsIgnoreCase(FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner());
   }

   public static final class BehaviorCategory {
      private static final String CATEGORY = "behavior";
      final Setting<Boolean> howlAtMoonEnabled = (new BooleanSetting(false)).setCategory("behavior").setName("howl_at_moon").setComment("Enables or disables untamed wolves howling at the moon").setSynchronizes(true);
      final Setting<Boolean> autoHealEnabled = (new BooleanSetting(true)).setCategory("behavior").setName("auto_eat").setComment("Enables or disables wolves automatically eating food when their health is low.").setSynchronizes(true);
      final Setting<WolfFoodStatsLevel> useFoodStats = (new WolfFoodStatsSetting()).setCategory("behavior").setName("hunger").setComment("Sets whether or not to enable hunger for wolves. Valid values are disabled, heal, and full. \"Heal\" will override the default heal functionality and heal wolves w/ saturation. \"Full\" will cause unfed wolves to lose health until 4 hearts are left, or, if starvation is enabled, until it dies.").setRequiresWorldReload().setSynchronizes(true);
      final Setting<Boolean> wolvesCanStarve = (new BooleanSetting(false)).setCategory("behavior").setName("starvation").setComment("Sets whether or not wolves can starve to death if hunger is set to \"full\".").setRequiresWorldReload().setSynchronizes(true);

      public BehaviorCategory() {
      }
   }

   @SideOnly(Side.CLIENT)
   public static final class ClientCategory {
      private static final String CATEGORY = "client";
      final Setting<Boolean> armorRendered = (new BooleanSetting(true)).setCategory("client").setName("armor_model").setComment("Enables or disables wolf armor model rendering. Disable this if you have an unsupported  custom model mod, or if rendering the armor causes other issues.");
      final Setting<Boolean> chestRendered = (new BooleanSetting(true)).setCategory("client").setName("backpack_model").setComment("Enables or disables wolf backpack model rendering. Disable if you have an unsupported custom model mod, or if rendering the backpack causes other issues.");
      final Setting<Boolean> statsInGui = (new BooleanSetting(true)).setCategory("client").setName("stats_in_gui").setComment("Enables or disables displaying the wolf health, armor, and food stats in the inventory");
      final Setting<Boolean> wolfStatsRendered = (new BooleanSetting(false)).setCategory("client").setName("stats_in_nameplate").setComment("Enables or disables displaying health, armor, and food stats above tamed wolves' heads.");

      public ClientCategory() {
      }
   }
}
