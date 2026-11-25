package se.gory_moon.globalgamerules;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.globalgamerules.config.GGRConfig;
import se.gory_moon.globalgamerules.reference.Reference;

@EventBusSubscriber(
   modid = "globalgamerules"
)
public class WorldEvents {
   public WorldEvents() {
   }

   @SubscribeEvent
   public static void onWorldJoin(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
         World world = event.getWorld();
         GameRules gRules = world.getGameRules();
         EntityPlayer playerMP = (EntityPlayer)event.getEntity();
         GlobalGR.getConfig().custom.forEach((s, stringValuePair) -> {
            if (gRules.getBoolean(s)) {
               String command = (String)stringValuePair.getKey();
               command = command.replaceAll("@p", playerMP.getName());
               MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
               server.commandManager.executeCommand(server, command);
            }

         });
      }

   }

   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      World world = event.getWorld();
      WorldInfo info = world.getWorldInfo();
      GameRules gRules = world.getGameRules();
      Reference.logger.info("Applying config gamerules to dimension {} ({})", Integer.valueOf(world.provider.getDimension()), info.getWorldName());
      GlobalGR.getConfig().rules.forEach((s, value) -> {
         gRules.setOrCreateGameRule(s, value.getStringValue());
      });
      GlobalGR.getConfig().custom.forEach((s, stringValuePair) -> {
         gRules.setOrCreateGameRule(s, ((GGRConfig.Value)stringValuePair.getValue()).getStringValue());
      });
      if (!event.getWorld().isRemote && !info.isDifficultyLocked()) {
         int diff = ((GGRConfig.Value)GlobalGR.getConfig().misc.get("worldDifficulty")).getIntegerValue();
         if (diff != -1) {
            info.setDifficulty(EnumDifficulty.getDifficultyEnum(diff));
            Reference.logger.info("Setting difficulty of dimension {} ({}) to {}", Integer.valueOf(world.provider.getDimension()), info.getWorldName(), EnumDifficulty.getDifficultyEnum(diff).toString());
         }

         if (((GGRConfig.Value)GlobalGR.getConfig().misc.get("worldDifficultyLocked")).getBooleanValue()) {
            info.setDifficultyLocked(true);
            Reference.logger.info("Locking difficulty of dimension {} ({})", Integer.valueOf(world.provider.getDimension()), info.getWorldName());
         }
      }

   }

   @SubscribeEvent
   public static void onWorldUnLoad(Unload event) {
      World world = event.getWorld();
      WorldInfo info = world.getWorldInfo();
      GameRules gRules = event.getWorld().getGameRules();
      if (((GGRConfig.Value)GlobalGR.getConfig().misc.get("saveGameRules")).getBooleanValue()) {
         Reference.logger.info("Saving gamerules of dimension {} ({}) to config", Integer.valueOf(world.provider.getDimension()), info.getWorldName());
         Arrays.stream(gRules.getRules()).filter((s) -> {
            return !GlobalGR.getConfig().custom.containsKey(s);
         }).forEach((s) -> {
            GGRConfig.Value var10000 = (GGRConfig.Value)GlobalGR.getConfig().rules.put(s, new GGRConfig.Value(gRules.getString(s), getType(gRules, s)));
         });
      }

      GlobalGR.getConfig().custom.forEach((s, stringValuePair) -> {
         Pair var10000 = (Pair)GlobalGR.getConfig().custom.put(s, MutablePair.of(stringValuePair.getLeft(), new GGRConfig.Value(gRules.getString(s), ((GGRConfig.Value)stringValuePair.getRight()).getType())));
      });
      if (!event.getWorld().isRemote && ((GGRConfig.Value)GlobalGR.getConfig().misc.get("worldDifficulty")).getIntegerValue() != -1 && !event.getWorld().getWorldInfo().isDifficultyLocked()) {
         GGRConfig.Value val = (GGRConfig.Value)GlobalGR.getConfig().misc.get("worldDifficulty");
         GGRConfig.Value newVal = new GGRConfig.Value(String.valueOf(info.getDifficulty().getDifficultyId()), val.getType(), val.getShowInGui());
         GlobalGR.getConfig().misc.put("worldDifficulty", newVal);
      }

      if (!((GGRConfig.Value)GlobalGR.getConfig().misc.get("saveGameRules")).getBooleanValue()) {
         Arrays.stream(gRules.getRules()).filter((s) -> {
            return !GlobalGR.getConfig().rules.containsKey(s);
         }).forEach((s) -> {
            GGRConfig.Value var10000 = (GGRConfig.Value)GlobalGR.getConfig().rules.put(s, new GGRConfig.Value(gRules.getString(s), getType(gRules, s)));
         });
      }

      GlobalGR.getConfig().saveConfig();
   }

   private static GGRConfig.ValueType getType(GameRules rules, String s) {
      if (rules.areSameType(s, ValueType.BOOLEAN_VALUE)) {
         return GGRConfig.ValueType.BOOLEAN;
      } else {
         return rules.areSameType(s, ValueType.NUMERICAL_VALUE) ? GGRConfig.ValueType.INTEGER : GGRConfig.ValueType.STRING;
      }
   }
}
