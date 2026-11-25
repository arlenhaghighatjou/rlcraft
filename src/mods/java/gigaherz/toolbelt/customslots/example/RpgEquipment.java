package gigaherz.toolbelt.customslots.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.customslots.ExtensionSlotItemHandler;
import gigaherz.toolbelt.customslots.IExtensionContainer;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.items.ItemStackHandler;

public class RpgEquipment implements IExtensionContainer, INBTSerializable<NBTTagCompound> {
   private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("examplemod", "rpg_inventory");
   @CapabilityInject(RpgEquipment.class)
   public static Capability<RpgEquipment> CAPABILITY = null;
   public static final ResourceLocation RING = new ResourceLocation("examplemod", "ring");
   public static final ResourceLocation TRINKET = new ResourceLocation("examplemod", "trinket");
   public static final ResourceLocation NECK = new ResourceLocation("examplemod", "neck");
   public static final ResourceLocation BELT = new ResourceLocation("examplemod", "belt");
   public static final ResourceLocation WRISTS = new ResourceLocation("examplemod", "wrists");
   public static final ResourceLocation ANKLES = new ResourceLocation("examplemod", "ankles");
   private final EntityLivingBase owner;
   private final ItemStackHandler inventory;
   private final ExtensionSlotItemHandler ring1;
   private final ExtensionSlotItemHandler ring2;
   private final ExtensionSlotItemHandler trinket1;
   private final ExtensionSlotItemHandler trinket2;
   private final ExtensionSlotItemHandler neck;
   private final ExtensionSlotItemHandler belt;
   private final ExtensionSlotItemHandler wrists;
   private final ExtensionSlotItemHandler ankles;
   private final ImmutableList<IExtensionSlot> slots;

   public static void register() {
      CapabilityManager.INSTANCE.register(RpgEquipment.class, new IStorage<RpgEquipment>() {
         @Nullable
         public NBTBase writeNBT(Capability<RpgEquipment> capability, RpgEquipment instance, EnumFacing side) {
            return null;
         }

         public void readNBT(Capability<RpgEquipment> capability, RpgEquipment instance, EnumFacing side, NBTBase nbt) {
         }
      }, () -> {
         return (void)null;
      });
      MinecraftForge.EVENT_BUS.register(new RpgEquipment.EventHandlers());
   }

   public static RpgEquipment get(EntityPlayer player) {
      return (RpgEquipment)player.getCapability(CAPABILITY, (EnumFacing)null);
   }

   private RpgEquipment(EntityLivingBase owner) {
      this.inventory = new ItemStackHandler(8);
      this.ring1 = new ExtensionSlotItemHandler(this, RING, this.inventory, 0);
      this.ring2 = new ExtensionSlotItemHandler(this, RING, this.inventory, 1);
      this.trinket1 = new ExtensionSlotItemHandler(this, TRINKET, this.inventory, 2);
      this.trinket2 = new ExtensionSlotItemHandler(this, TRINKET, this.inventory, 3);
      this.neck = new ExtensionSlotItemHandler(this, NECK, this.inventory, 4);
      this.belt = new ExtensionSlotItemHandler(this, BELT, this.inventory, 5);
      this.wrists = new ExtensionSlotItemHandler(this, WRISTS, this.inventory, 6);
      this.ankles = new ExtensionSlotItemHandler(this, ANKLES, this.inventory, 7);
      this.slots = ImmutableList.of(this.ring1, this.ring2, this.trinket1, this.trinket2, this.neck, this.belt, this.wrists, this.ankles);
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
   public IExtensionSlot getRing1() {
      return this.ring1;
   }

   @Nonnull
   public IExtensionSlot getRing2() {
      return this.ring2;
   }

   @Nonnull
   public IExtensionSlot getTrinket1() {
      return this.trinket1;
   }

   @Nonnull
   public IExtensionSlot getTrinket2() {
      return this.trinket2;
   }

   @Nonnull
   public IExtensionSlot getNeck() {
      return this.neck;
   }

   @Nonnull
   public IExtensionSlot getBelt() {
      return this.belt;
   }

   @Nonnull
   public IExtensionSlot getWrists() {
      return this.wrists;
   }

   @Nonnull
   public IExtensionSlot getAnkles() {
      return this.ankles;
   }

   private void tickAllSlots() {
      UnmodifiableIterator var1 = this.slots.iterator();

      while(var1.hasNext()) {
         IExtensionSlot slot = (IExtensionSlot)var1.next();
         ((ExtensionSlotItemHandler)slot).onWornTick();
      }

   }

   public NBTTagCompound serializeNBT() {
      return this.inventory.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.inventory.deserializeNBT(nbt);
   }

   static class EventHandlers {
      EventHandlers() {
      }

      @SubscribeEvent
      public void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(RpgEquipment.CAPABILITY_ID, new ICapabilitySerializable<NBTTagCompound>() {
               final RpgEquipment extensionContainer = new RpgEquipment((EntityPlayer)event.getObject());

               public NBTTagCompound serializeNBT() {
                  return this.extensionContainer.serializeNBT();
               }

               public void deserializeNBT(NBTTagCompound nbt) {
                  this.extensionContainer.deserializeNBT(nbt);
               }

               public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                  return capability == RpgEquipment.CAPABILITY;
               }

               @Nullable
               public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                  return (T)(capability == RpgEquipment.CAPABILITY ? this.extensionContainer : null);
               }
            });
         }

      }

      @SubscribeEvent
      public void entityTick(PlayerTickEvent event) {
         RpgEquipment instance = RpgEquipment.get(event.player);
         if (instance != null) {
            instance.tickAllSlots();
         }
      }
   }
}
