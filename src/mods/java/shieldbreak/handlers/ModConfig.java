package shieldbreak.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import shieldbreak.core.ShieldBreak;
import shieldbreak.util.PotionEntry;

@Config(
   modid = "shieldbreak"
)
public class ModConfig {
   @Comment({"Server Config"})
   @Name("Server")
   @LangKey("config.shieldbreak.server")
   public static final ModConfig.ServerConfig server = new ModConfig.ServerConfig();

   public ModConfig() {
   }

   @EventBusSubscriber(
      modid = "shieldbreak"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("shieldbreak")) {
            ModConfig.server.resetEffectCache();
            ConfigManager.sync("shieldbreak", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      private List<PotionEntry> attackerParry = null;
      private List<PotionEntry> defenderParry = null;
      private List<PotionEntry> attackerBreak = null;
      private List<PotionEntry> defenderBreak = null;
      @Comment({"Minimum amount of damage any shield will withstand before chance of cooldown."})
      @Name("Damage Minimum Threshold")
      @LangKey("config.shieldbreak.server.damageMinimumThreshold")
      public float damageMinimumThreshold = 1.0F;
      @Comment({"Maximum amount of damage any shield will withstand before chance of cooldown."})
      @Name("Damage Maximum Threshold")
      @LangKey("config.shieldbreak.server.damageMaximumThreshold")
      public float damageMaximumThreshold = 20.0F;
      @Comment({"Scaling for how much damage a shield will withstand before chance of cooldown based on max durability. (Durability/This = Damage)"})
      @Name("Damage Durability Scaling")
      @LangKey("config.shieldbreak.server.damageDurabilityScaling")
      public float damageDurabilityScaling = 100.0F;
      @Comment({"Minimum amount of cooldown ticks a shield break will cause."})
      @Name("Cooldown Minimum Ticks")
      @LangKey("config.shieldbreak.server.cooldownTicksMinimum")
      public int cooldownTicksMinimum = 10;
      @Comment({"Maximum amount of cooldown ticks a shield break will cause."})
      @Name("Cooldown Maximum Ticks")
      @LangKey("config.shieldbreak.server.cooldownTicksMaximum")
      public int cooldownTicksMaximum = 200;
      @Comment({"Multiplier for cooldown ticks per point of damage."})
      @Name("Cooldown Ticks Scaling")
      @LangKey("config.shieldbreak.server.cooldownTicksScaling")
      public float cooldownTicksScaling = 10.0F;
      @Comment({"Range for how long the shield can be held up and still parry the attack. (Counted after the initial shield delay, if any)"})
      @Name("Parry Tick Range")
      @LangKey("config.shieldbreak.server.parryTickRange")
      public int parryTickRange = 10;
      @Comment({"How much durability a shield should have for the purpose of scaling if it's max durability is 0."})
      @Name("Shield Durability Fallback")
      @LangKey("config.shieldbreak.server.unbreakableShieldDurability")
      public int unbreakableShieldDurability = 3000;
      @Comment({"How many ticks should it take when raising a shield for it to become active."})
      @Name("Shield Raise Tick Delay")
      @LangKey("config.shieldbreak.server.shieldRaiseTickDelay")
      public int shieldRaiseTickDelay = 5;
      @Comment({"Base chance for a weapon that can break shields to ignore shield protection and cause a cooldown. (Ex. Axe and Halberd)"})
      @Name("Shield Bypass Chance")
      @LangKey("config.shieldbreak.server.shieldBypassChance")
      public float shieldBypassChance = 0.25F;
      @Comment({"How long should the cooldown be if a weapon that can break shields bypasses the shield. (Ex. Axe and Halberd)"})
      @Name("Shield Bypass Cooldown")
      @LangKey("config.shieldbreak.server.shieldBypassCooldown")
      public int shieldBypassCooldown = 100;
      @Comment({"Always reset active hand after shield hit? (Fixes exploit with using handheld gui's to attack while shielding)"})
      @Name("Always Reset Active Hand")
      @LangKey("config.shieldbreak.server.alwaysResetActiveHand")
      public boolean alwaysResetActiveHand = true;
      @Comment({"Potion Effects to apply to an attacker on a parry. (Potion,Duration,Amplifier)"})
      @Name("Potion Effect Attacker Parry")
      @LangKey("config.shieldbreak.server.potionEffectAttackerParry")
      public String[] potionEffectAttackerParry = new String[]{""};
      @Comment({"Potion Effects to apply to a defender on a parry. (Potion,Duration,Amplifier)"})
      @Name("Potion Effect Defender Parry")
      @LangKey("config.shieldbreak.server.potionEffectDefenderParry")
      public String[] potionEffectDefenderParry = new String[]{""};
      @Comment({"Potion Effects to apply to an attacker on a break. (Potion,Duration,Amplifier)"})
      @Name("Potion Effect Attacker Break")
      @LangKey("config.shieldbreak.server.potionEffectAttackerBreak")
      public String[] potionEffectAttackerBreak = new String[]{""};
      @Comment({"Potion Effects to apply to a defender on a break. (Potion,Duration,Amplifier)"})
      @Name("Potion Effect Defender Break")
      @LangKey("config.shieldbreak.server.potionEffectDefenderBreak")
      public String[] potionEffectDefenderBreak = new String[]{""};
      @Comment({"Knockback power on parry."})
      @Name("Knockback Parry")
      @LangKey("config.shieldbreak.server.knockbackParry")
      public float knockbackParry = 1.0F;
      @Comment({"Knockback power on normal block."})
      @Name("Knockback Normal")
      @LangKey("config.shieldbreak.server.knockbackNormal")
      public float knockbackNormal = 0.5F;
      @Comment({"Knockback power on break."})
      @Name("Knockback Break")
      @LangKey("config.shieldbreak.server.knockbackBreak")
      public float knockbackBreak = 0.25F;

      public ServerConfig() {
      }

      public void resetEffectCache() {
         this.attackerParry = null;
         this.attackerBreak = null;
         this.defenderParry = null;
         this.defenderBreak = null;
      }

      @Nullable
      public List<PotionEntry> getEffectAttackerParry() {
         if (this.attackerParry != null) {
            return this.attackerParry;
         } else if (this.potionEffectAttackerParry.length > 0) {
            this.attackerParry = this.getPotionEffectFromString(this.potionEffectAttackerParry);
            return this.attackerParry;
         } else {
            return null;
         }
      }

      @Nullable
      public List<PotionEntry> getEffectDefenderParry() {
         if (this.defenderParry != null) {
            return this.defenderParry;
         } else if (this.potionEffectDefenderParry.length > 0) {
            this.defenderParry = this.getPotionEffectFromString(this.potionEffectDefenderParry);
            return this.defenderParry;
         } else {
            return null;
         }
      }

      @Nullable
      public List<PotionEntry> getEffectAttackerBreak() {
         if (this.attackerBreak != null) {
            return this.attackerBreak;
         } else if (this.potionEffectAttackerBreak.length > 0) {
            this.attackerBreak = this.getPotionEffectFromString(this.potionEffectAttackerBreak);
            return this.attackerBreak;
         } else {
            return null;
         }
      }

      @Nullable
      public List<PotionEntry> getEffectDefenderBreak() {
         if (this.defenderBreak != null) {
            return this.defenderBreak;
         } else if (this.potionEffectDefenderBreak.length > 0) {
            this.defenderBreak = this.getPotionEffectFromString(this.potionEffectDefenderBreak);
            return this.defenderBreak;
         } else {
            return null;
         }
      }

      @Nullable
      private List<PotionEntry> getPotionEffectFromString(String[] entryList) {
         try {
            List<PotionEntry> returnable = new ArrayList();

            for(String entryUnclean : entryList) {
               if (!entryUnclean.isEmpty()) {
                  String[] entry = this.cleanEntry(entryUnclean);
                  Potion potion = Potion.getPotionFromResourceLocation(entry[0]);
                  if (potion == null) {
                     ShieldBreak.logger.log(Level.WARN, "Invalid potion name: " + entry[0]);
                  } else {
                     int duration = Integer.parseInt(entry[1]);
                     int amplifier = Integer.parseInt(entry[2]);
                     returnable.add(new PotionEntry(potion, duration, amplifier));
                  }
               }
            }

            return returnable.isEmpty() ? null : returnable;
         } catch (Exception var11) {
            ShieldBreak.logger.log(Level.WARN, "Failed to parse config entry: " + var11);
            return null;
         }
      }

      private String[] cleanEntry(String entry) {
         return (String[])Arrays.stream(entry.split(",")).map(String::trim).toArray((x$0) -> {
            return new String[x$0];
         });
      }
   }
}
