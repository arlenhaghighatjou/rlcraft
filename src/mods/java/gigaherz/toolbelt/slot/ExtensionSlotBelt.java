package gigaherz.toolbelt.slot;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.Config;
import gigaherz.toolbelt.ToolBelt;
import gigaherz.toolbelt.customslots.ExtensionSlotItemHandler;
import gigaherz.toolbelt.customslots.IExtensionContainer;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import gigaherz.toolbelt.network.SyncBeltSlotContents;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ExtensionSlotBelt implements IExtensionContainer, INBTSerializable<NBTTagCompound> {
   private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("toolbelt", "belt_slot");
   public static Capability<ExtensionSlotBelt> CAPABILITY = null;
   public static final ResourceLocation BELT = new ResourceLocation("examplemod", "belt");
   private final EntityLivingBase owner;
   private final ItemStackHandler inventory;
   private final ExtensionSlotItemHandler belt;
   private final ImmutableList<IExtensionSlot> slots;

   public static void register() {
      CapabilityManager.INSTANCE.register(ExtensionSlotBelt.class, new IStorage<ExtensionSlotBelt>() {
         @Nullable
         public NBTBase writeNBT(Capability<ExtensionSlotBelt> capability, ExtensionSlotBelt instance, EnumFacing side) {
            return instance.serializeNBT();
         }

         public void readNBT(Capability<ExtensionSlotBelt> capability, ExtensionSlotBelt instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound)nbt);
         }
      }, () -> {
         throw new UnsupportedOperationException("Cannot instantiate extension slots without a player, use the class constructor.");
      });
      (new ExtensionSlotBelt.AttachHandlers()).register(MinecraftForge.EVENT_BUS);
      if (Config.customBeltSlotEnabled) {
         (new ExtensionSlotBelt.EventHandlers()).register(MinecraftForge.EVENT_BUS);
      } else {
         (new ExtensionSlotBelt.EventHandlers.Disabled()).register(MinecraftForge.EVENT_BUS);
      }

   }

   public static ExtensionSlotBelt get(EntityLivingBase player) {
      return (ExtensionSlotBelt)player.getCapability(CAPABILITY, (EnumFacing)null);
   }

   private void dropContents() {
      UnmodifiableIterator var1 = this.slots.iterator();

      while(var1.hasNext()) {
         IExtensionSlot slot = (IExtensionSlot)var1.next();
         ItemStack stack = slot.getContents();
         if (stack.getCount() > 0) {
            if (this.owner instanceof EntityPlayer) {
               ItemHandlerHelper.giveItemToPlayer((EntityPlayer)this.owner, stack);
            } else {
               this.owner.entityDropItem(stack, 0.1F);
            }

            slot.setContents(ItemStack.EMPTY);
         }
      }

   }

   private void syncToSelf() {
      this.syncTo((EntityPlayerMP)this.owner);
   }

   protected void syncTo(EntityPlayer target) {
      SyncBeltSlotContents message = new SyncBeltSlotContents((EntityPlayer)this.owner, this);
      ToolBelt.channel.sendTo(message, (EntityPlayerMP)target);
   }

   protected void syncToSelfAndTracking() {
      SyncBeltSlotContents message = new SyncBeltSlotContents((EntityPlayer)this.owner, this);
      ToolBelt.channel.sendToAllTracking(message, this.owner);
      ToolBelt.channel.sendTo(message, (EntityPlayerMP)this.owner);
   }

   private ExtensionSlotBelt(EntityLivingBase owner) {
      this.inventory = new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ExtensionSlotBelt.this.belt.onContentsChanged();
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !Config.customBeltSlotEnabled ? stack : super.insertItem(slot, stack, simulate);
         }

         public int getSlotLimit(int slot) {
            return !Config.customBeltSlotEnabled ? 0 : super.getSlotLimit(slot);
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !Config.customBeltSlotEnabled ? false : super.isItemValid(slot, stack);
         }
      };
      this.belt = new ExtensionSlotItemHandler(this, BELT, this.inventory, 0);
      this.slots = ImmutableList.of(this.belt);
      this.owner = owner;
   }

   @Nonnull
   public EntityLivingBase getOwner() {
      return this.owner;
   }

   @Nonnull
   public ImmutableList<IExtensionSlot> getSlots() {
      return this.slots;
   }

   public void onContentsChanged(IExtensionSlot slot) {
   }

   @Nonnull
   public IExtensionSlot getBelt() {
      return this.belt;
   }

   private void tickAllSlots() {
      UnmodifiableIterator var1 = this.slots.iterator();

      while(var1.hasNext()) {
         IExtensionSlot slot = (IExtensionSlot)var1.next();
         ((ExtensionSlotItemHandler)slot).onWornTick();
      }

   }

   public void setAll(NonNullList<ItemStack> stacks) {
      List<IExtensionSlot> slots = this.getSlots();

      for(int i = 0; i < slots.size(); ++i) {
         ((IExtensionSlot)slots.get(i)).setContents((ItemStack)stacks.get(i));
      }

   }

   public NBTTagCompound serializeNBT() {
      return this.inventory.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.inventory.deserializeNBT(nbt);
   }

   public static class AttachHandlers {
      private AttachHandlers() {
      }

      public void register(EventBus bus) {
         bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, this::attachCapabilities);
      }

      public void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(ExtensionSlotBelt.CAPABILITY_ID, new ICapabilitySerializable<NBTTagCompound>() {
               final ExtensionSlotBelt extensionContainer = new ExtensionSlotBelt((EntityPlayer)event.getObject()) {
                  public void onContentsChanged(IExtensionSlot slot) {
                     if (Config.customBeltSlotEnabled && !this.getOwner().world.isRemote) {
                        this.syncToSelfAndTracking();
                     }

                  }
               };

               public NBTTagCompound serializeNBT() {
                  return this.extensionContainer.serializeNBT();
               }

               public void deserializeNBT(NBTTagCompound nbt) {
                  this.extensionContainer.deserializeNBT(nbt);
               }

               public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                  return capability == ExtensionSlotBelt.CAPABILITY;
               }

               @Nullable
               public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                  return (T)(capability == ExtensionSlotBelt.CAPABILITY ? this.extensionContainer : null);
               }
            });
         }

      }
   }

   public static class EventHandlers {
      private EaglercraftRandom rand;

      private EventHandlers() {
         this.rand = new EaglercraftRandom();
      }

      public void register(EventBus bus) {
         bus.register(PlayerLoggedInEvent.class, this::joinWorld);
         bus.register(PlayerChangedDimensionEvent.class, this::joinWorld);
         bus.register(StartTracking.class, this::track);
         bus.register(PlayerTickEvent.class, this::entityTick);
         bus.register(PlayerDropsEvent.class, this::playerDeath);
         bus.register(Clone.class, this::playerClone);
      }

      public void joinWorld(PlayerLoggedInEvent event) {
         EntityPlayer target = event.player;
         if (!target.world.isRemote) {
            ExtensionSlotBelt instance = ExtensionSlotBelt.get(target);
            if (Config.customBeltSlotEnabled) {
               if (instance == null) {
                  return;
               }

               instance.syncToSelf();
            }

         }
      }

      public void joinWorld(PlayerChangedDimensionEvent event) {
         EntityPlayer target = event.player;
         if (!target.world.isRemote) {
            ExtensionSlotBelt instance = ExtensionSlotBelt.get(target);
            if (Config.customBeltSlotEnabled) {
               if (instance == null) {
                  return;
               }

               instance.syncToSelf();
            }

         }
      }

      public void track(StartTracking event) {
         Entity target = event.getTarget();
         if (!target.world.isRemote) {
            if (target instanceof EntityPlayer) {
               ExtensionSlotBelt instance = ExtensionSlotBelt.get((EntityLivingBase)target);
               if (Config.customBeltSlotEnabled) {
                  if (instance == null) {
                     return;
                  }

                  instance.syncTo(event.getEntityPlayer());
               }
            }

         }
      }

      public void entityTick(PlayerTickEvent event) {
         ExtensionSlotBelt instance = ExtensionSlotBelt.get(event.player);
         if (instance != null) {
            instance.tickAllSlots();
         }
      }

      public void playerDeath(PlayerDropsEvent event) {
         EntityPlayer player = event.getEntityPlayer();
         ExtensionSlotBelt instance = ExtensionSlotBelt.get(player);
         if (instance != null) {
            IExtensionSlot belt = instance.getBelt();
            ItemStack stack = belt.getContents();
            if (EnchantmentHelper.hasVanishingCurse(stack)) {
               stack = ItemStack.EMPTY;
               belt.setContents(stack);
            }

            if (!player.world.getGameRules().getBoolean("keepInventory") && !player.isSpectator() && stack.getCount() > 0) {
               event.getDrops().add(this.prepareDrop(player, stack, true));
               belt.setContents(ItemStack.EMPTY);
            }

         }
      }

      public void playerClone(Clone event) {
         EntityPlayer oldPlayer = event.getOriginal();
         EntityPlayer newPlayer = event.getEntityPlayer();
         ExtensionSlotBelt oldBelt = ExtensionSlotBelt.get(oldPlayer);
         ExtensionSlotBelt newBelt = ExtensionSlotBelt.get(newPlayer);
         if (oldBelt != null) {
            ItemStack stack = oldBelt.getBelt().getContents();
            if (newBelt == null) {
               if (stack.getCount() > 0) {
                  oldPlayer.dropItem(stack, true, false);
               }
            } else {
               newBelt.getBelt().setContents(stack);
            }

         }
      }

      @Nullable
      private EntityItem prepareDrop(EntityPlayer player, ItemStack droppedItem, boolean dropAround) {
         if (droppedItem.isEmpty()) {
            return null;
         } else {
            double d0 = player.posY - 0.30000001192092896D + (double)player.getEyeHeight();
            EntityItem entityitem = new EntityItem(player.world, player.posX, d0, player.posZ, droppedItem);
            entityitem.setPickupDelay(40);
            if (dropAround) {
               float f = this.rand.nextFloat() * 0.5F;
               float f1 = this.rand.nextFloat() * 6.2831855F;
               entityitem.motionX = (double)(-MathHelper.sin(f1) * f);
               entityitem.motionZ = (double)(MathHelper.cos(f1) * f);
               entityitem.motionY = 0.20000000298023224D;
            } else {
               float f2 = 0.3F;
               entityitem.motionX = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * MathHelper.cos(player.rotationPitch * 0.017453292F) * f2);
               entityitem.motionZ = (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * MathHelper.cos(player.rotationPitch * 0.017453292F) * f2);
               entityitem.motionY = (double)(-MathHelper.sin(player.rotationPitch * 0.017453292F) * f2 + 0.1F);
               float f3 = this.rand.nextFloat() * 6.2831855F;
               f2 = 0.02F * this.rand.nextFloat();
               entityitem.motionX += Math.cos((double)f3) * (double)f2;
               entityitem.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
               entityitem.motionZ += Math.sin((double)f3) * (double)f2;
            }

            return entityitem;
         }
      }

      static class Disabled {
         Disabled() {
         }

         public void register(EventBus bus) {
            bus.register(PlayerTickEvent.class, this::entityTick);
         }

         public void entityTick(PlayerTickEvent event) {
            ExtensionSlotBelt instance = ExtensionSlotBelt.get(event.player);
            if (instance != null) {
               instance.dropContents();
            }
         }
      }
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(ExtensionSlotBelt.class, capability -> CAPABILITY = capability);
   }
}
