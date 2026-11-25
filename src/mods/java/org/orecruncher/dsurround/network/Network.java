package org.orecruncher.dsurround.network;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.ReflectedField.ObjectField;
import org.orecruncher.lib.collections.EmptyList;
import org.orecruncher.lib.task.Scheduler;

@EventBusSubscriber(
   modid = "dsurround"
)
public final class Network {
   private static final ObjectField<NetworkDispatcher, EntityPlayerMP> player = new ObjectField(NetworkDispatcher.class, "player", (String)null);
   private static final ObjectOpenHashSet<EaglercraftUUID> blockList = new ObjectOpenHashSet();
   private static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("dsurround");

   public Network() {
   }

   public static void initialize() {
      int discriminator = 0;
      ++discriminator;
      NETWORK.registerMessage(PacketWeatherUpdate.PacketHandler.class, PacketWeatherUpdate.class, discriminator, Side.CLIENT);
      ++discriminator;
      NETWORK.registerMessage(PacketSpeechBubble.PacketHandler.class, PacketSpeechBubble.class, discriminator, Side.CLIENT);
      ++discriminator;
      NETWORK.registerMessage(PacketEntityData.PacketHandler.class, PacketEntityData.class, discriminator, Side.CLIENT);
      ++discriminator;
      NETWORK.registerMessage(PacketThunder.PacketHandler.class, PacketThunder.class, discriminator, Side.CLIENT);
      ++discriminator;
      NETWORK.registerMessage(PacketEnvironment.PacketHandler.class, PacketEnvironment.class, discriminator, Side.CLIENT);
      ++discriminator;
      NETWORK.registerMessage(PacketServerData.PacketHandler.class, PacketServerData.class, discriminator, Side.CLIENT);
   }

   @SubscribeEvent
   public static void clientConnect(@Nonnull ServerConnectionFromClientEvent event) {
      NetworkDispatcher dispatcher = NetworkDispatcher.get(event.getManager());
      if (dispatcher != null) {
         try {
            EntityPlayerMP p = (EntityPlayerMP)player.get(dispatcher);
            String version = (String)dispatcher.getModList().get("dsurround");
            if (StringUtils.isEmpty(version)) {
               synchronized(blockList) {
                  blockList.add(p.getPersistentID());
               }

               ModBase.log().info("Player [%s] connected without having %s installed", new Object[]{p.getDisplayNameString(), "Dynamic Surroundings"});
            } else {
               synchronized(blockList) {
                  blockList.remove(p.getPersistentID());
               }

               ModBase.log().info("Player [%s] connected with %s %s", new Object[]{p.getDisplayNameString(), "Dynamic Surroundings", version});
            }
         } catch (Throwable var9) {
            var9.printStackTrace();
         }
      }

   }

   @SubscribeEvent
   public static void clientDisconnect(@Nonnull ServerDisconnectionFromClientEvent event) {
      NetworkDispatcher dispatcher = NetworkDispatcher.get(event.getManager());
      if (dispatcher != null) {
         try {
            EntityPlayerMP p = (EntityPlayerMP)player.get(dispatcher);
            synchronized(blockList) {
               blockList.remove(p.getPersistentID());
            }
         } catch (Throwable var6) {
            var6.printStackTrace();
         }
      }

   }

   @SubscribeEvent
   public static void clientDisconnect(@Nonnull PlayerLoggedOutEvent event) {
      try {
         synchronized(blockList) {
            blockList.remove(event.player.getPersistentID());
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

   }

   @SideOnly(Side.CLIENT)
   static void postEvent(@Nonnull Event event) {
      postEvent(Side.CLIENT, event);
   }

   private static void postEvent(@Nonnull Side side, @Nonnull Event event) {
      Scheduler.schedule(side, () -> {
         MinecraftForge.EVENT_BUS.post(event);
      });
   }

   private static void sendToList(@Nonnull List<EntityPlayerMP> players, @Nonnull IMessage msg) {
      synchronized(NETWORK) {
         players.forEach((p) -> {
            NETWORK.sendTo(msg, p);
         });
      }
   }

   private static <T extends EntityPlayer> List<EntityPlayerMP> generateStream(@Nonnull Collection<T> c, Predicate<T> pred) {
      Stream<T> strm = c.stream().filter((p) -> {
         return !(p instanceof FakePlayer);
      }).filter((p) -> {
         return !blockList.contains(p.getPersistentID());
      });
      if (pred != null) {
         strm = strm.filter(pred);
      }

      try {
         synchronized(blockList) {
            return (List)strm.map((p) -> {
               return (EntityPlayerMP)p;
            }).collect(Collectors.toList());
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
         return EmptyList.empty();
      }
   }

   public static void sendToPlayer(@Nonnull EntityPlayerMP player, @Nonnull IMessage msg) {
      try {
         synchronized(blockList) {
            if (blockList.contains(player.getPersistentID())) {
               return;
            }
         }

         synchronized(NETWORK) {
            NETWORK.sendTo(msg, player);
         }
      } catch (Throwable var7) {
         var7.printStackTrace();
      }

   }

   public static void sendToEntityViewers(@Nonnull Entity entity, @Nonnull IMessage msg) {
      Set<? extends EntityPlayer> players = ((WorldServer)entity.getEntityWorld()).getEntityTracker().getTrackingPlayers(entity);
      List<EntityPlayerMP> list = generateStream(players, (Predicate)null);
      sendToList(list, msg);
   }

   public static void sendToDimension(int dimensionId, @Nonnull IMessage msg) {
      WorldServer world = DimensionManager.getWorld(dimensionId);
      if (world != null) {
         List<EntityPlayerMP> players = generateStream(DimensionManager.getWorld(dimensionId).playerEntities, (Predicate)null);
         sendToList(players, msg);
      }

   }

   public static void sendToAll(@Nonnull IMessage msg) {
      List<EntityPlayerMP> players = generateStream(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers(), (Predicate)null);
      sendToList(players, msg);
   }

   public static void sendToAllAround(@Nonnull Locus point, @Nonnull IMessage msg) {
      WorldServer world = DimensionManager.getWorld(point.dimension);
      if (world != null) {
         double rSq = point.range * point.range;
         List<EntityPlayerMP> players = generateStream(DimensionManager.getWorld(point.dimension).playerEntities, (p) -> {
            return p.getDistanceSq(point.x, point.y, point.z) <= rSq;
         });
         sendToList(players, msg);
      }

   }
}
