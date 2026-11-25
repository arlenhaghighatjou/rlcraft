package net.mcft.copy.backpacks.api;

import baubles.api.BaublesApi;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.item.ItemBackpack;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public final class BackpackHelper {
   public static boolean equipAsChestArmor = false;
   public static double INTERACT_MAX_DISTANCE = 1.8D;
   public static double INTERACT_MAX_ANGLE = 110.0D;
   public static Logger LOG = LogManager.getLogger("wearablebackpacks:api");

   private BackpackHelper() {
   }

   public static IBackpack getBackpack(Entity entity) {
      if (entity == null) {
         return null;
      } else {
         IBackpack backpack = (IBackpack)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         return backpack != null && !backpack.getStack().isEmpty() ? backpack : null;
      }
   }

   public static IBackpack getBackpack(TileEntity entity) {
      return entity != null ? (IBackpack)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null) : null;
   }

   public static IBackpackType getBackpackType(ItemStack stack) {
      return !stack.isEmpty() ? getBackpackType(stack.getItem()) : null;
   }

   public static IBackpackType getBackpackType(Item item) {
      return item instanceof IBackpackType ? (IBackpackType)item : null;
   }

   public static boolean canEquipBackpack(EntityLivingBase entity) {
      return entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null) != null && getBackpack(entity) == null && (!ModConfig.server.equipAsBauble || !(entity instanceof EntityPlayer) || isBackpackBaubleSlotEmpty((EntityPlayer)entity));
   }

   public static boolean isBackpackBaubleSlotEmpty(EntityPlayer player) {
      return getBackpackBaubleSlotItemStack(player).isEmpty();
   }

   public static ItemStack getBackpackBaubleSlotItemStack(EntityPlayer player) {
      return BaublesApi.getBaublesHandler(player).getStackInSlot(getBackpackBaubleSlotIndex());
   }

   public static void setBackpackBaubleSlotItemStack(EntityPlayer player, ItemStack stack) {
      ((ItemStackHandler)BaublesApi.getBaublesHandler(player)).setStackInSlot(getBackpackBaubleSlotIndex(), stack);
   }

   public static int getBackpackBaubleSlotIndex() {
      return ItemBackpack.getBaubleType().getValidSlots()[0];
   }

   public static void setEquippedBackpack(EntityLivingBase entity, ItemStack stack, IBackpackData backpackData) {
      IBackpackType backpackType = getBackpackType(stack);
      if (!stack.isEmpty() && backpackType == null) {
         throw new IllegalArgumentException("Backpack item isn't an IBackpackType.");
      } else {
         IBackpack backpack = (IBackpack)entity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         backpack.setStack(stack);
         backpack.setData(backpackData);
      }
   }

   public static boolean canInteractWithEquippedBackpack(EntityPlayer player, EntityLivingBase carrier) {
      IBackpack backpack = getBackpack(carrier);
      if (backpack != null && player.isEntityAlive() && carrier.isEntityAlive()) {
         if (player == carrier) {
            return true;
         } else {
            double distance = (double)player.getDistance(carrier);
            double angle = Math.toDegrees(Math.atan2(carrier.posZ - player.posZ, carrier.posX - player.posX));
            angle = ((angle - (double)carrier.renderYawOffset - 90.0D) % 360.0D + 540.0D) % 360.0D - 180.0D;
            return distance <= INTERACT_MAX_DISTANCE && Math.abs(angle) < INTERACT_MAX_ANGLE / 2.0D;
         }
      } else {
         return false;
      }
   }

   public static boolean equipBackpack(EntityLivingBase entity, TileEntity tileEntity) {
      if (tileEntity != null && canEquipBackpack(entity)) {
         IBackpack backpack = (IBackpack)tileEntity.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);
         if (backpack == null) {
            return false;
         } else {
            ItemStack stack = backpack.getStack();
            IBackpackType type = getBackpackType(stack);
            if (type == null) {
               return false;
            } else {
               type.onEquip(entity, tileEntity, backpack);
               if (!entity.world.isRemote) {
                  setEquippedBackpack(entity, stack, backpack.getData());
                  backpack.setStack(ItemStack.EMPTY);
                  backpack.setData((IBackpackData)null);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean placeBackpack(World world, BlockPos pos, ItemStack stack, EntityLivingBase entity, boolean ignoreEntities) {
      EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      if (player != null && !player.canPlayerEdit(pos, EnumFacing.UP, stack)) {
         return false;
      } else {
         Item item = stack.getItem();
         Block block = (Block)Block.REGISTRY.getObject(item.getRegistryName());
         if (ignoreEntities) {
            if (!block.canPlaceBlockAt(world, pos)) {
               return false;
            }
         } else if (!world.mayPlace(block, pos, false, EnumFacing.UP, (Entity)null)) {
            return false;
         }

         IBlockState state = block.getStateForPlacement(world, pos, EnumFacing.UP, 0.5F, 0.5F, 0.5F, item.getMetadata(stack.getMetadata()), player, EnumHand.MAIN_HAND);
         if (world.setBlockState(pos, state, 3) && world.getBlockState(pos).getBlock() == block) {
            block.onBlockPlacedBy(world, pos, state, entity, stack);
            double x = (double)pos.getX() + 0.5D;
            double y = (double)pos.getY() + 0.5D;
            double z = (double)pos.getZ() + 0.5D;
            SoundType sound = block.getSoundType(state, world, pos, entity);
            world.playSound(x, y, z, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F, false);
            boolean alreadyConfigured = ItemBlock.setTileEntityNBT(world, player, pos, stack);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) {
               return true;
            } else {
               IBackpack placedBackpack = getBackpack(tileEntity);
               if (placedBackpack == null) {
                  return true;
               } else {
                  IBackpack carrierBackpack = getBackpack(entity);
                  boolean isEquipped = carrierBackpack != null && carrierBackpack.getStack() == stack;
                  ItemStack stackOrig = stack;
                  stack = stack.copy();
                  stack.setCount(1);
                  if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
                     stack.getTagCompound().removeTag("BlockEntityTag");
                  }

                  placedBackpack.setStack(stack);
                  if (!alreadyConfigured) {
                     if (isEquipped) {
                        IBackpackType type = carrierBackpack.getType();
                        IBackpackData data = carrierBackpack.getData();
                        if (data == null && !world.isRemote) {
                           LOG.error("Backpack data was null when placing down equipped backpack");
                           data = type.createBackpackData(stack);
                        }

                        placedBackpack.setData(data);
                        if (!world.isRemote) {
                           setEquippedBackpack(entity, ItemStack.EMPTY, (IBackpackData)null);
                        }

                        type.onUnequip(entity, tileEntity, placedBackpack);
                     } else if (!world.isRemote) {
                        placedBackpack.setData(placedBackpack.getType().createBackpackData(stack));
                     }
                  }

                  stackOrig.shrink(1);
                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public static void updateLidTicks(IBackpack properties, double x, double y, double z) {
      World world = Minecraft.getMinecraft().world;
      boolean usedByPlayer = properties.getPlayersUsing() > 0;
      int prevLidTicks = properties.getLidTicks();
      IBackpackType backpackType = getBackpackType(properties.getStack());
      int maxLidTicks = backpackType != null ? backpackType.getLidMaxTicks() : 0;
      int lidTicks = Math.max(-1, Math.min(maxLidTicks, prevLidTicks + (usedByPlayer ? 1 : -1)));
      properties.setLidTicks(lidTicks);
      if (lidTicks > 0 && prevLidTicks <= 0) {
         world.playSound(x, y, z, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.PLAYERS, 1.0F, 0.6F, false);
      }

      if (lidTicks < maxLidTicks / 5 && prevLidTicks >= maxLidTicks / 5) {
         world.playSound(x, y, z, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.PLAYERS, 0.8F, 0.4F, false);
      }

   }
}
