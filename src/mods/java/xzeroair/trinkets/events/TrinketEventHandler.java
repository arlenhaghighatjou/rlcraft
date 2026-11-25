package xzeroair.trinkets.events;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.TrinketContainerProvider;
import xzeroair.trinkets.container.TrinketContainerHandler;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.SyncItemDataPacket;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class TrinketEventHandler {
   public TrinketEventHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void clientTickEvent(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player == null || !player.isEntityAlive() || player.world == null) {
            return;
         }

         TrinketHelper.getTrinketHandler(player, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventClientTick(stack, player);
               }
            }

         });
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void PlayerLoggedInEvent(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      World world = player.getEntityWorld();
      if (world != null) {
         boolean client = world.isRemote;
         TrinketHelper.getTrinketHandler(player, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!client && world instanceof WorldServer) {
                  if (!empty) {
                     Capabilities.getTrinketProperties(stack, (properties) -> {
                        properties.itemEquipped(stack, player);
                     });
                  }

                  SyncItemDataPacket packet = new SyncItemDataPacket(player, stack, stack.getTagCompound(), i, TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS, true, !empty);
                  NetworkHandler.sendToClients((WorldServer)world, player.getPosition(), packet);
               }

               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventPlayerLogin(stack, player);
               }
            }

         });
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void EntityJoinWorld(EntityJoinWorldEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         World world = event.getWorld();
         if (world != null) {
            boolean client = world.isRemote;
            TrinketHelper.getTrinketHandler(player, (handler) -> {
               for(int i = 0; i < handler.getSlots(); ++i) {
                  ItemStack stack = handler.getStackInSlot(i);
                  boolean empty = stack.isEmpty();
                  if (!client && world instanceof WorldServer) {
                     SyncItemDataPacket packet = new SyncItemDataPacket(player, stack, stack.getTagCompound(), i, TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS, true, !empty);
                     NetworkHandler.sendTo(packet, player);
                  }
               }

            });
         }
      }

   }

   @SubscribeEvent
   public void PlayerLoggedOutEvent(PlayerLoggedOutEvent event) {
      EntityPlayer player = event.player;
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventPlayerLogout(stack, player);
            }
         }

      });
   }

   @SubscribeEvent
   public void playerStartTracking(StartTracking event) {
      EntityPlayer player = event.getEntityPlayer();
      Entity target = event.getTarget();
      World world = player.getEntityWorld();
      if (!world.isRemote && world instanceof WorldServer && target instanceof EntityPlayer) {
         EntityPlayer targetPlayer = (EntityPlayer)target;
         TrinketHelper.getTrinketHandler(targetPlayer, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               SyncItemDataPacket packet = new SyncItemDataPacket(targetPlayer, stack, stack.getTagCompound(), i, TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS, true, !empty);
               NetworkHandler.sendTo(packet, (EntityPlayerMP)player);
            }

         });
      }

   }

   @SubscribeEvent
   public void PlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      EntityPlayer player = event.player;
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventPlayerChangedDimension(stack, player, event.fromDim, event.toDim);
            }
         }

      });
   }

   @SubscribeEvent
   public void playerUpdate(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      if (player.isEntityAlive()) {
         if (event.phase == Phase.END) {
            TrinketHelper.getTrinketHandler(player, (handler) -> {
               for(int i = 0; i < handler.getSlots(); ++i) {
                  ItemStack stack = handler.getStackInSlot(i);
                  boolean empty = stack.isEmpty();
                  if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                     IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                     item.eventPlayerTick(stack, player);
                  }
               }

            });
         }

      }
   }

   @SubscribeEvent
   public void LivingUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (!(entity instanceof EntityPlayer)) {
         TrinketHelper.getTrinketHandler(entity, (trinkets) -> {
            for(int i = 0; i < trinkets.getSlots(); ++i) {
               ItemStack stack = trinkets.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventLivingUpdateTick(stack, entity);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void potionApplicable(PotionApplicableEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      TrinketHelper.getTrinketHandler(entity, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventPotionApplicable(stack, entity, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void livingJump(LivingJumpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      TrinketHelper.getTrinketHandler(entity, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventLivingJump(stack, entity);
            }
         }

      });
   }

   @SubscribeEvent
   public void livingFall(LivingFallEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      TrinketHelper.getTrinketHandler(entity, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventLivingFall(stack, entity, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void TargetEvent(LivingSetAttackTargetEvent event) {
      EntityLivingBase entity = event.getTarget();
      TrinketHelper.getTrinketHandler(entity, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventSetAttackTarget(stack, entity, event.getEntityLiving());
            }
         }

      });
   }

   @SubscribeEvent
   public void HurtEvent(LivingHurtEvent event) {
      EntityLivingBase attacked = event.getEntityLiving();
      TrinketHelper.getTrinketHandler(attacked, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventLivingHurtAttacked(stack, attacked, event);
            }
         }

      });
      if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
         TrinketHelper.getTrinketHandler(attacker, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventLivingHurtAttacker(stack, attacker, event);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void deathEvent(LivingDamageEvent event) {
      EntityLivingBase attacked = event.getEntityLiving();
      TrinketHelper.getTrinketHandler(attacked, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventLivingDamageAttacked(stack, attacked, event);
            }
         }

      });
      if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
         TrinketHelper.getTrinketHandler(attacker, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventLivingDamageAttacker(stack, attacker, event);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void experienceDropEvent(LivingExperienceDropEvent event) {
      EntityPlayer player = event.getAttackingPlayer();
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventLivingExperienceDrops(stack, player, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void ItemDropEvent(LivingDropsEvent event) {
      if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)event.getSource().getTrueSource();
         TrinketHelper.getTrinketHandler(entity, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventLivingDrops(stack, entity, event);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void onAttemptToBreakBlock(BreakSpeed event) {
      EntityPlayer player = event.getEntityPlayer();
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventBreakSpeed(stack, player, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void onBlockBroken(BreakEvent event) {
      EntityPlayer player = event.getPlayer();
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventBlockBreak(stack, player, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void onBlockDrops(HarvestDropsEvent event) {
      EntityPlayer player = event.getHarvester();
      TrinketHelper.getTrinketHandler(player, (handler) -> {
         for(int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
               item.eventBlockDrops(stack, player, event);
            }
         }

      });
   }

   @SubscribeEvent
   public void playerDeath(PlayerDropsEvent event) {
      if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote && !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
         this.dropItemsAt(event.getEntityPlayer(), event.getDrops(), event.getEntityPlayer());
      }

   }

   public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
      TrinketHelper.getTrinketHandler(player, (Trinket) -> {
         for(int i = 0; i < Trinket.getSlots(); ++i) {
            if (!Trinket.getStackInSlot(i).isEmpty()) {
               EntityItem ei = new EntityItem(e.world, e.posX, e.posY + (double)e.getEyeHeight(), e.posZ, Trinket.getStackInSlot(i).copy());
               ei.setPickupDelay(40);
               float f1 = e.world.rand.nextFloat() * 0.5F;
               float f2 = e.world.rand.nextFloat() * 3.1415927F * 2.0F;
               ei.motionX = (double)(-MathHelper.sin(f2) * f1);
               ei.motionZ = (double)(MathHelper.cos(f2) * f1);
               ei.motionY = 0.20000000298023224D;
               drops.add(ei);
               Trinket.setStackInSlot(i, ItemStack.EMPTY);
            }
         }

      });
   }

   @SubscribeEvent
   public void PlayerCloneEvent(Clone event) {
      try {
         TrinketContainerHandler bco = (TrinketContainerHandler)event.getOriginal().getCapability(TrinketContainerProvider.containerCap, (EnumFacing)null);
         NBTTagCompound nbt = bco.serializeNBT();
         TrinketContainerHandler bcn = (TrinketContainerHandler)event.getEntityPlayer().getCapability(TrinketContainerProvider.containerCap, (EnumFacing)null);
         bcn.deserializeNBT(nbt);
      } catch (Exception var5) {
         ;
      }

   }
}
