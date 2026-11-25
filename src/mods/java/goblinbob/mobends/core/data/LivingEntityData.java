package goblinbob.mobends.core.data;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class LivingEntityData<E extends EntityLivingBase> extends EntityData<E> {
   protected float ticksInAir = 100.0F;
   protected float ticksAfterTouchdown = 100.0F;
   protected float ticksAfterAttack = 100.0F;
   protected float ticksFalling = 100.0F;
   protected float climbingCycle = 0.0F;
   protected boolean alreadyAttacked = false;
   protected boolean climbing = false;
   public OverridableProperty<Float> limbSwing = new OverridableProperty<Float>(0.0F);
   public OverridableProperty<Float> limbSwingAmount = new OverridableProperty<Float>(0.0F);
   public OverridableProperty<Float> swingProgress = new OverridableProperty<Float>(0.0F);
   public OverridableProperty<Float> headYaw = new OverridableProperty<Float>(0.0F);
   public OverridableProperty<Float> headPitch = new OverridableProperty<Float>(0.0F);

   public LivingEntityData(E entity) {
      super(entity);
   }

   public void setClimbing(boolean flag) {
      this.climbing = flag;
   }

   public float getClimbingCycle() {
      return this.climbingCycle;
   }

   public float getTicksInAir() {
      return this.ticksInAir;
   }

   public float getTicksAfterTouchdown() {
      return this.ticksAfterTouchdown;
   }

   public float getTicksAfterAttack() {
      return this.ticksAfterAttack;
   }

   public float getTicksFalling() {
      return this.ticksFalling;
   }

   public boolean isClimbing() {
      return this.climbing;
   }

   public void updateClient() {
      super.updateClient();
      boolean calcOnGroundResult = this.calcOnGround();
      if (calcOnGroundResult & !this.onGround) {
         this.onTouchdown();
         this.onGround = true;
      }

      if (!calcOnGroundResult & this.onGround | (this.prevMotionY <= 0.0D && this.motionY - this.prevMotionY > 0.4D && this.ticksInAir > 2.0F)) {
         this.onLiftoff();
         this.onGround = false;
      }

      if (this.calcClimbing()) {
         this.climbingCycle = (float)((double)this.climbingCycle + this.motionY * 2.5999999046325684D);
         this.climbing = true;
      } else {
         this.climbing = false;
      }

      if (((EntityLivingBase)this.entity).isSwingInProgress) {
         if (!this.alreadyAttacked || this.ticksAfterAttack > 5.0F) {
            this.onAttack();
            this.alreadyAttacked = true;
         }
      } else {
         this.alreadyAttacked = false;
      }

   }

   public void update(float partialTicks) {
      super.update(partialTicks);
      if (this.isOnGround()) {
         this.ticksAfterTouchdown += DataUpdateHandler.ticksPerFrame;
      } else {
         this.ticksInAir += DataUpdateHandler.ticksPerFrame;
         if (this.motionY < 0.0D) {
            this.ticksFalling += DataUpdateHandler.ticksPerFrame;
         } else {
            this.ticksFalling = 0.0F;
         }
      }

      this.ticksAfterAttack += DataUpdateHandler.ticksPerFrame;
   }

   public void onTouchdown() {
      this.ticksAfterTouchdown = 0.0F;
      this.ticksFalling = 0.0F;
   }

   public void onLiftoff() {
      this.ticksInAir = 0.0F;
   }

   public void onAttack() {
      this.ticksAfterAttack = 0.0F;
   }

   public float getClimbingRotation() {
      return this.getLadderFacing().getHorizontalAngle() + 180.0F;
   }

   private static boolean isBlockClimbable(IBlockState state) {
      return state.getBlock() instanceof BlockLadder || state.getBlock() instanceof BlockVine;
   }

   private static EnumFacing getClimbableBlockFacing(IBlockState state) {
      if (state.getBlock() instanceof BlockLadder) {
         return (EnumFacing)state.getValue(BlockLadder.FACING);
      } else {
         if (state.getBlock() instanceof BlockVine) {
            if (((Boolean)state.getValue(BlockVine.EAST)).booleanValue()) {
               return EnumFacing.WEST;
            }

            if (((Boolean)state.getValue(BlockVine.WEST)).booleanValue()) {
               return EnumFacing.EAST;
            }

            if (((Boolean)state.getValue(BlockVine.NORTH)).booleanValue()) {
               return EnumFacing.SOUTH;
            }

            if (((Boolean)state.getValue(BlockVine.SOUTH)).booleanValue()) {
               return EnumFacing.NORTH;
            }
         }

         return EnumFacing.NORTH;
      }
   }

   public EnumFacing getLadderFacing() {
      BlockPos position = new BlockPos(Math.floor(((EntityLivingBase)this.entity).posX), Math.floor(((EntityLivingBase)this.entity).posY), Math.floor(((EntityLivingBase)this.entity).posZ));
      IBlockState block = ((EntityLivingBase)this.entity).world.getBlockState(position);
      IBlockState blockBelow = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, -1, 0));
      IBlockState blockBelow2 = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, -2, 0));
      EnumFacing facing = EnumFacing.NORTH;
      facing = getClimbableBlockFacing(block);
      if (facing == EnumFacing.NORTH) {
         facing = getClimbableBlockFacing(blockBelow);
      }

      if (facing == EnumFacing.NORTH) {
         facing = getClimbableBlockFacing(blockBelow2);
      }

      return facing;
   }

   public boolean calcClimbing() {
      if (this.entity != null && ((EntityLivingBase)this.entity).world != null) {
         BlockPos position = new BlockPos(Math.floor(((EntityLivingBase)this.entity).posX), Math.floor(((EntityLivingBase)this.entity).posY), Math.floor(((EntityLivingBase)this.entity).posZ));
         IBlockState block = ((EntityLivingBase)this.entity).world.getBlockState(position);
         IBlockState blockBelow = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, -1, 0));
         IBlockState blockBelow2 = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, -2, 0));
         return ((EntityLivingBase)this.entity).isOnLadder() && !this.isOnGround() && (isBlockClimbable(block) || isBlockClimbable(blockBelow) || isBlockClimbable(blockBelow2));
      } else {
         return false;
      }
   }

   public float getLedgeHeight() {
      float clientY = (float)(((EntityLivingBase)this.entity).posY + (((EntityLivingBase)this.entity).posY - ((EntityLivingBase)this.entity).prevPosY) * (double)DataUpdateHandler.partialTicks);
      BlockPos position = new BlockPos(Math.floor(((EntityLivingBase)this.entity).posX), Math.floor(((EntityLivingBase)this.entity).posY), Math.floor(((EntityLivingBase)this.entity).posZ));
      IBlockState block = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, 2, 0));
      IBlockState blockBelow = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, 1, 0));
      IBlockState blockBelow2 = ((EntityLivingBase)this.entity).world.getBlockState(position.add(0, 0, 0));
      if (!isBlockClimbable(block)) {
         if (!isBlockClimbable(blockBelow)) {
            return !isBlockClimbable(blockBelow2) ? clientY - (float)((int)clientY) + 2.0F : clientY - (float)((int)clientY) + 1.0F;
         } else {
            return clientY - (float)((int)clientY);
         }
      } else {
         return -2.0F;
      }
   }

   public boolean isDrawingBow() {
      if (((EntityLivingBase)this.entity).getItemInUseCount() > 0) {
         ItemStack mainItemStack = ((EntityLivingBase)this.entity).getHeldItemMainhand();
         ItemStack offItemStack = ((EntityLivingBase)this.entity).getHeldItemOffhand();
         if (!mainItemStack.isEmpty() && mainItemStack.getItemUseAction() == EnumAction.BOW || !offItemStack.isEmpty() && offItemStack.getItemUseAction() == EnumAction.BOW) {
            return true;
         }
      }

      return false;
   }

   public E getEntity() {
      return (E)(this.entity);
   }
}
