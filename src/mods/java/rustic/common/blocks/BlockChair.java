package rustic.common.blocks;

import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.network.MessageDismountChair;
import rustic.common.network.PacketHandler;

public class BlockChair extends BlockBase {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   protected static final AxisAlignedBB CHAIR_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.125D, 0.875D);

   public BlockChair(String type) {
      super(Material.WOOD, "chair_" + type);
      this.setHardness(1.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setSoundType(SoundType.WOOD);
      Blocks.FIRE.setFireInfo(this, 5, 20);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CHAIR_AABB;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState blockState) {
      return false;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (player.getDistanceSqToCenter(pos) < 5.0D && !player.isSneaking() && !player.isRiding()) {
         if (!world.isRemote) {
            List<BlockChair.EntityChair> chairs = world.getEntitiesWithinAABB(BlockChair.EntityChair.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
            if (chairs.isEmpty()) {
               BlockChair.EntityChair chair = new BlockChair.EntityChair(world, pos, (EnumFacing)state.getValue(FACING));
               world.spawnEntity(chair);
               if (player.startRiding(chair)) {
                  player.setPositionAndUpdate(chair.posX, chair.posY, chair.posZ);
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }

   public static class EntityChair extends Entity {
      public EntityChair(World world) {
         super(world);
         this.setSize(0.0F, 0.0F);
      }

      public EntityChair(World world, BlockPos pos, EnumFacing facing) {
         super(world);
         if (facing != null) {
            this.rotationYaw = facing.getHorizontalAngle();
         }

         Vec3i facingVec = facing.getDirectionVec();
         double xOffset = (double)facingVec.getX() * -0.125D;
         double zOffset = (double)facingVec.getZ() * -0.125D;
         this.setPosition((double)pos.getX() + 0.5D + xOffset, (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.5D + zOffset);
         this.setSize(0.0F, 0.0F);
      }

      public void onUpdate() {
         super.onUpdate();
         BlockPos pos = this.getPosition();
         if (pos != null && !(this.world.getBlockState(pos).getBlock() instanceof BlockChair)) {
            this.setDead();
         } else {
            List<Entity> passengers = this.getPassengers();
            if (passengers.isEmpty()) {
               this.setDead();
            }

            if (!this.world.isRemote) {
               for(int i = 0; i < passengers.size(); ++i) {
                  Entity passenger = (Entity)passengers.get(i);
                  if (passenger.isSneaking() || passenger.getDistanceSq(this.posX, this.posY, this.posZ) >= 1.0D) {
                     this.setDead();
                  }
               }
            }

         }
      }

      public boolean canBeAttackedWithItem() {
         return false;
      }

      protected void entityInit() {
      }

      public void setDead() {
         super.setDead();
         if (this.world.isRemote) {
            PacketHandler.INSTANCE.sendToServer(new MessageDismountChair());
         }

      }

      @SideOnly(Side.CLIENT)
      public void applyOrientationToEntity(Entity entityToUpdate) {
         entityToUpdate.setRenderYawOffset(this.rotationYaw);
         float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
         float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
         entityToUpdate.prevRotationYaw += f1 - f;
         entityToUpdate.rotationYaw += f1 - f;
         entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
      }

      protected void readEntityFromNBT(NBTTagCompound compound) {
      }

      protected void writeEntityToNBT(NBTTagCompound compound) {
      }
   }
}
