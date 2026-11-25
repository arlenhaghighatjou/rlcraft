package wiresegal.classyhats.event;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.ClassyHats;
import wiresegal.classyhats.ClassyHatsConfig;
import wiresegal.classyhats.ClassyHatsContent;
import wiresegal.classyhats.capability.CapabilityHatContainer;
import wiresegal.classyhats.capability.data.ClassyHatProvider;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.item.ItemHat;
import wiresegal.classyhats.network.PacketEntityHatSync;
import wiresegal.classyhats.network.PacketHatSync;

@EventBusSubscriber(
   modid = "classyhats"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0007J\u0010\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0010H\u0007J\u0010\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0012H\u0007J\u0010\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0014H\u0007J\u0010\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0016H\u0007J\u0010\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0018H\u0007J\u0010\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u001aH\u0007J\u0010\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u001cH\u0007J\u0016\u0010\u001d\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00040\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""},
   d2 = {"Lwiresegal/classyhats/event/CapabilityEvents;", "", "()V", "customKey", "", "getCustomKey", "()Ljava/lang/String;", "mapOfEntity", "Ljava/util/WeakHashMap;", "Lnet/minecraft/entity/EntityLiving;", "attachCapabilitiesPlayer", "", "event", "Lnet/minecraftforge/event/AttachCapabilitiesEvent;", "Lnet/minecraft/entity/Entity;", "cloneCapabilitiesEvent", "Lnet/minecraftforge/event/entity/player/PlayerEvent$Clone;", "onEntityJoinWorld", "Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;", "onLivingUpdate", "Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;", "onPlayerChangedDimension", "Lnet/minecraftforge/fml/common/gameevent/PlayerEvent$PlayerChangedDimensionEvent;", "onPlayerLoggedIn", "Lnet/minecraftforge/fml/common/gameevent/PlayerEvent$PlayerLoggedInEvent;", "onPlayerRespawn", "Lnet/minecraftforge/fml/common/gameevent/PlayerEvent$PlayerRespawnEvent;", "onPlayerStartTracking", "Lnet/minecraftforge/event/entity/player/PlayerEvent$StartTracking;", "syncDataFor", "entity", "Lnet/minecraft/entity/player/EntityPlayer;", "to", "Lnet/minecraft/entity/player/EntityPlayerMP;", "classyhats"}
)
public final class CapabilityEvents {
   @NotNull
   private static final String customKey = "classyhats:hat";
   private static final WeakHashMap<EntityLiving, String> mapOfEntity = new WeakHashMap();
   public static final CapabilityEvents INSTANCE;

   @SubscribeEvent
   @JvmStatic
   public static final void cloneCapabilitiesEvent(@NotNull Clone event) {
      Intrinsics.checkParameterIsNotNull(event, "event");

      try {
         CapabilityHatContainer.Companion var5 = CapabilityHatContainer.Companion;
         EntityPlayer var7 = event.getOriginal();
         Intrinsics.checkExpressionValueIsNotNull(var7, "event.original");
         IHatContainer bco = var5.getCapability(var7);
         NBTTagCompound nbt = (NBTTagCompound)bco.serializeNBT();
         var5 = CapabilityHatContainer.Companion;
         var7 = event.getEntityPlayer();
         Intrinsics.checkExpressionValueIsNotNull(var7, "event.entityPlayer");
         IHatContainer bcn = var5.getCapability(var7);
         bcn.deserializeNBT((NBTBase)nbt);
      } catch (Exception var4) {
         Logger var10000 = ClassyHats.Companion.getLOGGER();
         StringBuilder var10001 = (new StringBuilder()).append("Could not clone player [");
         EntityPlayer var10002 = event.getOriginal();
         Intrinsics.checkExpressionValueIsNotNull(var10002, "event.original");
         var10000.error(var10001.append(var10002.getName()).append("] HATS when changing dimensions").toString());
      }

   }

