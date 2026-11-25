package tschipp.carryon.common.item;

import com.google.common.base.CharMatcher;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tschipp.carryon.CarryOn;
import tschipp.carryon.client.keybinds.CarryOnKeybinds;
import tschipp.carryon.common.config.CarryOnConfig;
import tschipp.carryon.common.event.ItemEvents;
import tschipp.carryon.common.handler.CustomPickupOverrideHandler;
import tschipp.carryon.common.handler.ModelOverridesHandler;

public class ItemTile extends Item {
   public static final String TILE_DATA_KEY = "tileData";
   public static final String[] FACING_KEYS = new String[]{"rotation", "rot", "facing", "face", "direction", "dir", "front", "forward"};

   public ItemTile() {
      this.setUnlocalizedName("tile_item");
      this.setRegistryName("carryon", "tile_item");
      ForgeRegistries.ITEMS.register(this);
      this.setMaxStackSize(1);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      if (hasTileData(stack)) {
         IBlockState state = getBlockState(stack);
         NBTTagCompound nbt = getTileData(stack);
         if (ModelOverridesHandler.hasCustomOverrideModel(state, nbt)) {
            Object override = ModelOverridesHandler.getOverrideObject(state, nbt);
            if (override instanceof ItemStack) {
               return ((ItemStack)override).getDisplayName();
            } else {
               IBlockState ostate = (IBlockState)override;
               ItemStack itemstack = new ItemStack(ostate.getBlock().getItemDropped(ostate, itemRand, 0), 1, state.getBlock().damageDropped(ostate));
               return itemstack.getDisplayName();
            }
         } else {
            return getItemStack(stack).getDisplayName();
         }
      } else {
         return "";
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (Loader.isModLoaded("betterplacement") && CarryOnKeybinds.isKeyPressed(player)) {
         return EnumActionResult.FAIL;
      } else {
         Block block = world.getBlockState(pos).getBlock();
         ItemStack stack = player.getHeldItem(hand);
         if (hasTileData(stack)) {
            try {
               Vec3d vec = player.getLookVec();
               EnumFacing facing2 = EnumFacing.getFacingFromVector((float)vec.x, 0.0F, (float)vec.z);
               BlockPos pos2 = pos;
               Block containedblock = getBlock(stack);
               int meta = getMeta(stack);
               IBlockState containedstate = getBlockState(stack);
               if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
                  pos2 = pos.offset(facing);
               }

               if (world.getBlockState(pos2).getBlock().isReplaceable(world, pos2) && containedblock != null) {
                  boolean canPlace = containedblock.canPlaceBlockAt(world, pos2);
                  if (canPlace && player.canPlayerEdit(pos, facing, stack) && world.mayPlace(containedblock, pos2, false, facing, (Entity)null)) {
                     boolean set = false;

                     for(IProperty prop : containedblock.getDefaultState().getPropertyKeys()) {
                        Object[] allowedValues = prop.getAllowedValues().toArray();
                        if (prop instanceof PropertyDirection && this.equal(allowedValues, EnumFacing.HORIZONTALS)) {
                           BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                           PlaceEvent event = new PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                           MinecraftForge.EVENT_BUS.post(event);
                           if (!event.isCanceled()) {
                              world.setBlockState(pos2, containedstate.withProperty(prop, containedblock instanceof BlockStairs ? facing2 : facing2.getOpposite()));
                              set = true;
                           }
                        } else if (prop instanceof PropertyDirection && this.equal(allowedValues, EnumFacing.VALUES)) {
                           facing2 = EnumFacing.getFacingFromVector((float)vec.x, (float)vec.y, (float)vec.z);
                           BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                           PlaceEvent event = new PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                           MinecraftForge.EVENT_BUS.post(event);
                           if (!event.isCanceled()) {
                              world.setBlockState(pos2, containedstate.withProperty(prop, facing2.getOpposite()));
                              set = true;
                           }
                        }
                     }

                     if (!set && !getTileData(stack).hasNoTags()) {
                        NBTTagCompound tag = getTileData(stack);

                        label122:
                        for(String key : tag.getKeySet()) {
                           for(String facingKey : FACING_KEYS) {
                              if (key.toLowerCase().equals(facingKey)) {
                                 String type = NBTTagCompound.getTagTypeName(tag.getTagId(key));
                                 byte var30 = -1;
                                 switch(type.hashCode()) {
                                 case -829733494:
                                    if (type.equals("TAG_Int")) {
                                       var30 = 1;
                                    }
                                    break;
                                 case -761853802:
                                    if (type.equals("TAG_String")) {
                                       var30 = 0;
                                    }
                                    break;
                                 case 47867597:
                                    if (type.equals("TAG_Byte")) {
                                       var30 = 2;
                                    }
                                 }

                                 switch(var30) {
                                 case 0:
                                    tag.setString(key, CharMatcher.JAVA_UPPER_CASE.matchesAllOf(tag.getString(key)) ? facing2.getOpposite().getName().toUpperCase() : facing2.getOpposite().getName());
                                    break label122;
                                 case 1:
                                    tag.setInteger(key, facing2.getOpposite().getIndex());
                                    break label122;
                                 case 2:
                                    tag.setByte(key, (byte)facing2.getOpposite().getIndex());
                                 default:
                                    break label122;
                                 }
                              }
                           }
                        }
                     }

                     if (!set) {
                        BlockSnapshot snapshot = new BlockSnapshot(world, pos2, containedstate);
                        PlaceEvent event = new PlaceEvent(snapshot, world.getBlockState(pos), player, hand);
                        MinecraftForge.EVENT_BUS.post(event);
                        if (!event.isCanceled()) {
                           world.setBlockState(pos2, containedstate);
                           set = true;
                        }
                     }

                     if (set) {
                        TileEntity tile = world.getTileEntity(pos2);
                        if (tile != null) {
                           NBTTagCompound tag = getTileData(stack);
                           updateTileLocation(tag, pos2);
                           tile.readFromNBT(tag);
                        }

                        clearTileData(stack);
                        player.playSound(containedblock.getSoundType().getPlaceSound(), 1.0F, 0.5F);
                        player.setHeldItem(hand, ItemStack.EMPTY);
                        player.getEntityData().removeTag("overrideKey");
                        ItemEvents.sendPacket(player, 9, 0);
                        return EnumActionResult.SUCCESS;
                     }
                  }
               }
            } catch (Exception var31) {
               var31.printStackTrace();
               if (world != null && world.isRemote) {
                  CarryOn.LOGGER.info("Block: " + getBlock(stack));
                  CarryOn.LOGGER.info("BlockState: " + getBlockState(stack));
                  CarryOn.LOGGER.info("Meta: " + getMeta(stack));
                  CarryOn.LOGGER.info("ItemStack: " + getItemStack(stack));
                  if (ModelOverridesHandler.hasCustomOverrideModel(getBlockState(stack), getTileData(stack))) {
                     CarryOn.LOGGER.info("Override Model: " + ModelOverridesHandler.getOverrideObject(getBlockState(stack), getTileData(stack)));
                  }

                  if (CustomPickupOverrideHandler.hasSpecialPickupConditions(getBlockState(stack))) {
                     CarryOn.LOGGER.info("Custom Pickup Condition: " + CustomPickupOverrideHandler.getPickupCondition(getBlockState(stack)));
                  }

                  player.sendMessage(new TextComponentString(TextFormatting.RED + "Error detected. Cannot place block. Execute \"/carryon clear\" to remove the item"));
                  TextComponentString s = new TextComponentString(TextFormatting.GOLD + "here");
                  s.getStyle().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/Tschipp/CarryOn/issues"));
                  player.sendMessage((new TextComponentString(TextFormatting.RED + "Please report this error ")).appendSibling(s));
               }
            }
         }

         return EnumActionResult.FAIL;
      }
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (hasTileData(stack)) {
         if (entity instanceof EntityLivingBase) {
            if ((!(entity instanceof EntityPlayer) || !CarryOnConfig.settings.slownessInCreative) && ((EntityPlayer)entity).isCreative()) {
               return;
            }

            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, this.potionLevel(stack), false, false));
         }
      } else {
         stack = ItemStack.EMPTY;
      }

   }

   public static boolean hasTileData(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         return false;
      } else {
         NBTTagCompound tag = stack.getTagCompound();
         return tag.hasKey("tileData") && tag.hasKey("block") && tag.hasKey("meta") && tag.hasKey("stateid");
      }
   }

   public static boolean storeTileData(@Nullable TileEntity tile, World world, BlockPos pos, IBlockState state, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         NBTTagCompound chest = new NBTTagCompound();
         if (tile != null) {
            chest = tile.writeToNBT(chest);
         }

         NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
         if (tag.hasKey("tileData")) {
            return false;
         } else {
            tag.setTag("tileData", chest);
            ItemStack drop = new ItemStack(state.getBlock().getItemDropped(state, itemRand, 0), 1, state.getBlock().damageDropped(state));
            tag.setString("block", state.getBlock().getRegistryName().toString());
            Item item = Item.getItemFromBlock(state.getBlock());
            tag.setInteger("meta", drop.getItemDamage());
            tag.setInteger("stateid", Block.getStateId(state));
            stack.setTagCompound(tag);
            return true;
         }
      }
   }

   public static void clearTileData(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         tag.removeTag("tileData");
         tag.removeTag("block");
         tag.removeTag("meta");
         tag.removeTag("stateid");
      }

   }

   public static void updateTileLocation(NBTTagCompound tag, BlockPos pos) {
      tag.setInteger("x", pos.getX());
      tag.setInteger("y", pos.getY());
      tag.setInteger("z", pos.getZ());
   }

   public static NBTTagCompound getTileData(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         return tag.getCompoundTag("tileData");
      } else {
         return null;
      }
   }

   public static Block getBlock(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         int id = tag.getInteger("stateid");
         return Block.getStateById(id).getBlock();
      } else {
         return Blocks.AIR;
      }
   }

   public static int getMeta(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         int meta = tag.getInteger("meta");
         return meta;
      } else {
         return 0;
      }
   }

   public static ItemStack getItemStack(ItemStack stack) {
      return new ItemStack(getBlock(stack), 1, getMeta(stack));
   }

   public static IBlockState getBlockState(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         int id = tag.getInteger("stateid");
         return Block.getStateById(id);
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   public static boolean isLocked(BlockPos pos, World world) {
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
         NBTTagCompound tag = new NBTTagCompound();
         te.writeToNBT(tag);
         return tag.hasKey("Lock") ? !tag.getString("Lock").equals("") : false;
      } else {
         return false;
      }
   }

   private boolean equal(Object[] a, Object[] b) {
      if (a.length != b.length) {
         return false;
      } else {
         List lA = Arrays.asList(a);
         List lB = Arrays.asList(b);
         return lA.containsAll(lB);
      }
   }

   private int potionLevel(ItemStack stack) {
      String nbt = getTileData(stack).toString();
      int i = nbt.length() / 500;
      if (i > 4) {
         i = 4;
      }

      if (!CarryOnConfig.settings.heavyTiles) {
         i = 1;
      }

      return (int)((double)i * CarryOnConfig.settings.blockSlownessMultiplier);
   }
}
