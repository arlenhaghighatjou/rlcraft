package knightminer.inspirations.recipes.tileentity;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tank.CauldronTank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileCauldron extends TileEntity {
   public static final DamageSource DAMAGE_BOIL = (new DamageSource(Util.prefix("boiling"))).setDamageBypassesArmor();
   private ICauldronRecipe.CauldronState state = ICauldronRecipe.CauldronState.WATER;
   private CauldronTank tank = new CauldronTank(this);
   private static final String TAG_CAULDRON_CRAFTED = "cauldron_crafted";
   private static final String TAG_CAULDRON_COOLDOWN = "cauldron_cooldown";
   public static final String TAG_STATE = "state";

   public TileCauldron() {
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   @Nonnull
   public BlockEnhancedCauldron.CauldronContents getContentType() {
      if (this.state.getFluid() != null) {
         return BlockEnhancedCauldron.CauldronContents.FLUID;
      } else if (this.state.getColor() > -1) {
         return BlockEnhancedCauldron.CauldronContents.DYE;
      } else {
         return this.state.getPotion() != null ? BlockEnhancedCauldron.CauldronContents.POTION : BlockEnhancedCauldron.CauldronContents.FLUID;
      }
   }

   public boolean isWater() {
      return this.state.isWater();
   }

   public ICauldronRecipe.CauldronState getState() {
      return this.state;
   }

   public int getColor() {
      switch(this.getContentType()) {
      case DYE:
         return this.state.getColor();
      case POTION:
         return PotionUtils.getPotionColor(this.state.getPotion());
      default:
         Fluid fluid = this.state.getFluid();
         return fluid != null ? this.state.getFluid().getColor() : -1;
      }
   }

   public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
      if (BlockEnhancedCauldron.getCauldronLevel(state) > 0 && this.state != ICauldronRecipe.CauldronState.WATER && this.getContentType() == BlockEnhancedCauldron.CauldronContents.FLUID) {
         Fluid fluid = this.state.getFluid();
         if (fluid != null) {
            state = state.withProperty(BlockEnhancedCauldron.TEXTURE, fluid.getStill().toString());
         }
      }

      return state;
   }

   public static boolean interact(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (stack.isEmpty()) {
         return false;
      } else {
         TileCauldron cauldron = null;
         ICauldronRecipe.CauldronState state = ICauldronRecipe.CauldronState.WATER;
         boolean boiling = false;
         Block block = blockState.getBlock();
         if (Config.enableExtendedCauldron && block instanceof BlockEnhancedCauldron) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileCauldron) {
               cauldron = (TileCauldron)te;
               state = cauldron.state;
               boiling = ((Boolean)blockState.getValue(BlockEnhancedCauldron.BOILING)).booleanValue();
            }
         } else {
            state = InspirationsRegistry.getCauldronState(blockState);
            boiling = InspirationsRegistry.isCauldronFire(world.getBlockState(pos.down()));
         }

         int level = BlockEnhancedCauldron.getCauldronLevel(blockState);
         ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, state);
         if (recipe != null) {
            if (!world.isRemote) {
               ICauldronRecipe.CauldronState newState = recipe.getState(stack, boiling, level, state);
               if (!Config.enableExtendedCauldron && !ICauldronRecipe.CauldronState.WATER.matches(newState)) {
                  return true;
               }

               SoundEvent sound = recipe.getSound(stack, boiling, level, state);
               if (sound != null) {
                  world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
               }

               int newLevel = recipe.getLevel(level);
               if (newLevel != level || !state.matches(newState)) {
                  if (newLevel == InspirationsRegistry.getCauldronMax() && InspirationsRegistry.isNormalCauldron(blockState) && InspirationsRegistry.hasFullCauldron(newState)) {
                     world.setBlockState(pos, InspirationsRegistry.getFullCauldron(newState));
                     cauldron = null;
                  } else {
                     if (!(block instanceof BlockCauldron)) {
                        Blocks.CAULDRON.setWaterLevel(world, pos, Blocks.CAULDRON.getDefaultState(), newLevel);
                        if (Config.enableExtendedCauldron) {
                           TileEntity te = world.getTileEntity(pos);
                           if (te instanceof TileCauldron) {
                              cauldron = (TileCauldron)te;
                           }
                        }
                     } else {
                        ((BlockCauldron)block).setWaterLevel(world, pos, blockState, newLevel);
                     }

                     if (newLevel == 0) {
                        newState = ICauldronRecipe.CauldronState.WATER;
                     }
                  }
               }

               if (cauldron != null) {
                  cauldron.setState(newState, true);
               }

               ItemStack result = recipe.getResult(stack, boiling, level, state);
               if (!player.capabilities.isCreativeMode) {
                  ItemStack container = recipe.getContainer(stack);
                  int original = stack.getCount();
                  ItemStack transform = recipe.transformInput(stack, boiling, level, state);
                  if (transform.isEmpty()) {
                     if (!container.isEmpty()) {
                        container.setCount(container.getCount() * original);
                        player.setHeldItem(hand, container);
                     }
                  } else {
                     player.setHeldItem(hand, transform);
                     if (!container.isEmpty()) {
                        container.setCount(container.getCount() * (original - transform.getCount()));
                        ItemHandlerHelper.giveItemToPlayer(player, container, player.inventory.currentItem);
                     }
                  }
               }

               if (!result.isEmpty()) {
                  ItemHandlerHelper.giveItemToPlayer(player, result, player.inventory.currentItem);
               }
            }

            return true;
         } else if (!state.isWater()) {
            return true;
         } else {
            Item item = stack.getItem();
            return state != ICauldronRecipe.CauldronState.WATER && (item == Items.POTIONITEM || item == Items.WATER_BUCKET);
         }
      }
   }

   public int onEntityCollide(Entity entity, int level, IBlockState currentState) {
      if (entity instanceof EntityItem) {
         EntityItem entityItem = (EntityItem)entity;
         NBTTagCompound entityTags = entity.getEntityData();
         if (entityTags.getBoolean("cauldron_crafted")) {
            return level;
         }

         int cooldown = entityTags.getInteger("cauldron_cooldown");
         if (cooldown > 0) {
            entityTags.setInteger("cauldron_cooldown", cooldown - 1);
            return level;
         }

         boolean boiling = ((Boolean)currentState.getValue(BlockEnhancedCauldron.BOILING)).booleanValue();
         ItemStack stack = entityItem.getItem();
         ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, this.state);
         if (recipe != null) {
            ICauldronRecipe.CauldronState state = this.state;
            SoundEvent sound = recipe.getSound(stack, boiling, level, state);
            if (sound != null) {
               this.world.playSound((EntityPlayer)null, this.pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
            }

            int matches = 0;

            ICauldronRecipe.CauldronState newState;
            while(true) {
               newState = recipe.getState(stack, boiling, level, state);
               level = recipe.getLevel(level);
               if (level == 0) {
                  newState = ICauldronRecipe.CauldronState.WATER;
               }

               ItemStack result = recipe.getResult(stack, boiling, level, state);
               if (!result.isEmpty()) {
                  this.spawnItem(result, entityItem);
               }

               ItemStack container = recipe.getContainer(stack);
               int oldCount = stack.getCount();
               stack = recipe.transformInput(stack, boiling, level, state);
               if (!container.isEmpty()) {
                  container.setCount(container.getCount() * (oldCount - stack.getCount()));
                  this.spawnItem(container, entityItem);
               }

               state = newState;
               ++matches;
               if (!recipe.matches(stack, boiling, level, newState) || matches >= 10) {
                  break;
               }
            }

            if (matches == 10) {
               Inspirations.log.warn("Recipe '{}' matched too many times in a single tick. Either the level or the state should change to make it no longer match.", recipe);
            }

            if (stack.isEmpty()) {
               entityItem.setDead();
            } else {
               entityItem.setItem(stack);
               entityTags.setBoolean("cauldron_crafted", true);
            }

            if (!newState.matches(this.state)) {
               this.state = newState;
               this.world.notifyBlockUpdate(this.pos, currentState, currentState, 2);
            }
         } else {
            entityTags.setInteger("cauldron_cooldown", 60);
         }
      } else if (level > 0) {
         switch(this.getContentType()) {
         case POTION:
            if (entity instanceof EntityLivingBase) {
               EntityLivingBase living = (EntityLivingBase)entity;
               List<PotionEffect> effects = this.state.getPotion().getEffects();
               if (effects.stream().anyMatch((effect) -> {
                  return !living.isPotionActive(effect.getPotion());
               })) {
                  for(PotionEffect effect : effects) {
                     if (effect.getPotion().isInstant()) {
                        effect.getPotion().affectEntity(living, living, living, effect.getAmplifier(), 1.0D);
                     } else {
                        living.addPotionEffect(new PotionEffect(effect));
                     }
                  }

                  --level;
               }
            }
            break;
         case FLUID:
            if (this.isWater()) {
               if (entity.isBurning()) {
                  entity.extinguish();
                  --level;
               }
            } else {
               Fluid fluid = this.state.getFluid();
               if (fluid.getTemperature() > 450 && !entity.isImmuneToFire()) {
                  entity.attackEntityFrom(DamageSource.LAVA, 4.0F);
                  entity.setFire(15);
                  break;
               }
            }
         case DYE:
            if (((Boolean)currentState.getValue(BlockEnhancedCauldron.BOILING)).booleanValue()) {
               entity.attackEntityFrom(DAMAGE_BOIL, 2.0F);
            }
         }
      }

      return level;
   }

   private void spawnItem(ItemStack stack, EntityItem base) {
      EntityItem entityItem = new EntityItem(this.world, base.posX, base.posY, base.posZ, stack);
      entityItem.getEntityData().setBoolean("cauldron_crafted", true);
      this.world.spawnEntity(entityItem);
   }

   public void onBreak(BlockPos pos, int level) {
      switch(this.getContentType()) {
      case POTION:
         PotionType potion = this.state.getPotion();
         EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(this.world, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F));
         cloud.setRadius(0.5F * (float)level + 0.5F);
         cloud.setDuration(20 * (level + 1));
         cloud.setRadiusOnUse(-0.5F);
         cloud.setWaitTime(10);
         cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
         cloud.setPotion(potion);

         for(PotionEffect effect : potion.getEffects()) {
            cloud.addEffect(new PotionEffect(effect));
         }

         this.world.spawnEntity(cloud);
         break;
      case FLUID:
         Block block = this.state.getFluid().getBlock();
         if (block != null) {
            if (block == Blocks.WATER) {
               block = Blocks.FLOWING_WATER;
            } else if (block == Blocks.LAVA) {
               block = Blocks.FLOWING_LAVA;
            }

            if (level == (Config.enableBiggerCauldron ? 4 : 3)) {
               this.world.setBlockState(pos, block.getDefaultState());
            }
         }
      }

   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   @Nonnull
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.tank) : super.getCapability(capability, facing));
   }

   public int getFluidLevel() {
      IBlockState state = this.world.getBlockState(this.pos);
      Block block = state.getBlock();
      return block instanceof BlockEnhancedCauldron ? ((BlockEnhancedCauldron)block).getLevel(state) : 0;
   }

   public void setFluidLevel(int levels) {
      IBlockState state = this.world.getBlockState(this.pos);
      Block block = state.getBlock();
      if (block instanceof BlockEnhancedCauldron) {
         if (levels == 0) {
            this.state = ICauldronRecipe.CauldronState.WATER;
         }

         ((BlockEnhancedCauldron)block).setWaterLevel(this.world, this.pos, state, levels);
      }

   }

   public void setState(ICauldronRecipe.CauldronState newState, boolean doBlockUpdate) {
      if (!this.state.matches(newState)) {
         this.state = newState;
         if (doBlockUpdate) {
            IBlockState blockstate = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, blockstate, blockstate, 2);
         } else {
            this.markDirty();
         }
      }

   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      tags = super.writeToNBT(tags);
      tags.setTag("state", this.state.writeToNBT());
      return tags;
   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      this.state = ICauldronRecipe.CauldronState.fromNBT(tags.getCompoundTag("state"));
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), this.state.writeToNBT());
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      ICauldronRecipe.CauldronState newState = ICauldronRecipe.CauldronState.fromNBT(pkt.getNbtCompound());
      if (!this.state.matches(newState)) {
         this.state = newState;
         if (this.world.isRemote) {
            Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate((World)null, this.pos, (IBlockState)null, (IBlockState)null, 0);
         }
      }

   }
}
