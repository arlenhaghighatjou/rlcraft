package xzeroair.trinkets.events;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
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
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.SyncItemDataPacket;
import xzeroair.trinkets.util.compat.baubles.BaublesHelper;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class BaubleEventHandler {
   public BaubleEventHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void clientTickEvent(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player == null || !player.isEntityAlive() || player.world == null) {
            return;
         }

         BaublesHelper.getBaublesHandler(player, (handler) -> {
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
         BaublesHelper.getBaublesHandler(player, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               if (!client && world instanceof WorldServer) {
                  if (!empty) {
                     Capabilities.getTrinketProperties(stack, (properties) -> {
                        properties.itemEquipped(stack, player);
                     });
                  }

                  SyncItemDataPacket packet = new SyncItemDataPacket(player, stack, stack.getTagCompound(), i, TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES, true, !empty);
                  NetworkHandler.sendTo(packet, (EntityPlayerMP)player);
               }

               if (!empty && stack.getItem() instanceof IAccessoryInterface) {
                  IAccessoryInterface item = (IAccessoryInterface)stack.getItem();
                  item.eventPlayerLogin(stack, player);
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void EntityJoinWorld(EntityJoinWorldEvent event) {
   }

   @SubscribeEvent
   public void PlayerLoggedOutEvent(PlayerLoggedOutEvent event) {
      EntityPlayer player = event.player;
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
         BaublesHelper.getBaublesHandler(targetPlayer, (handler) -> {
            for(int i = 0; i < handler.getSlots(); ++i) {
               ItemStack stack = handler.getStackInSlot(i);
               boolean empty = stack.isEmpty();
               SyncItemDataPacket packet = new SyncItemDataPacket(targetPlayer, stack, stack.getTagCompound(), i, TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES, true, !empty);
               NetworkHandler.sendTo(packet, (EntityPlayerMP)player);
            }

         });
      }

   }

   @SubscribeEvent
   public void PlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      EntityPlayer player = event.player;
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
            BaublesHelper.getBaublesHandler(player, (handler) -> {
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
   public void potionApplicable(PotionApplicableEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      BaublesHelper.getBaublesHandler(entity, (handler) -> {
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
      BaublesHelper.getBaublesHandler(entity, (handler) -> {
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
      BaublesHelper.getBaublesHandler(entity, (handler) -> {
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
      EntityLivingBase entity = event.getEntityLiving();
      BaublesHelper.getBaublesHandler(entity, (handler) -> {
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
      BaublesHelper.getBaublesHandler(attacked, (handler) -> {
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
         BaublesHelper.getBaublesHandler(attacker, (handler) -> {
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
      BaublesHelper.getBaublesHandler(attacked, (handler) -> {
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
         BaublesHelper.getBaublesHandler(attacker, (handler) -> {
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
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
         BaublesHelper.getBaublesHandler(entity, (handler) -> {
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
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
      BaublesHelper.getBaublesHandler(player, (handler) -> {
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
}
