package vazkii.quark.vanity.feature;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.RenderBlank;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageDismountSeat;

public class SitInStairs extends Feature {
   public SitInStairs() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      String name = "quark:seat";
      EntityRegistry.registerModEntity(new ResourceLocation(name), SitInStairs.Seat.class, name, 12, Quark.instance, 16, 128, false);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(SitInStairs.Seat.class, RenderBlank::new);
   }

   @SubscribeEvent
   public void onInteract(RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!player.isSneaking() && player.getRidingEntity() == null) {
         World world = event.getWorld();
         if (!world.isRemote) {
            BlockPos pos = event.getPos();
            Vec3d vec = new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
            double maxDist = 2.0D;
            if ((vec.x - player.posX) * (vec.x - player.posX) + (vec.y - player.posY) * (vec.y - player.posY) + (vec.z - player.posZ) * (vec.z - player.posZ) <= maxDist * maxDist) {
               IBlockState state = world.getBlockState(pos);
               ItemStack stack1 = player.getHeldItemMainhand();
               ItemStack stack2 = player.getHeldItemOffhand();
               if (stack1.isEmpty() && stack2.isEmpty()) {
                  if (state.getBlock() instanceof BlockStairs && state.getValue(BlockStairs.HALF) == EnumHalf.BOTTOM && !state.isSideSolid(world, pos, (EnumFacing)Objects.requireNonNull(event.getFace())) && canBeAbove(world, pos)) {
                     List<SitInStairs.Seat> seats = world.getEntitiesWithinAABB(SitInStairs.Seat.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
                     if (seats.isEmpty()) {
                        SitInStairs.Seat seat = new SitInStairs.Seat(world, pos);
                        world.spawnEntity(seat);
                        if (event.getEntityPlayer().startRiding(seat)) {
                           event.getEntityPlayer().setPositionAndUpdate(seat.posX, seat.posY, seat.posZ);
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public static boolean canBeAbove(World world, BlockPos pos) {
      BlockPos upPos = pos.up();
      IBlockState state = world.getBlockState(upPos);
      return state.getCollisionBoundingBox(world, upPos) == null;
   }

   public static class Seat extends Entity {
      public Seat(World world, BlockPos pos) {
         this(world);
         this.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
      }

      public Seat(World par1World) {
         super(par1World);
         this.setSize(0.25F, 0.25F);
      }

      public double getMountedYOffset() {
         return -0.25D;
      }

      @Nonnull
      public BlockPos getPosition() {
         return new BlockPos(this.posX, this.posY, this.posZ);
      }

      public boolean canBeAttackedWithItem() {
         return false;
      }

      public void onUpdate() {
         super.onUpdate();
         BlockPos pos = this.getPosition();
         if (this.getEntityWorld().getBlockState(pos).getBlock() instanceof BlockStairs && SitInStairs.canBeAbove(this.getEntityWorld(), pos)) {
            List<Entity> passengers = this.getPassengers();
            if (passengers.isEmpty()) {
               this.setDead();
            }

            for(Entity e : passengers) {
               if (e.isSneaking() || e.getDistanceSq(this) >= 1.0D) {
                  this.setDead();
               }
            }

         } else {
            this.setDead();
         }
      }

      public void setDead() {
         super.setDead();
         if (this.world.isRemote) {
            NetworkHandler.INSTANCE.sendToServer(new MessageDismountSeat());
         }

      }

      protected void entityInit() {
      }

      protected void readEntityFromNBT(@Nonnull NBTTagCompound nbttagcompound) {
      }

      protected void writeEntityToNBT(@Nonnull NBTTagCompound nbttagcompound) {
      }
   }
}
