package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.capability.TweakerProvider;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.network.MessageSendVersion;
import com.charles445.rltweaker.network.MessageUpdateAttackYaw;
import com.charles445.rltweaker.network.MessageUpdateDismountStatus;
import com.charles445.rltweaker.network.MessageUpdateEntityMovement;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.network.TaskScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinecraftHandler {
   private static final ReflectionMethod<Void> WRITE_PLAYER_DATA = new ReflectionMethod<Void>(PlayerList.class, "writePlayerData", "writePlayerData", new Class[]{EntityPlayerMP.class});
   public static Map<String, MinecraftHandler.IContainerValidator> containerValidators = new ConcurrentHashMap();
   public Map<UUID, BlockPos> containerEnforcedPlayers = new ConcurrentHashMap();

   public MinecraftHandler() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onPlayerUpdate(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         World world = player.world;
         if (!world.isRemote && player.openContainer != null && !player.openContainer.equals(player.inventoryContainer)) {
            UUID playerId = EntityPlayer.getUUID(player.getGameProfile());
            MinecraftHandler.IContainerValidator validator = (MinecraftHandler.IContainerValidator)containerValidators.get(player.openContainer.getClass().getName());
            if (validator != null && !validator.isValid(player.openContainer)) {
               this.containerEnforcedPlayers.remove(playerId);
               player.closeScreen();
            }

            BlockPos usedPosition = (BlockPos)this.containerEnforcedPlayers.get(playerId);
            if (usedPosition != null && usedPosition.distanceSq(player.getPosition()) > 65.0D) {
               this.containerEnforcedPlayers.remove(playerId);
               player.closeScreen();
            }
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote) {
         if (ModConfig.server.minecraft.allZombiesBreakDoors && event.getEntity() instanceof EntityZombie) {
            ((EntityZombie)event.getEntity()).setBreakDoorsAItask(true);
         }

      }
   }

   @SubscribeEvent
   public void onWorldUnload(Save event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         if (ModConfig.server.minecraft.cleanupMineshaftWorldgenFiles) {
            this.cleanMapGenStructureData(world, "Mineshaft", false, true);
         }

      }
   }

   private void cleanMapGenStructureData(World world, String dataName, boolean checkIsUngenerated, boolean checkIsLoaded) {
      long nanoA = System.nanoTime();
      MapGenStructureData structureData = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, dataName);
      if (structureData != null) {
         NBTTagCompound structureDataCompound = structureData.getTagCompound();
         List<String> toRemove = new ArrayList();
         long cleanupCount = 0L;

         for(String s : structureDataCompound.getKeySet()) {
            toRemove.add(s);
         }

         for(String remove : toRemove) {
            structureDataCompound.removeTag(remove);
         }

         if (toRemove.size() > 0) {
            structureData.markDirty();
         }

         long nanoB = System.nanoTime();
         toRemove.clear();
      }
   }

   private List<ChunkPos> containedChunks(int xIn1, int zIn1, int xIn2, int zIn2) {
      int xStart = xIn1 >> 4;
      int zStart = zIn1 >> 4;
      int xEnd = xIn2 >> 4;
      int zEnd = zIn2 >> 4;
      List<ChunkPos> chunks = new ArrayList();

      for(int i = xStart; i <= xEnd; ++i) {
         for(int j = zStart; j <= zEnd; ++j) {
            chunks.add(new ChunkPos(i, j));
         }
      }

      return chunks;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onAttachCapability(AttachCapabilitiesEvent event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("rltweaker", "tweaker"), new TweakerProvider(RLCapabilities.TWEAKER));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onClientScheduled(ClientTickEvent event) {
      TaskScheduler.processClientTick(event);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntityDismount(EntityMountEvent event) {
      World world = event.getWorldObj();
      if (!world.isRemote) {
         if (ModConfig.server.minecraft.playerDismountSync && event.isDismounting() && event.getEntityMounting() instanceof EntityPlayerMP && event.getEntityBeingMounted() != null) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityMounting();
            if (NetworkHandler.isVersionAtLeast(0, 3, player)) {
               MessageUpdateDismountStatus message = new MessageUpdateDismountStatus();
               PacketHandler.instance.sendTo(message, player);
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onClientPlayerJoinWorldEvent(EntityJoinWorldEvent event) {
      if (event.getWorld().isRemote) {
         if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer localPlayer = RLTweaker.proxy.getClientMinecraftPlayer();
            if (localPlayer == null) {
               return;
            }

            EntityPlayer entPlayer = (EntityPlayer)event.getEntity();
            if (entPlayer.getGameProfile().getId().equals(localPlayer.getGameProfile().getId()) && NetworkHandler.serverHasVersioning) {
               RLTweaker.logger.debug("Sending MessageSendVersion to server");
               PacketHandler.instance.sendToServer(new MessageSendVersion(RLTweaker.VERSION_DELIMITER));
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event) {
      if (event.player != null) {
         NetworkHandler.removeClient(event.player.getGameProfile().getId());
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntityJoinWorldLowest(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityArrow) {
         this.handleArrowJoinWorld((EntityArrow)event.getEntity());
      }

      if (event.getEntity() instanceof EntityPotion) {
         this.handlePotionJoinWorld((EntityPotion)event.getEntity());
      }

   }

   private void handlePotionJoinWorld(EntityPotion potion) {
      if (ModConfig.server.minecraft.witchPotionReplacements && potion.getThrower() instanceof EntityWitch) {
         ItemStack stack = potion.getPotion();
         PotionType type = PotionUtils.getPotionFromItem(stack);
         if (type == PotionTypes.HARMING) {
            this.swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchHarmingReplacements);
         } else if (type == PotionTypes.SLOWNESS) {
            this.swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchSlownessReplacements);
         } else if (type == PotionTypes.POISON) {
            this.swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchPoisonReplacements);
         } else if (type == PotionTypes.WEAKNESS) {
            this.swapPotionFromList(potion, stack, ModConfig.server.minecraft.witchWeaknessReplacements);
         }
      }

   }

   private void swapPotionFromList(EntityPotion potion, ItemStack stack, String[] potionNames) {
      if (potionNames.length > 0) {
         PotionType toSwap = PotionType.getPotionTypeForName(potionNames[potion.world.rand.nextInt(potionNames.length)]);
         if (toSwap != null) {
            PotionUtils.addPotionToItemStack(stack, toSwap);
            potion.setItem(stack);
         }
      }

   }

   private void handleArrowJoinWorld(EntityArrow arrow) {
      World world = arrow.getEntityWorld();
      if (!world.isRemote && ModConfig.server.minecraft.playerArrowSync && arrow.shootingEntity instanceof EntityPlayer) {
         double posX = arrow.posX;
         double posY = arrow.posY;
         double posZ = arrow.posZ;
         MessageUpdateEntityMovement message = new MessageUpdateEntityMovement(arrow);

         for(EntityPlayer nearbyPlayer : world.playerEntities) {
            if (nearbyPlayer.getDistanceSq(posX, posY, posZ) < 576.0D && NetworkHandler.isVersionAtLeast(0, 2, nearbyPlayer)) {
               PacketHandler.instance.sendTo(message, (EntityPlayerMP)nearbyPlayer);
            }
         }
      }

   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (ModConfig.server.minecraft.blacksmithChestTweak && event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
         LootTable table = event.getTable();
         if (table != null) {
            LootPool pool = table.getPool("main");
            if (pool != null) {
               pool.removeEntry("minecraft:diamond");
               pool.removeEntry("minecraft:iron_pickaxe");
               pool.removeEntry("minecraft:iron_sword");
               pool.removeEntry("minecraft:iron_chestplate");
               pool.removeEntry("minecraft:iron_helmet");
               pool.removeEntry("minecraft:iron_leggings");
               pool.removeEntry("minecraft:iron_boots");
               pool.removeEntry("minecraft:obsidian");
               pool.removeEntry("minecraft:iron_horse_armor");
               pool.removeEntry("minecraft:golden_horse_armor");
               pool.removeEntry("minecraft:diamond_horse_armor");
            }
         }
      }

   }

   @SubscribeEvent
   public void onKnockback(LivingKnockBackEvent event) {
      if (ModConfig.server.minecraft.damageTilt) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (player.world.isRemote) {
               return;
            }

            if (NetworkHandler.isVersionAtLeast(0, 4, player)) {
               PacketHandler.instance.sendTo(new MessageUpdateAttackYaw(player), (EntityPlayerMP)player);
            }
         }

      }
   }

   @SubscribeEvent
   public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
      if (event.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER && ModConfig.server.minecraft.lightningSoundChunkDistance != 10000.0D) {
         event.setVolume((float)ModConfig.server.minecraft.lightningSoundChunkDistance);
      }

   }

   @SubscribeEvent
   public void onContainerOpen(Open event) {
      if (event.getEntityPlayer() != null) {
         if (event.getContainer() != null) {
            EntityPlayer player = event.getEntityPlayer();
            UUID playerId = EntityPlayer.getUUID(player.getGameProfile());
            String containerClazz = event.getContainer().getClass().getName();
            String[] distanceClazzes = ModConfig.server.minecraft.containerDistanceClasses;

            for(int i = 0; i < distanceClazzes.length; ++i) {
               if (containerClazz.equals(distanceClazzes[i])) {
                  this.containerEnforcedPlayers.put(playerId, player.getPosition());
                  return;
               }
            }

            this.containerEnforcedPlayers.remove(playerId);
         }
      }
   }

   @SubscribeEvent
   public void onContainerClosed(Close event) {
      if (event.getEntityPlayer() != null) {
         if (event.getContainer() != null) {
            EntityPlayer player = event.getEntityPlayer();
            this.containerEnforcedPlayers.remove(EntityPlayer.getUUID(player.getGameProfile()));
         }
      }
   }

   @SubscribeEvent
   public void onLightningStruckEntity(EntityStruckByLightningEvent event) {
      if (!ModConfig.server.minecraft.lightningDestroysItems && event.getEntity() instanceof EntityItem) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void fixDropDisconnectLagDupe(PlayerLoggedOutEvent event) {
      if (ModConfig.server.minecraft.fixDropDisconnectLagDupe) {
         if (!event.player.world.isRemote) {
            MinecraftServer server = event.player.getServer();
            server.futureTaskQueue.add(new FutureTask(() -> {
               WRITE_PLAYER_DATA.invoke(server.getPlayerList(), (EntityPlayerMP)event.player);
               return null;
            }));
         }
      }
   }

   public interface IContainerValidator {
      boolean isValid(Container var1);
   }
}
