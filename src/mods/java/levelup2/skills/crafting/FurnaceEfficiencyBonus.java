package levelup2.skills.crafting;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Lists;
import java.util.ConcurrentModificationException;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import levelup2.api.IProcessor;
import levelup2.capability.CapabilityFurnace;
import levelup2.capability.PlayerCapability;
import levelup2.skills.BaseSkill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class FurnaceEfficiencyBonus extends BaseSkill {
   public FurnaceEfficiencyBonus() {
   }

   public String getSkillName() {
      return "levelup:furnacespeed";
   }

   public byte getSkillType() {
      return 1;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Blocks.FURNACE);
   }

   public void register(EventBus bus) {
      bus.registerGeneric(AttachCapabilitiesEvent.class, TileEntity.class, this::registerFurnaceCapability);
      bus.register(RightClickBlock.class, this::onTileInteracted);
      bus.register(WorldTickEvent.class, this::doFurnaceTicks);
   }

   public void registerFurnaceCapability(AttachCapabilitiesEvent<TileEntity> evt) {
      if (evt.getObject() instanceof TileEntityFurnace) {
         final TileEntityFurnace furnace = (TileEntityFurnace)evt.getObject();
         evt.addCapability(new ResourceLocation("levelup", "furnacemods"), new ICapabilitySerializable<NBTTagCompound>() {
            IProcessor instance = new CapabilityFurnace(furnace);

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == PlayerCapability.MACHINE_PROCESSING;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == PlayerCapability.MACHINE_PROCESSING ? PlayerCapability.MACHINE_PROCESSING.cast(this.instance) : null);
            }

            public NBTTagCompound serializeNBT() {
               return (NBTTagCompound)PlayerCapability.MACHINE_PROCESSING.getStorage().writeNBT(PlayerCapability.MACHINE_PROCESSING, this.instance, (EnumFacing)null);
            }

            public void deserializeNBT(NBTTagCompound tag) {
               PlayerCapability.MACHINE_PROCESSING.getStorage().readNBT(PlayerCapability.MACHINE_PROCESSING, this.instance, (EnumFacing)null, tag);
            }
         });
      }

   }

   public void onTileInteracted(RightClickBlock evt) {
      if (!evt.getWorld().isRemote && evt.getEntityPlayer() != null) {
         EntityPlayer player = evt.getEntityPlayer();
         if (player instanceof FakePlayer || !player.isSneaking() || !evt.getItemStack().isEmpty()) {
            return;
         }

         TileEntity tile = evt.getWorld().getTileEntity(evt.getPos());
         if (tile != null && tile.hasCapability(PlayerCapability.MACHINE_PROCESSING, EnumFacing.UP)) {
            IProcessor cap = (IProcessor)tile.getCapability(PlayerCapability.MACHINE_PROCESSING, EnumFacing.UP);
            if (cap != null) {
               String name = UsernameCache.getLastKnownUsername(player.getGameProfile().getId());
               if (cap.getPlayerFromUUID() == null) {
                  player.sendStatusMessage(new TextComponentTranslation("levelup.interact.register", new Object[]{name}), true);
                  cap.setUUID(player.getGameProfile().getId());
               } else if (cap.getPlayerFromUUID().getGameProfile().getId() == player.getGameProfile().getId()) {
                  player.sendStatusMessage(new TextComponentTranslation("levelup.interact.unregister", new Object[]{name}), true);
                  cap.setUUID((EaglercraftUUID)null);
               } else {
                  name = UsernameCache.getLastKnownUsername(cap.getPlayerFromUUID().getGameProfile().getId());
                  player.sendStatusMessage(new TextComponentTranslation("levelup.interact.notowned", new Object[]{name}), true);
               }
            }
         }
      }

   }

   public void doFurnaceTicks(WorldTickEvent evt) {
      if (!evt.world.isRemote && evt.phase != Phase.END && evt.side == Side.SERVER) {
         List<IProcessor> tiles = Lists.newArrayList();

         try {
            synchronized(evt.world.loadedTileEntityList) {
               tiles = (List)evt.world.loadedTileEntityList.stream().filter((t) -> {
                  return t != null && t.hasCapability(PlayerCapability.MACHINE_PROCESSING, EnumFacing.UP);
               }).map((t) -> {
                  return (IProcessor)t.getCapability(PlayerCapability.MACHINE_PROCESSING, EnumFacing.UP);
               }).collect(Collectors.toList());
            }
         } catch (ConcurrentModificationException var6) {
            ;
         }

         tiles.forEach(this::processTick);
      }
   }

   private void processTick(IProcessor tile) {
      if (tile != null && tile.getPlayerFromUUID() != null) {
         tile.extraProcessing(tile.getPlayerFromUUID());
      }

   }
}