   @SubscribeEvent
   @JvmStatic
   public static final void attachCapabilitiesPlayer(@NotNull AttachCapabilitiesEvent<Entity> event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("classyhats", "container"), (ICapabilityProvider)(new ClassyHatProvider((IHatContainer)null, 1, (DefaultConstructorMarker)null)));
      }

   }

   @NotNull
   public final String getCustomKey() {
      return customKey;
   }

   @SubscribeEvent
   @JvmStatic
   public static final void onEntityJoinWorld(@NotNull EntityJoinWorldEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Entity var10000 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "event.entity");
      NBTTagCompound customData = var10000.getEntityData();
      World world = event.getWorld();
      String name = String.valueOf(EntityList.getKey(event.getEntity()));
      Entity entity = event.getEntity();
      if (ClassyHatsConfig.INSTANCE.getNames().contains(name) && world instanceof WorldServer && entity instanceof EntityLiving) {
         if (!customData.hasKey(customKey)) {
            List stack = ((WorldServer)world).getLootTableManager().getLootTableFromLocation(new ResourceLocation("classyhats", "combined")).generateLootForPools(world.rand, new LootContext(0.0F, (WorldServer)world, ((WorldServer)world).getLootTableManager(), (Entity)null, (EntityPlayer)null, (DamageSource)null));
            Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
            Collection var6 = (Collection)stack;
            if (!var6.isEmpty() && world.rand.nextFloat() < ClassyHatsConfig.INSTANCE.getHatSpawnPercentage()) {
               String var10001 = customKey;
               ItemHat var10002 = ClassyHatsContent.INSTANCE.getHAT();
               Object var10003 = stack.get(0);
               Intrinsics.checkExpressionValueIsNotNull(var10003, "stack[0]");
               customData.setString(var10001, var10002.getHat((ItemStack)var10003).getName());
            } else {
               customData.setString(customKey, "");
            }
         }

         mapOfEntity.put(entity, customData.getString(customKey));
      }

   }

   @SubscribeEvent
   @JvmStatic
   public static final void onLivingUpdate(@NotNull LivingUpdateEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Entity var10000 = event.getEntity();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "event.entity");
      NBTTagCompound customData = var10000.getEntityData();
      Entity entity = event.getEntity();
      World world = entity.world;
      String name = String.valueOf(EntityList.getKey(event.getEntity()));
      if (ClassyHatsConfig.INSTANCE.getNames().contains(name) && world instanceof WorldServer && entity instanceof EntityLiving) {
         String hat = customData.getString(customKey);
         String prev = (String)mapOfEntity.get(entity);
         if (Intrinsics.areEqual(hat, prev) ^ true) {
            mapOfEntity.put(entity, hat);
            EntityTrackerEntry var13 = CapabilityHatContainer.Companion.getEntry(entity, (WorldServer)world);
            if (var13 != null) {
               Set var14 = var13.trackingPlayers;
               if (var14 != null) {
                  for(Object element$iv : (Iterable)var14) {
                     EntityPlayerMP it = (EntityPlayerMP)element$iv;
                     SimpleNetworkWrapper var15 = PacketHandler.NETWORK;
                     int var10003 = ((EntityLiving)entity).getEntityId();
                     Intrinsics.checkExpressionValueIsNotNull(hat, "hat");
                     var15.sendTo((IMessage)(new PacketEntityHatSync(var10003, hat)), it);
                  }
               }
            }
         }
      }

   }

   @SubscribeEvent
   @JvmStatic
   public static final void onPlayerStartTracking(@NotNull StartTracking event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getTarget() instanceof EntityPlayer) {
         CapabilityEvents var10000 = INSTANCE;
         Entity var10001 = event.getTarget();
         if (var10001 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayer");
         }

         EntityPlayer var4 = (EntityPlayer)var10001;
         EntityPlayer var10002 = event.getEntityPlayer();
         if (var10002 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayerMP");
         }

         var10000.syncDataFor(var4, (EntityPlayerMP)var10002);
      } else {
         Entity var2 = event.getTarget();
         Intrinsics.checkExpressionValueIsNotNull(var2, "event.target");
         NBTTagCompound data = var2.getEntityData();
         if (data.hasKey(customKey)) {
            SimpleNetworkWrapper var3 = PacketHandler.NETWORK;
            Entity var10003 = event.getTarget();
            Intrinsics.checkExpressionValueIsNotNull(var10003, "event.target");
            int var7 = var10003.getEntityId();
            String var10004 = data.getString(customKey);
            Intrinsics.checkExpressionValueIsNotNull(var10004, "data.getString(customKey)");
            IMessage var5 = (IMessage)(new PacketEntityHatSync(var7, var10004));
            EntityPlayer var6 = event.getEntityPlayer();
            if (var6 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayerMP");
            }

            var3.sendTo(var5, (EntityPlayerMP)var6);
         }
      }

   }

   @SubscribeEvent
   @JvmStatic
   public static final void onPlayerLoggedIn(@NotNull PlayerLoggedInEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      CapabilityEvents var10000 = INSTANCE;
      EntityPlayer var10001 = event.player;
      Intrinsics.checkExpressionValueIsNotNull(event.player, "event.player");
      EntityPlayer var10002 = event.player;
      if (event.player == null) {
         throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayerMP");
      } else {
         var10000.syncDataFor(var10001, (EntityPlayerMP)var10002);
      }
   }

   @SubscribeEvent
   @JvmStatic
   public static final void onPlayerChangedDimension(@NotNull PlayerChangedDimensionEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      CapabilityEvents var10000 = INSTANCE;
      EntityPlayer var10001 = event.player;
      Intrinsics.checkExpressionValueIsNotNull(event.player, "event.player");
      EntityPlayer var10002 = event.player;
      if (event.player == null) {
         throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayerMP");
      } else {
         var10000.syncDataFor(var10001, (EntityPlayerMP)var10002);
      }
   }

   @SubscribeEvent
   @JvmStatic
   public static final void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      CapabilityEvents var10000 = INSTANCE;
      EntityPlayer var10001 = event.player;
      Intrinsics.checkExpressionValueIsNotNull(event.player, "event.player");
      EntityPlayer var10002 = event.player;
      if (event.player == null) {
         throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayerMP");
      } else {
         var10000.syncDataFor(var10001, (EntityPlayerMP)var10002);
      }
   }

   public final void syncDataFor(@NotNull EntityPlayer entity, @NotNull EntityPlayerMP to) {
      Intrinsics.checkParameterIsNotNull(entity, "entity");
      Intrinsics.checkParameterIsNotNull(to, "to");
      IHatContainer data = CapabilityHatContainer.Companion.getCapability(entity);
      NBTTagCompound hats = entity.getEntityId() == to.getEntityId() ? (NBTTagCompound)data.serializeNBT() : null;
      PacketHandler.NETWORK.sendTo((IMessage)(new PacketHatSync(entity.getEntityId(), data.getEquipped(), hats)), to);
   }

   private CapabilityEvents() {
   }

   static {
      CapabilityEvents var0 = new CapabilityEvents();
      INSTANCE = var0;
   }
}
