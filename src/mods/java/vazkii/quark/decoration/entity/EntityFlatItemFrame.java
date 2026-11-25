package vazkii.quark.decoration.entity;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Predicate;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.Validate;

public class EntityFlatItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {
   protected static final Predicate<Entity> IS_HANGING_ENTITY = (entity) -> {
      return entity instanceof EntityHanging;
   };
   private static final String TAG_ITEM_DROP_CHANCE = "ItemDropChance";
   private static final String TAG_REAL_FACING_DIRECTION = "RealFacing";
   public EnumFacing realFacingDirection;
   private float itemDropChance = 1.0F;

   public EntityFlatItemFrame(World worldIn) {
      super(worldIn);
   }

   public EntityFlatItemFrame(World worldIn, BlockPos blockPos, EnumFacing face) {
      super(worldIn, blockPos, face);
   }

   public void dropItemOrSelf(Entity entityIn, boolean creative) {
      if (!creative) {
         super.dropItemOrSelf(entityIn, false);
      } else {
         if (this.getEntityWorld().getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = this.getDisplayedItem();
            if (entityIn instanceof EntityPlayer) {
               EntityPlayer entityplayer = (EntityPlayer)entityIn;
               if (entityplayer.capabilities.isCreativeMode) {
                  this.removeFrameFromMap(itemstack);
                  return;
               }
            }

            this.dropFrame();
            if (!itemstack.isEmpty() && this.rand.nextFloat() < this.itemDropChance) {
               itemstack = itemstack.copy();
               this.removeFrameFromMap(itemstack);
               this.entityDropItem(itemstack, 0.0F);
            }
         }

      }
   }

   protected void dropFrame() {
      this.entityDropItem(new ItemStack(Items.ITEM_FRAME, 1), 0.0F);
   }

   public EntityItem entityDropItem(ItemStack stack, float offsetY) {
      EntityItem entityitem = new EntityItem(this.world, this.posX + (double)((float)this.realFacingDirection.getFrontOffsetX() * 0.25F), this.posY + (double)offsetY + (double)((float)this.realFacingDirection.getFrontOffsetY() * 0.25F), this.posZ + (double)((float)this.realFacingDirection.getFrontOffsetZ() * 0.25F), stack);
      entityitem.setDefaultPickupDelay();
      if (this.realFacingDirection == EnumFacing.DOWN) {
         entityitem.motionY = -Math.abs(entityitem.motionY);
      }

      this.world.spawnEntity(entityitem);
      return entityitem;
   }

   public boolean onValidSurface() {
      if (this.realFacingDirection.getAxis() == Axis.Y) {
         if (!this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
            return false;
         } else {
            BlockPos blockpos = this.hangingPosition.offset(this.realFacingDirection.getOpposite());
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            if (!iblockstate.isSideSolid(this.world, blockpos, this.realFacingDirection) && !iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate)) {
               return false;
            } else {
               World var10000 = this.world;
               AxisAlignedBB var10002 = this.getEntityBoundingBox();
               Predicate var10003 = IS_HANGING_ENTITY;
               IS_HANGING_ENTITY.getClass();
               return var10000.getEntitiesInAABBexcluding(this, var10002, var10003::test).isEmpty();
            }
         }
      } else {
         return super.onValidSurface();
      }
   }

   protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      this.realFacingDirection = facingDirectionIn;
      this.facingDirection = this.realFacingDirection.getAxis() == Axis.Y ? EnumFacing.SOUTH : this.realFacingDirection;
      this.rotationYaw = this.realFacingDirection.getAxis() == Axis.Y ? 0.0F : (float)(this.realFacingDirection.getHorizontalIndex() * 90);
      this.rotationPitch = this.realFacingDirection.getAxis() == Axis.Y ? (this.realFacingDirection == EnumFacing.UP ? -90.0F : 90.0F) : 0.0F;
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   protected void updateBoundingBox() {
      if (this.realFacingDirection != null) {
         if (this.realFacingDirection.getAxis() == Axis.Y) {
            double d0 = (double)this.hangingPosition.getX() + 0.5D;
            double d1 = (double)this.hangingPosition.getY() + 0.5D;
            double d2 = (double)this.hangingPosition.getZ() + 0.5D;
            d1 = d1 - (double)this.realFacingDirection.getFrontOffsetY() * 0.46875D;
            double d6 = (double)this.getHeightPixels();
            double d7 = (double)(-this.realFacingDirection.getFrontOffsetY());
            double d8 = (double)this.getHeightPixels();
            d6 = d6 / 32.0D;
            d7 = d7 / 32.0D;
            d8 = d8 / 32.0D;
            this.posX = d0;
            this.posY = d1 - d7;
            this.posZ = d2;
            this.height = 0.0625F;
            this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
         } else {
            super.updateBoundingBox();
         }

      }
   }

   private void removeFrameFromMap(ItemStack stack) {
      if (!stack.isEmpty()) {
         if (stack.getItem() instanceof ItemMap) {
            MapData mapdata = ((ItemMap)stack.getItem()).getMapData(stack, this.getEntityWorld());
            if (mapdata != null) {
               mapdata.mapDecorations.remove("frame-" + this.getEntityId());
            }
         }

         stack.setItemFrame((EntityItemFrame)null);
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setByte("RealFacing", (byte)this.realFacingDirection.getIndex());
      super.writeEntityToNBT(compound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("ItemDropChance", 99)) {
         this.itemDropChance = compound.getFloat("ItemDropChance");
      }

      super.readEntityFromNBT(compound);
      this.updateFacingWithBoundingBox(EnumFacing.getFront(compound.getByte("RealFacing")));
   }

   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeShort(this.realFacingDirection.getIndex());
   }

   public void readSpawnData(ByteBuf additionalData) {
      this.updateFacingWithBoundingBox(EnumFacing.getFront(additionalData.readShort()));
   }
}
