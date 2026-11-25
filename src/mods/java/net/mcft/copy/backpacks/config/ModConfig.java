package net.mcft.copy.backpacks.config;

import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.misc.BackpackSize;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

@Config(
   modid = "wearablebackpacks"
)
public class ModConfig {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ModConfig.ServerConfig server = new ModConfig.ServerConfig();
   @Comment({"Client Config"})
   @Name("Client")
   public static final ModConfig.ClientConfig client = new ModConfig.ClientConfig();

   public ModConfig() {
   }

   public static class ClientConfig {
      @Comment({"Controls the opacity / visibility of the enchantment effect on equipped and placed backpacks, if present. Default: 80%."})
      @Name("Enchant Effect Opacity")
      @LangKey("config.wearablebackpacks.cosmetic.enchantEffectOpacity")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double enchantEffectOpacity = 0.8D;

      public ClientConfig() {
      }
   }

   @EventBusSubscriber(
      modid = "wearablebackpacks"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("wearablebackpacks")) {
            ModConfig.server.resetCache();
            ConfigManager.sync("wearablebackpacks", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      private BackpackSize backpackSize;
      private Potion backpackEffect;
      @Comment({"Whether or not the backpack should be equipped as a Bauble."})
      @Name("Equip Backpack as Bauble")
      @LangKey("config.wearablebackpacks.server.equipAsBauble")
      public boolean equipAsBauble = true;
      @Comment({"If enabled, allows equipped backpacks to be opened by other players by right clicking the target's back."})
      @Name("Enable Equipped Interaction")
      @LangKey("config.wearablebackpacks.general.enableEquippedInteraction")
      public boolean enableEquippedInteraction = true;
      @Comment({"If enabled, allows players to open their own equipped backpack without requiring it to be placed first using a keybind."})
      @Name("Enable Self Interaction")
      @LangKey("config.wearablebackpacks.general.enableSelfInteraction")
      public boolean enableSelfInteraction = false;
      @Comment({"If enabled, allows machines to interact with placed backpacks."})
      @Name("Enable Machine Interaction")
      @LangKey("config.wearablebackpacks.general.enableMachineInteraction")
      public boolean enableMachineInteraction = false;
      @Comment({"If enabled, places equipped backpacks as a block on death, instead of scattering the items all around."})
      @Name("Drop as Block on Death")
      @LangKey("config.wearablebackpacks.general.dropAsBlockOnDeath")
      public boolean dropAsBlockOnDeath = true;
      @Comment({"Should backpack placement and breaking attempt to ignore event cancellation? (Patches Lycanite's boss arena voiding backpacks)"})
      @Name("Backpack Ignore Cancellation")
      @LangKey("config.wearablebackpacks.general.ignoreCancellation")
      public boolean ignoreCancellation = false;
      @Comment({"Column size of a normal backpack. Does not affect placed or equipped backpacks until turned back into an item."})
      @Name("Backpack Size Column")
      @LangKey("config.wearablebackpacks.size.sizeColumn")
      @RangeInt(
         min = 1,
         max = 17
      )
      public int backpackSizeColumn = 9;
      @Comment({"Row size of a normal backpack. Does not affect placed or equipped backpacks until turned back into an item."})
      @Name("Backpack Size Row")
      @LangKey("config.wearablebackpacks.size.sizeRow")
      @RangeInt(
         min = 1,
         max = 6
      )
      public int backpackSizeRow = 4;
      @Comment({"Controls whether mobs spawned naturally can randomly be wearing backpacks."})
      @Name("Natural Mobs Spawn With Backpacks")
      @LangKey("config.wearablebackpacks.entity.spawnNaturally")
      public boolean spawnNaturally = true;
      @Comment({"Controls whether mobs spawned from spawners can randomly be wearing backpacks."})
      @Name("Spawned Mobs Spawn With Backpacks")
      @LangKey("config.wearablebackpacks.entity.spawnFromSpawners")
      public boolean spawnFromSpawners = false;
      @Comment({"Whether or not to apply a potion effect to the player while wearing a backpack."})
      @Name("Apply Potion Effect")
      @LangKey("config.wearablebackpacks.effect.doEffect")
      public boolean doPotionEffect = false;
      @Comment({"Amplifier of the potion effect to be applied to the player while wearing a backpack."})
      @Name("Potion Effect Amplifier")
      @LangKey("config.wearablebackpacks.effect.effectAmplifier")
      @RangeInt(
         min = 0,
         max = 10
      )
      public int potionEffectAmplifier = 0;
      @Comment({"The Resourcelocation of the potion effect to be applied on the player while wearing a backpack. (Ex. minecraft:weakness)"})
      @Name("Potion Effect Name")
      @LangKey("config.wearablebackpacks.effect.effectName")
      public String potionEffectName = "";

      public ServerConfig() {
      }

      public void resetCache() {
         this.backpackSize = null;
         this.backpackEffect = null;
      }

      public BackpackSize getBackpackSize() {
         if (this.backpackSize == null) {
            this.backpackSize = new BackpackSize(this.backpackSizeColumn, this.backpackSizeRow);
         }

         return this.backpackSize;
      }

      public Potion getBackpackEffect() {
         if (this.backpackEffect == null) {
            this.backpackEffect = Potion.getPotionFromResourceLocation(this.potionEffectName);
            if (this.backpackEffect == null) {
               WearableBackpacks.LOG.log(Level.WARN, "WearableBackpacksRLCraft: Invalid potion effect in config, returning weakness.");
               this.backpackEffect = MobEffects.WEAKNESS;
            }
         }

         return this.backpackEffect;
      }
   }
}
