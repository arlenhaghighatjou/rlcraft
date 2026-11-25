package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.ModConfig;
import de.mennomax.astikorcarts.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityPlowCart extends AbstractDrawnInventory implements IInventoryChangedListener {
   private static final DataParameter<Boolean> PLOWING = EntityDataManager.createKey(EntityPlowCart.class, DataSerializers.BOOLEAN);
   private static final double BLADEOFFSET = 1.7D;
   private static final DataParameter[] TOOLS = new DataParameter[]{EntityDataManager.createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK), EntityDataManager.createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK), EntityDataManager.createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK)};

   public EntityPlowCart(World worldIn) {
      super(worldIn);
      this.setSize(1.5F, 1.4F);
      this.spacing = 2.4D;
      this.initInventory(this.getName(), true, 3);
      this.inventory.addInventoryChangeListener(this);
   }

   public boolean canBePulledBy(Entity pullingIn) {
      if (this.isPassenger(pullingIn)) {
         return false;
      } else {
         for(String entry : ModConfig.plowCart.canPull) {
            if (entry.equals(pullingIn instanceof EntityPlayer ? "minecraft:player" : EntityList.getKey(pullingIn).toString())) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean getPlowing() {
      return ((Boolean)this.dataManager.get(PLOWING)).booleanValue();
   }

   public void onUpdate() {
      super.onUpdate();
      EntityPlayer player = this.pulling != null && this.pulling.getControllingPassenger() instanceof EntityPlayer ? (EntityPlayer)this.pulling.getControllingPassenger() : (this.pulling instanceof EntityPlayer ? (EntityPlayer)this.pulling : null);
      if (!this.world.isRemote && ((Boolean)this.dataManager.get(PLOWING)).booleanValue() && player != null && (this.prevPosX != this.posX || this.prevPosZ != this.posZ)) {
         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            if (this.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
               float offset = 38.0F + (float)i * -38.0F;
               double blockPosX = this.posX + (double)MathHelper.sin((this.rotationYaw - offset) * 0.017453292F) * 1.7D;
               double blockPosZ = this.posZ - (double)MathHelper.cos((this.rotationYaw - offset) * 0.017453292F) * 1.7D;
               BlockPos blockPos = new BlockPos(blockPosX, this.posY - 0.5D, blockPosZ);
               BlockPos upPos = blockPos.up();
               Material upMaterial = this.world.getBlockState(upPos).getMaterial();
               if (upMaterial == Material.AIR) {
                  this.handleTool(blockPos, i, player);
               } else if (upMaterial == Material.PLANTS || upMaterial == Material.VINE) {
                  this.world.destroyBlock(upPos, false);
                  this.handleTool(blockPos, i, player);
               }
            }
         }
      }

   }

   public Item getCartItem() {
      return ModItems.PLOWCART;
   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      if (!this.world.isRemote) {
         if (player.isSneaking()) {
            player.openGui(AstikorCarts.instance, 1, this.world, this.getEntityId(), 0, 0);
         } else {
            this.dataManager.set(PLOWING, Boolean.valueOf(!((Boolean)this.dataManager.get(PLOWING)).booleanValue()));
         }
      }

      return true;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(PLOWING, Boolean.valueOf(compound.getBoolean("Plowing")));

      for(int i = 0; i < TOOLS.length; ++i) {
         this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Plowing", ((Boolean)this.dataManager.get(PLOWING)).booleanValue());
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(PLOWING, Boolean.valueOf(false));

      for(int i = 0; i < TOOLS.length; ++i) {
         this.dataManager.register(TOOLS[i], ItemStack.EMPTY);
      }

   }

   private void handleTool(BlockPos pos, int slot, EntityPlayer player) {
      IBlockState state = this.world.getBlockState(pos);
      Block block = state.getBlock();
      ItemStack itemstack = this.inventory.getStackInSlot(slot);
      if (itemstack.getItem() instanceof ItemHoe) {
         if (block != Blocks.GRASS && block != Blocks.GRASS_PATH) {
            if (block == Blocks.DIRT) {
               switch((DirtType)state.getValue(BlockDirt.VARIANT)) {
               case DIRT:
                  this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
                  this.damageAndUpdateOnBreak(slot, itemstack, player);
                  break;
               case COARSE_DIRT:
                  this.world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT), 11);
                  this.damageAndUpdateOnBreak(slot, itemstack, player);
               }
            }
         } else {
            this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
            this.damageAndUpdateOnBreak(slot, itemstack, player);
         }
      } else if (itemstack.getItem() instanceof ItemSpade && block == Blocks.GRASS) {
         this.world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
         this.damageAndUpdateOnBreak(slot, itemstack, player);
      }

   }

   private void damageAndUpdateOnBreak(int slot, ItemStack itemstack, EntityPlayer player) {
      itemstack.damageItem(1, player);
      if (itemstack.isEmpty()) {
         this.dataManager.set(TOOLS[slot], ItemStack.EMPTY);
      }

   }

   public void onInventoryChanged(IInventory invBasic) {
      for(int i = 0; i < TOOLS.length; ++i) {
         if (this.dataManager.get(TOOLS[i]) != invBasic.getStackInSlot(i)) {
            this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
         }
      }

   }

   public ItemStack getTool(int i) {
      return (ItemStack)this.dataManager.get(TOOLS[i]);
   }
}
