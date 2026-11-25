package net.mcft.copy.backpacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.api.IBackpackType;
import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.item.DyeWashingHandler;
import net.mcft.copy.backpacks.misc.BackpackCapability;
import net.mcft.copy.backpacks.misc.util.MiscUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.mcft.copy.backpacks.misc.util.WorldUtils;
import net.mcft.copy.backpacks.network.MessageBackpackUpdate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class ProxyCommon {
   private boolean cancelOffHand = false;

   public ProxyCommon() {
   }

   public void preInit() {
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(WearableBackpacks.CONTENT);
      MinecraftForge.EVENT_BUS.register(new DyeWashingHandler());
      CapabilityManager.INSTANCE.register(IBackpack.class, new BackpackCapability.Storage(), BackpackCapability::new);
   }

   public void init() {
   }

   public void initBackpackLayers() {
   }

   @SubscribeEvent
   public void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
      if (BackpackRegistry.canEntityWearBackpacks((Entity)event.getObject())) {
         event.addCapability(BackpackCapability.IDENTIFIER, new BackpackCapability.Provider((EntityLivingBase)event.getObject()));
      }
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      this.sendBackpackStack(event.player, event.player);
   }

   @SubscribeEvent
   public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      this.sendBackpackStack(event.player, event.player);
   }

   @SubscribeEvent
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      this.sendBackpackStack(event.player, event.player);
   }

   @SubscribeEvent
   public void onPlayerStartTracking(StartTracking event) {
      this.sendBackpackStack(event.getTarget(), event.getEntityPlayer());
   }

   private void sendBackpackStack(Entity carrier, EntityPlayer player) {
      BackpackCapability backpack = (BackpackCapability)BackpackHelper.getBackpack(carrier);
      if (backpack != null) {
         WearableBackpacks.CHANNEL.sendTo(MessageBackpackUpdate.stack(carrier, backpack.stack), player);
      }

   }

   @SubscribeEvent
   public void onCheckSpawn(CheckSpawn event) {
      if (event.isSpawner()) {
         if (!ModConfig.server.spawnFromSpawners) {
            return;
         }
      } else if (!ModConfig.server.spawnNaturally) {
         return;
      }

      EntityLivingBase entity = event.getEntityLiving();

      for(BackpackRegistry.BackpackEntry entry : BackpackRegistry.getDefaultBackpackEntries(entity.getClass())) {
         if (entry.chance != 0 && entity.world.rand.nextDouble() <= 1.0D / (double)entry.chance) {
            BackpackCapability backpack = (BackpackCapability)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
            backpack.spawnWith = entry;
            break;
         }
      }

   }

   private void onSpawnedWith(EntityLivingBase entity, BackpackCapability backpack, BackpackRegistry.BackpackEntry entry) {
      ItemStack stack = new ItemStack(entry.getBackpackItem());
      if (entry.colorRange != null) {
         NbtUtils.set(stack, Integer.valueOf(entry.colorRange.getRandom()), "display", "color");
      }

      int maxDamage = stack.getMaxDamage();
      int damage = maxDamage / 4 + (maxDamage / 2 > 0 ? entity.world.rand.nextInt(maxDamage / 2) : 0);
      stack.setItemDamage(damage);
      IBackpackType type = (IBackpackType)entry.getBackpackItem();
      IBackpackData data = type.createBackpackData(stack);
      BackpackHelper.setEquippedBackpack(entity, stack, data);
      type.onSpawnedWith(entity, backpack, entry.lootTable);
      backpack.spawnWith = null;
      backpack.mayDespawn = true;
   }

   @SubscribeEvent
   public void onPlayerInteractBlock(RightClickBlock event) {
      if (this.cancelOffHand) {
         this.cancelOffHand = false;
         if (event.getHand() == EnumHand.OFF_HAND) {
            event.setCanceled(true);
            return;
         }
      }

      EntityPlayer player = event.getEntityPlayer();
      World world = event.getWorld();
      if (player.isSneaking() && event.getHand() == EnumHand.MAIN_HAND && player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
         IBackpack backpack = BackpackHelper.getBackpack(player);
         if (backpack != null) {
            player.inventory.mainInventory.set(player.inventory.currentItem, backpack.getStack());
            if (backpack.getStack().onItemUse(player, world, event.getPos(), EnumHand.MAIN_HAND, event.getFace(), 0.5F, 0.5F, 0.5F) == EnumActionResult.SUCCESS) {
               player.swingArm(EnumHand.MAIN_HAND);
               event.setCanceled(true);
               this.cancelOffHand = true;
            } else {
               player.inventory.mainInventory.set(player.inventory.currentItem, ItemStack.EMPTY);
            }

         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public void onBlockPlace(PlaceEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.getWorld().isRemote && event.getPlayer() != null && event.isCanceled() && ModConfig.server.ignoreCancellation) {
         TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
         if (tileEntity != null) {
            IBackpack placedBackpack = BackpackHelper.getBackpack(tileEntity);
            if (placedBackpack != null) {
               event.setCanceled(false);
            }
         }

         EntityEquipmentSlot slot = event.getHand().equals(EnumHand.MAIN_HAND) ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
         ((EntityPlayerMP)event.getPlayer()).connection.sendPacket(new SPacketEntityEquipment(event.getPlayer().getEntityId(), slot, event.getPlayer().getItemStackFromSlot(slot)));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public void onBlockBreak(BreakEvent event) {
      if (event.getState() != null && event.getWorld() != null && !event.getWorld().isRemote && event.getPlayer() != null && event.isCanceled() && ModConfig.server.ignoreCancellation) {
         TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
         if (tileEntity != null) {
            IBackpack placedBackpack = BackpackHelper.getBackpack(tileEntity);
            if (placedBackpack != null) {
               event.setCanceled(false);
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      if (ModConfig.server.enableEquippedInteraction && event.getTarget() instanceof EntityLivingBase) {
         EntityPlayer player = event.getEntityPlayer();
         EntityLivingBase target = (EntityLivingBase)event.getTarget();
         BackpackCapability backpack = (BackpackCapability)target.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         if (backpack != null && BackpackHelper.canInteractWithEquippedBackpack(player, target)) {
            IBackpackType type = backpack.getType();
            if (type == null) {
               WearableBackpacks.LOG.error("Backpack type was null when accessing equipped backpack");
            } else {
               if (!player.world.isRemote && backpack.getData() == null) {
                  IBackpackData data = type.createBackpackData(backpack.getStack());
                  if (data != null) {
                     WearableBackpacks.LOG.error("Backpack data was null when accessing equipped backpack");
                     backpack.setData(data);
                  }
               }

               type.onEquippedInteract(player, target, backpack);
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      BackpackCapability backpack = (BackpackCapability)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
      if (backpack != null) {
         if (backpack.spawnWith != null) {
            this.onSpawnedWith(entity, backpack, backpack.spawnWith);
         }

         boolean hasBackpack = !backpack.getStack().isEmpty();
         if (backpack.isBauble()) {
            if (!hasBackpack) {
               backpack.getType().onFaultyRemoval(entity, backpack);
               backpack.setStack(ItemStack.EMPTY);
               return;
            }
         } else if (!hasBackpack) {
            return;
         }

         backpack.getType().onEquippedTick(entity, backpack);
         if (entity.world.isRemote) {
            BackpackHelper.updateLidTicks(backpack, entity.posX, entity.posY + 1.0D, entity.posZ);
         }

      }
   }

   @SubscribeEvent
   public void onLivingDeath(LivingDeathEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      World world = entity.world;
      if (!world.isRemote) {
         BackpackCapability backpack = (BackpackCapability)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         if (backpack != null && !backpack.getStack().isEmpty()) {
            if (!MiscUtils.shouldKeepItem(entity, backpack.getStack())) {
               if (ModConfig.server.dropAsBlockOnDeath) {
                  List<ProxyCommon.BlockCoord> coords = new ArrayList();

                  for(int x = -2; x <= 2; ++x) {
                     for(int z = -2; z <= 2; ++z) {
                        coords.add(new ProxyCommon.BlockCoord(entity, x, z));
                     }
                  }

                  Collections.sort(coords, new Comparator<ProxyCommon.BlockCoord>() {
                     public int compare(ProxyCommon.BlockCoord o1, ProxyCommon.BlockCoord o2) {
                        if (o1.distance < o2.distance) {
                           return -1;
                        } else {
                           return o1.distance > o2.distance ? 1 : 0;
                        }
                     }
                  });

                  while(!coords.isEmpty()) {
                     Iterator<ProxyCommon.BlockCoord> iter = coords.iterator();

                     while(iter.hasNext()) {
                        ProxyCommon.BlockCoord coord = (ProxyCommon.BlockCoord)iter.next();
                        if (BackpackHelper.placeBackpack(world, coord, backpack.getStack(), entity, true)) {
                           ((TileEntityBackpack)world.getTileEntity(coord)).setPlacedOnDeath(backpack.mayDespawn);
                           return;
                        }

                        boolean replacable = world.getBlockState(coord).getBlock().isReplaceable(world, coord);
                        coord.add(0, replacable ? -1 : 1, 0);
                        coord.moved += replacable ? 1 : 5;
                        if (coord.getY() <= 0 || coord.getY() > world.getHeight() || (double)coord.moved > 24.0D - coord.distance * 4.0D) {
                           iter.remove();
                        }
                     }
                  }
               }

               backpack.getType().onDeath(entity, backpack);
               if (!backpack.getStack().isEmpty()) {
                  WorldUtils.dropStackFromEntity(entity, backpack.getStack(), 4.0F);
               }

               BackpackHelper.setEquippedBackpack(entity, ItemStack.EMPTY, (IBackpackData)null);
            }
         }
      }
   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      IBackpack originalBackpack = BackpackHelper.getBackpack(event.getOriginal());
      if (originalBackpack != null) {
         EntityPlayer player = event.getEntityPlayer();
         IBackpack clonedBackpack = (IBackpack)player.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         clonedBackpack.setStack(originalBackpack.getStack());
         clonedBackpack.setData(originalBackpack.getData());
      }
   }

   private static class BlockCoord extends MutableBlockPos {
      public double distance;
      public int moved = 0;

      public BlockCoord(Entity entity, int x, int z) {
         super((int)entity.posX + x, (int)entity.posY, (int)entity.posZ + z);
         this.distance = Math.sqrt(Math.pow((double)this.getX() + 0.5D - entity.posX, 2.0D) + Math.pow((double)this.getY() + 0.5D - entity.posY, 2.0D) + Math.pow((double)this.getZ() + 0.5D - entity.posZ, 2.0D));
      }
   }
}
