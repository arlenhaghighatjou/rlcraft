package xzeroair.trinkets.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.api.ITrinketInterface;
import xzeroair.trinkets.blocks.tileentities.TileEntityMoonRose;
import xzeroair.trinkets.blocks.tileentities.TileEntityTeddyBear;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.TrinketContainerProvider;
import xzeroair.trinkets.capabilities.TileEntityCap.TileEntityProperties;
import xzeroair.trinkets.capabilities.Trinket.TrinketProperties;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.container.TrinketContainerHandler;
import xzeroair.trinkets.util.TrinketsConfig;

public class CapabilitiesHandler {
   public CapabilitiesHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onAddCapabilites(AttachCapabilitiesEvent<Entity> event) {
      Entity entity = (Entity)event.getObject();
      boolean isLiving = event.getObject() instanceof EntityLivingBase;
      if (isLiving) {
         try {
            if (!entity.isNonBoss()) {
               return;
            }
         } catch (Exception var5) {
            return;
         }

         boolean isPlayer = event.getObject() instanceof EntityPlayer;
         if (isPlayer) {
            event.addCapability(new ResourceLocation("xat", "container"), new TrinketContainerProvider(new TrinketContainerHandler()));
            if (!entity.hasCapability(Capabilities.VIP_STATUS, (EnumFacing)null)) {
               event.addCapability(new ResourceLocation("xat", "VIP"), new CapabilityProviderBase(Capabilities.VIP_STATUS, new VipStatus((EntityPlayer)entity)));
            }
         }

         if (!entity.hasCapability(Capabilities.STATUS_HANDLER, (EnumFacing)null)) {
            event.addCapability(new ResourceLocation("xat", "StatusHandler"), new CapabilityProviderBase(Capabilities.STATUS_HANDLER, new StatusHandler((EntityLivingBase)entity)));
         }

         if (!entity.hasCapability(Capabilities.ENTITY_PROPERTIES, (EnumFacing)null)) {
            if (TrinketsConfig.SERVER.Potion.players_only) {
               if (isPlayer) {
                  event.addCapability(new ResourceLocation("xat", "Race"), new CapabilityProviderBase(Capabilities.ENTITY_PROPERTIES, new EntityProperties((EntityPlayer)entity)));
               }
            } else {
               event.addCapability(new ResourceLocation("xat", "Race"), new CapabilityProviderBase(Capabilities.ENTITY_PROPERTIES, new EntityProperties((EntityLivingBase)entity)));
            }
         }

         if (!entity.hasCapability(Capabilities.ENTITY_MAGIC, (EnumFacing)null)) {
            event.addCapability(new ResourceLocation("xat", "MagicStats"), new CapabilityProviderBase(Capabilities.ENTITY_MAGIC, new MagicStats((EntityLivingBase)entity)));
         }
      }

   }

   @SubscribeEvent
   public void TileEntityCapability(AttachCapabilitiesEvent<TileEntity> event) {
      TileEntity TileEntity = (TileEntity)event.getObject();
      if (event.getObject() instanceof TileEntityMoonRose && !TileEntity.hasCapability(Capabilities.TILE_ENTITY_PROPERTIES, (EnumFacing)null)) {
         event.addCapability(new ResourceLocation("xat", "ManaEssence"), new CapabilityProviderBase(Capabilities.TILE_ENTITY_PROPERTIES, (new TileEntityProperties(TileEntity)).setHasEssence(true)));
      }

      if (event.getObject() instanceof TileEntityTeddyBear && !TileEntity.hasCapability(Capabilities.TILE_ENTITY_PROPERTIES, (EnumFacing)null)) {
         event.addCapability(new ResourceLocation("xat", "TrinketTE"), new CapabilityProviderBase(Capabilities.TILE_ENTITY_PROPERTIES, (new TileEntityProperties(TileEntity)).setHasEssence(false)));
      }

   }

   @SubscribeEvent
   public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event) {
      ItemStack stack = (ItemStack)event.getObject();
      if (!stack.isEmpty() && stack.getItem() instanceof ITrinketInterface && !stack.hasCapability(Capabilities.ITEM_TRINKET, (EnumFacing)null)) {
         event.addCapability(new ResourceLocation("xat", "Trinket"), new CapabilityProviderBase(Capabilities.ITEM_TRINKET, new TrinketProperties(stack)));
      }

   }
}
