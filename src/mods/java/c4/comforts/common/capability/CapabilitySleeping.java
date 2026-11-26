package c4.comforts.common.capability;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import c4.comforts.common.ConfigHandler;
import c4.comforts.common.blocks.BlockSleepingBag;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;

public class CapabilitySleeping {
   public static Capability<CapabilitySleeping.ISleeping> SLEEP_CAP = null;
   public static final EnumFacing DEFAULT_FACING = null;
   public static final ResourceLocation ID = new ResourceLocation("comforts", "sleeping");
   private static final String SLEEP_TAG = "Sleeping";
   private static final String POS_TAG = "Pos";

   public CapabilitySleeping() {
   }

   public static void register() {
      CapabilityManager.INSTANCE.register(CapabilitySleeping.ISleeping.class, new IStorage<CapabilitySleeping.ISleeping>() {
         public NBTBase writeNBT(Capability<CapabilitySleeping.ISleeping> capability, CapabilitySleeping.ISleeping instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("Sleeping", instance.isSleeping());
            compound.setTag("Pos", NBTUtil.createPosTag(instance.getPos()));
            return compound;
         }

         public void readNBT(Capability<CapabilitySleeping.ISleeping> capability, CapabilitySleeping.ISleeping instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            instance.setSleeping(compound.getBoolean("Sleeping"));
            instance.setPos(NBTUtil.getPosFromTag(compound.getCompoundTag("Pos")));
         }
      }, CapabilitySleeping.Sleeping::new);
      if (ConfigHandler.autoUse) {
         (new CapabilitySleeping.EventHandler()).register(MinecraftForge.EVENT_BUS);
      }

   }

   @Nullable
   public static CapabilitySleeping.ISleeping getSleeping(EntityPlayer playerIn) {
      return playerIn != null && playerIn.hasCapability(SLEEP_CAP, DEFAULT_FACING) ? (CapabilitySleeping.ISleeping)playerIn.getCapability(SLEEP_CAP, DEFAULT_FACING) : null;
   }

   public static ICapabilityProvider createProvider(CapabilitySleeping.ISleeping sleeping) {
      return new CapabilitySleeping.Provider(sleeping, SLEEP_CAP, DEFAULT_FACING);
   }

   private static class EventHandler {
      private EventHandler() {
      }

      public void register(EventBus bus) {
         bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, this::attachCapabilities);
         bus.register(PlayerTickEvent.class, this::onPostPlayerTick);
         bus.register(PlayerWakeUpEvent.class, this::onPlayerWakeUp);
      }

      public void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
         if (evt.getObject() instanceof EntityPlayer) {
            evt.addCapability(CapabilitySleeping.ID, CapabilitySleeping.createProvider(new CapabilitySleeping.Sleeping()));
         }

      }

      public void onPostPlayerTick(PlayerTickEvent evt) {
         if (evt.phase == Phase.END && evt.side == Side.SERVER) {
            EntityPlayer player = evt.player;
            CapabilitySleeping.ISleeping sleeping = CapabilitySleeping.getSleeping(player);
            if (!player.isPlayerSleeping() && sleeping != null && sleeping.isSleeping()) {
               World world = player.world;
               BlockPos blockpos = sleeping.getPos();
               IBlockState state = world.getBlockState(blockpos);
               if (world.isBlockLoaded(blockpos) && state.getBlock() instanceof BlockSleepingBag) {
                  SleepResult sleepResult = ((BlockSleepingBag)state.getBlock()).doSleep(world, blockpos, state, player);
                  if (sleepResult != SleepResult.OK) {
                     sleeping.setSleeping(false);
                     sleeping.setPos(BlockPos.ORIGIN);
                  }
               } else {
                  sleeping.setSleeping(false);
                  sleeping.setPos(BlockPos.ORIGIN);
               }
            }
         }

      }

      public void onPlayerWakeUp(PlayerWakeUpEvent evt) {
         EntityPlayer player = evt.getEntityPlayer();
         World world = player.world;
         if (!world.isRemote && player.bedLocation != null) {
            BlockPos pos = player.bedLocation;
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockSleepingBag) {
               CapabilitySleeping.ISleeping sleeping = CapabilitySleeping.getSleeping(player);
               if (sleeping != null && sleeping.isSleeping()) {
                  sleeping.setSleeping(false);
                  sleeping.setPos(BlockPos.ORIGIN);
                  ItemStack stack = state.getBlock().getPickBlock(state, (RayTraceResult)null, world, pos, player);
                  BlockPos pos1 = pos.offset(((EnumFacing)state.getValue(BlockSleepingBag.FACING)).getOpposite());
                  world.setBlockToAir(pos);
                  world.setBlockToAir(pos1);
                  if (!player.capabilities.isCreativeMode) {
                     ItemHandlerHelper.giveItemToPlayer(player, stack, player.inventory.currentItem);
                  }
               }
            }
         }

      }
   }

   public interface ISleeping {
      boolean isSleeping();

      void setSleeping(boolean var1);

      BlockPos getPos();

      void setPos(BlockPos var1);
   }

   public static class Provider implements ICapabilitySerializable<NBTBase> {
      final Capability<CapabilitySleeping.ISleeping> capability;
      final EnumFacing facing;
      final CapabilitySleeping.ISleeping instance;

      Provider(CapabilitySleeping.ISleeping instance, Capability<CapabilitySleeping.ISleeping> capability, @Nullable EnumFacing facing) {
         this.instance = instance;
         this.capability = capability;
         this.facing = facing;
      }

      public boolean hasCapability(@Nullable Capability<?> capability, EnumFacing facing) {
         return capability == this.getCapability();
      }

      public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
         return (T)(capability == this.getCapability() ? this.getCapability().cast(this.instance) : null);
      }

      final Capability<CapabilitySleeping.ISleeping> getCapability() {
         return this.capability;
      }

      EnumFacing getFacing() {
         return this.facing;
      }

      final CapabilitySleeping.ISleeping getInstance() {
         return this.instance;
      }

      public NBTBase serializeNBT() {
         return this.getCapability().writeNBT(this.getInstance(), this.getFacing());
      }

      public void deserializeNBT(NBTBase nbt) {
         this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
      }
   }

   public static class Sleeping implements CapabilitySleeping.ISleeping {
      private boolean isSleeping = false;
      private BlockPos blockPos = BlockPos.ORIGIN;

      public Sleeping() {
      }

      public boolean isSleeping() {
         return this.isSleeping;
      }

      public void setSleeping(boolean value) {
         this.isSleeping = value;
      }

      public BlockPos getPos() {
         return this.blockPos;
      }

      public void setPos(BlockPos pos) {
         this.blockPos = pos;
      }
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(CapabilitySleeping.ISleeping.class, capability -> SLEEP_CAP = capability);
   }
}
