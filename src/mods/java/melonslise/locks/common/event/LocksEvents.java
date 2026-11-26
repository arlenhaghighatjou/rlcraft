package melonslise.locks.common.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksEnchantments;
import melonslise.locks.common.init.LocksItems;
import melonslise.locks.common.init.LocksNetworks;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.item.KeyItem;
import melonslise.locks.common.item.KeyRingItem;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.item.LockPickItem;
import melonslise.locks.common.item.LockingItem;
import melonslise.locks.common.network.toclient.AddLockablePacket;
import melonslise.locks.common.network.toclient.ConfigSyncPacket;
import melonslise.locks.common.network.toclient.RemoveLockablePacket;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.world.LocksWorldEventListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.event.world.ChunkWatchEvent.UnWatch;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public final class LocksEvents {
   public static final LocksWorldEventListener LISTENER = new LocksWorldEventListener();
   public static final ITextComponent LOCKED_MESSAGE = new TextComponentTranslation("locks.status.locked", new Object[0]);

   private LocksEvents() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, LocksEvents::registerItems);
      bus.registerGeneric(Register.class, Enchantment.class, LocksEvents::registerEnchantments);
      bus.registerGeneric(Register.class, SoundEvent.class, LocksEvents::registerSoundEvents);
      bus.registerGeneric(AttachCapabilitiesEvent.class, World.class, LocksEvents::attachCapabilitiesToWorld);
      bus.registerGeneric(AttachCapabilitiesEvent.class, Chunk.class, LocksEvents::attachCapabilitiesToChunk);
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, LocksEvents::attachCapabilitiesToEntity);
      bus.register(OnConfigChangedEvent.class, LocksEvents::onConfigChange);
      bus.register(Load.class, LocksEvents::onWorldLoad);
      bus.register(Post.class, LocksEvents::onChunkPopulate);
      bus.register(PlayerLoggedInEvent.class, LocksEvents::onPlayerLogIn);
      bus.register(PlayerChangedDimensionEvent.class, LocksEvents::onPlayerChangeDim);
      bus.register(PlayerRespawnEvent.class, LocksEvents::onPlayerRespawn);
      bus.register(PlayerTickEvent.class, LocksEvents::onPlayerTick);
      bus.register(RightClickBlock.class, LocksEvents::onRightClick);
      bus.register(BreakSpeed.class, LocksEvents::onBlockBreaking);
      bus.register(BreakEvent.class, LocksEvents::onBlockBreak);
      bus.register(Watch.class, LocksEvents::onChunkWatch);
      bus.register(UnWatch.class, LocksEvents::onChunkUnWatch);
      bus.register(Unload.class, LocksEvents::onChunkUnload);
      bus.register(net.minecraftforge.event.world.ChunkEvent.Load.class, LocksEvents::onChunkLoad);
   }

   public static void registerItems(Register<Item> event) {
      LocksItems.register(event);
   }

   public static void registerEnchantments(Register<Enchantment> event) {
      LocksEnchantments.register(event);
   }

   public static void registerSoundEvents(Register<SoundEvent> event) {
      LocksSoundEvents.register(event);
   }

   public static void attachCapabilitiesToWorld(AttachCapabilitiesEvent<World> event) {
      LocksCapabilities.attachToWorld(event);
   }

   public static void attachCapabilitiesToChunk(AttachCapabilitiesEvent<Chunk> event) {
      LocksCapabilities.attachToChunk(event);
   }

   public static void attachCapabilitiesToEntity(AttachCapabilitiesEvent<Entity> event) {
      LocksCapabilities.attachToEntity(event);
   }

   public static void onConfigChange(OnConfigChangedEvent event) {
      if (event.getModID().equals("locks")) {
         ConfigManager.sync("locks", Type.INSTANCE);
         LocksConfig.SERVER.init();
         LocksConfig.getServerClient().init();
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         if (server != null) {
            server.addScheduledTask(() -> {
               LocksNetworks.MAIN.sendToAll(new ConfigSyncPacket(LocksConfig.SERVER));
            });
         }
      }
   }

   public static void onWorldLoad(Load event) {
      event.getWorld().addEventListener(LISTENER);
   }

   public static void onChunkPopulate(Post event) {
      Chunk chunk = event.getWorld().getChunkProvider().getLoadedChunk(event.getChunkX(), event.getChunkZ());
      if (chunk != null) {
         ((ILockableWorldGenHandler)chunk.getCapability(LocksCapabilities.LOCKABLE_WORLDGEN_HANDLER, (EnumFacing)null)).setChunkShouldGenerateChests();
      }
   }

   public static void onPlayerLogIn(PlayerLoggedInEvent event) {
      LocksNetworks.MAIN.sendTo(new ConfigSyncPacket(LocksConfig.SERVER), (EntityPlayerMP)event.player);
   }

   public static void onPlayerChangeDim(PlayerChangedDimensionEvent event) {
   }

   public static void onPlayerRespawn(PlayerRespawnEvent event) {
   }

   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase != Phase.END) {
         ISelection select = (ISelection)event.player.getCapability(LocksCapabilities.SELECTION, (EnumFacing)null);
         if (select.get() != null) {
            for(ItemStack stack : event.player.getHeldEquipment()) {
               if (stack.getItem() instanceof LockItem) {
                  return;
               }
            }

            select.set((BlockPos)null);
         }
      }
   }

   public static void onRightClick(RightClickBlock event) {
      BlockPos pos = event.getPos();
      World world = event.getWorld();
      EntityPlayer player = event.getEntityPlayer();
      ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
      List<Lockable> intersecting = (List)handler.getInChunk(pos).values().stream().filter((lockable1) -> {
         return lockable1.box.intersects(pos);
      }).collect(Collectors.toList());
      if (!intersecting.isEmpty()) {
         if (event.getHand() != EnumHand.MAIN_HAND) {
            event.setUseBlock(Result.DENY);
         } else {
            Optional<Lockable> locked = intersecting.stream().filter(LocksPredicates.LOCKED).findFirst();
            if (locked.isPresent()) {
               Lockable lkb = (Lockable)locked.get();
               event.setUseBlock(Result.DENY);
               ItemStack stack = event.getItemStack();
               Item item = stack.getItem();
               if (!(item instanceof LockPickItem) && item != LocksItems.MASTER_KEY && (!(item instanceof KeyItem) || LockingItem.getOrSetId(stack) != lkb.lock.id) && (item != LocksItems.KEY_RING || !KeyRingItem.containsId(stack, lkb.lock.id))) {
                  lkb.shake(20);
                  world.playSound((EntityPlayer)null, pos, LocksSoundEvents.LOCK_RATTLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  if (world.isRemote && LocksConfig.CLIENT.deafMode) {
                     player.sendStatusMessage(LOCKED_MESSAGE, true);
                  }
               }

               player.swingArm(EnumHand.MAIN_HAND);
            } else {
               if (LocksConfig.getServer(world).allowRemovingLocks && player.isSneaking() && event.getItemStack().isEmpty()) {
                  List<Lockable> matching = (List)intersecting.stream().filter(LocksPredicates.NOT_LOCKED).collect(Collectors.toList());
                  if (matching.isEmpty()) {
                     return;
                  }

                  event.setUseBlock(Result.DENY);
                  world.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
                  player.swingArm(EnumHand.MAIN_HAND);
                  if (world.isRemote) {
                     return;
                  }

                  for(Lockable lockable : matching) {
                     world.spawnEntity(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, lockable.stack));
                     handler.remove(lockable.networkID);
                  }
               }

            }
         }
      }
   }

   public static boolean canBreakLockable(EntityPlayer player, BlockPos pos) {
      return !LocksConfig.getServer(player.world).protectLockables || player.isCreative() || !LocksUtil.locked(player.world, pos);
   }

   public static void onBlockBreaking(BreakSpeed event) {
      if (!canBreakLockable(event.getEntityPlayer(), event.getPos())) {
         event.setCanceled(true);
      }

   }

   public static void onBlockBreak(BreakEvent event) {
      if (!canBreakLockable(event.getPlayer(), event.getPos())) {
         event.setCanceled(true);
      }

   }

   public static void onChunkWatch(Watch event) {
      EntityPlayerMP player = event.getPlayer();
      ((ILockableStorage)event.getChunkInstance().getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).get().values().stream().forEach((lkb) -> {
         LocksNetworks.MAIN.sendTo(new AddLockablePacket(lkb), player);
      });
   }

   public static void onChunkUnWatch(UnWatch event) {
      EntityPlayerMP player = event.getPlayer();
      ((ILockableStorage)event.getChunkInstance().getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).get().keySet().stream().forEach((id) -> {
         LocksNetworks.MAIN.sendTo(new RemoveLockablePacket(id.intValue()), player);
      });
   }

   public static void onChunkUnload(Unload event) {
      Chunk ch = event.getChunk();
      World world = ch.getWorld();
      if (!world.isRemote) {
         ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
         ((ILockableStorage)ch.getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).get().values().forEach((lkb) -> {
            ChunkPos unloadPos = ch.getPos();

            for(ChunkPos chunkPos : lkb.box.containedChunkPosList()) {
               if (!unloadPos.equals(chunkPos) && LocksUtil.hasChunk(world, chunkPos.x, chunkPos.z)) {
                  ;
               }
            }

            handler.getLoaded().remove(lkb.networkID);
            lkb.deleteObserver(handler);
            if (Locks.debug) {
               Locks.logger.debug("Removing lockable from loaded with id: " + lkb.networkID + " ::: " + lkb.toString());
            }

         });
      }
   }

   public static void onChunkLoad(net.minecraftforge.event.world.ChunkEvent.Load event) {
      Chunk ch = event.getChunk();
      World world = ch.getWorld();
      if (!world.isRemote) {
         ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
         Int2ObjectMap<Lockable> loadedLockables = handler.getLoaded();
         ((ILockableStorage)ch.getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).get().values().forEach((lkb) -> {
            if (!loadedLockables.containsKey(lkb.networkID)) {
               lkb.addObserver(handler);
               loadedLockables.put(lkb.networkID, lkb);
               if (Locks.debug) {
                  Locks.logger.debug("Placing lockable into loaded with id: " + lkb.networkID + " ::: " + lkb.toString());
               }
            }

         });
      }
   }
}
