package shadows.placebo.patreon;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import shadows.placebo.Placebo;

@EventBusSubscriber(
   modid = "placebo",
   value = {Side.CLIENT}
)
public class PatreonManager {
   public static final Int2ObjectMap<Consumer<String>> DATA_PARSERS = new Int2ObjectOpenHashMap();
   public static final Map<EaglercraftUUID, EnumParticleTypes> PATREONS = new HashMap();

   public PatreonManager() {
   }

   @SubscribeEvent
   public static void init(ModelRegistryEvent e) {
      (new Thread(() -> {
         Placebo.LOG.info("Loading patreon data...");

         try {
            URL url = new URL("https://raw.githubusercontent.com/Shadows-of-Fire/Placebo/master/PatreonInfo.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            while(reader.ready()) {
               String s = reader.readLine();
               String[] split = s.split(" ", 2);
               if (split.length != 2) {
                  Placebo.LOG.error("Invalid patreon data entry {} will be ignored.", s);
               } else {
                  int dataVer = Integer.parseInt(split[0]);
                  Consumer<String> func = (Consumer)DATA_PARSERS.get(dataVer);
                  if (func == null) {
                     Placebo.LOG.error("Invalid patreon data entry {} with invalid data version {} will be ignored.", s, Integer.valueOf(dataVer));
                  } else {
                     func.accept(split[1]);
                  }
               }
            }

            reader.close();
         } catch (IOException var6) {
            Placebo.LOG.error("Exception loading patreon data!");
            var6.printStackTrace();
         }

      }, "Placebo Patreon Loader")).start();
   }

   static void parseV0Data(String s) {
      String[] split = s.split(" ");
      if (split.length != 2) {
         Placebo.LOG.error("Invalid patreon data entry (version 0) {} will be ignored.", s);
      } else {
         EaglercraftUUID id = EaglercraftUUID.fromString(split[0]);
         EnumParticleTypes type = EnumParticleTypes.getByName(split[1]);
         PATREONS.put(id, type);
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent e) {
      if (e.player.world.isRemote) {
         EaglercraftRandom rand = Minecraft.getMinecraft().world.rand;
         if (rand.nextInt(35) == 0) {
            EnumParticleTypes type = (EnumParticleTypes)PATREONS.get(e.player.getGameProfile().getId());
            if (type == null) {
               return;
            }

            double x = e.player.prevPosX;
            double y = e.player.prevPosY;
            double z = e.player.prevPosZ;
            Minecraft.getMinecraft().world.spawnParticle(type, true, x, y + 0.7D, z, MathHelper.nextDouble(rand, -0.05D, 0.05D), MathHelper.nextDouble(rand, 0.03D, 0.15D), MathHelper.nextDouble(rand, -0.05D, 0.05D), new int[0]);
         }
      }

   }

   static {
      DATA_PARSERS.put(0, PatreonManager::parseV0Data);
   }
}
