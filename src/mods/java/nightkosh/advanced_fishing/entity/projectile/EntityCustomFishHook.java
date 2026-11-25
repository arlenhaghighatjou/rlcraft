package nightkosh.advanced_fishing.entity.projectile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook.State;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nightkosh.advanced_fishing.core.CatchManager;
import nightkosh.advanced_fishing.core.MaterialManager;
import nightkosh.advanced_fishing.core.ParticlesManager;
import nightkosh.advanced_fishing.entity.item.EntityFireproofItem;

public class EntityCustomFishHook extends AbstractFishHook {
   protected boolean inGround;
   protected int ticksInGround;
   protected int ticksInAir;
   protected int ticksCatchable;
   protected int ticksCaughtDelay;
   protected int ticksCatchableDelay;
   protected float fishApproachAngle;

   public EntityCustomFishHook(World world) {
      super(world);
   }

   @SideOnly(Side.CLIENT)
   public EntityCustomFishHook(World world, EntityPlayer player, double x, double y, double z) {
      super(world, player, x, y, z);
   }

   public EntityCustomFishHook(World world, EntityPlayer player) {
      super(world, player);
   }

   public void onUpdate() {
      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.onEntityUpdate();
      if (this.getAngler() == null) {
         this.setDead();
      } else if (this.world.isRemote || !this.shouldStopFishing()) {
         if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               this.setDead();
               return;
            }
         }

         float f = 0.0F;
         BlockPos pos = new BlockPos(this);
         IBlockState state = this.world.getBlockState(pos);
         boolean isInLiquid = MaterialManager.MATERIAL_SET.contains(state.getMaterial());
         if (state.getMaterial() == Material.LAVA && !this.isImmuneToFire) {
            this.setDead();
         }

         if (isInLiquid) {
            f = BlockLiquid.getBlockLiquidHeight(state, this.world, pos);
         }

         if (this.currentState == State.FLYING) {
            if (this.caughtEntity != null) {
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
               this.currentState = State.HOOKED_IN_ENTITY;
               return;
            }

            if (f > 0.0F) {
               this.motionX *= 0.3D;
               this.motionY *= 0.2D;
               this.motionZ *= 0.3D;
               this.currentState = State.BOBBING;
               return;
            }

            if (!this.world.isRemote) {
               this.checkCollision();
            }

            if (!this.inGround && !this.onGround && !this.collidedHorizontally) {
               ++this.ticksInAir;
            } else {
               this.ticksInAir = 0;
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
            }
         } else {
            if (this.currentState == State.HOOKED_IN_ENTITY) {
               if (this.caughtEntity != null) {
                  if (this.caughtEntity.isDead) {
                     this.caughtEntity = null;
                     this.currentState = State.FLYING;
                  } else {
                     this.posX = this.caughtEntity.posX;
                     double d2 = (double)this.caughtEntity.height;
                     this.posY = this.caughtEntity.getEntityBoundingBox().minY + d2 * 0.8D;
                     this.posZ = this.caughtEntity.posZ;
                     this.setPosition(this.posX, this.posY, this.posZ);
                  }
               }

               return;
            }

            if (this.currentState == State.BOBBING) {
               this.motionX *= 0.9D;
               this.motionZ *= 0.9D;
               double d0 = this.posY + this.motionY - (double)pos.getY() - (double)f;
               if (Math.abs(d0) < 0.01D) {
                  d0 += Math.signum(d0) * 0.1D;
               }

               this.motionY -= d0 * (double)this.rand.nextFloat() * 0.2D;
               if (!this.world.isRemote && f > 0.0F) {
                  this.catchingFish(pos);
               }
            }
         }

         if (!isInLiquid) {
            this.motionY -= 0.03D;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.updateRotation();
         this.motionX *= 0.92D;
         this.motionY *= 0.92D;
         this.motionZ *= 0.92D;
         this.setPosition(this.posX, this.posY, this.posZ);
      }

   }

   protected boolean shouldStopFishing() {
      ItemStack mainStack = this.getAngler().getHeldItemMainhand();
      ItemStack offStack = this.getAngler().getHeldItemOffhand();
      boolean flag = this.isFishingPoleStack(mainStack);
      boolean flag1 = this.isFishingPoleStack(offStack);
      if (!this.getAngler().isDead && this.getAngler().isEntityAlive() && (flag || flag1) && this.getDistanceSq(this.getAngler()) <= 1024.0D) {
         return false;
      } else {
         this.setDead();
         return true;
      }
   }

   protected void catchingFish(BlockPos pos) {
      WorldServer worldserver = (WorldServer)this.world;
      int i = 1;
      BlockPos blockpos = pos.up();
      if ((double)this.rand.nextFloat() < 0.25D && this.world.isRainingAt(blockpos)) {
         ++i;
      }

      if ((double)this.rand.nextFloat() < 0.5D && !this.world.canSeeSky(blockpos)) {
         --i;
      }

      if (this.ticksCatchable > 0) {
         --this.ticksCatchable;
         if (this.ticksCatchable <= 0) {
            this.ticksCaughtDelay = 0;
            this.ticksCatchableDelay = 0;
         } else {
            this.motionY -= 0.2D * (double)this.rand.nextFloat() * (double)this.rand.nextFloat();
         }
      } else if (this.ticksCatchableDelay > 0) {
         this.ticksCatchableDelay -= i;
         int minY = MathHelper.floor(this.getEntityBoundingBox().minY);
         Block liquidBlock = worldserver.getBlockState(new BlockPos(this.posX, (double)minY, this.posZ)).getBlock();
         if (this.ticksCatchableDelay > 0) {
            this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
            float angle = this.fishApproachAngle * 0.0175F;
            float sin = MathHelper.sin(angle);
            float cos = MathHelper.cos(angle);
            double xPos = this.posX + (double)(sin * (float)this.ticksCatchableDelay) * 0.1D;
            double yPos = (double)(minY + 1);
            double zPos = this.posZ + (double)(cos * (float)this.ticksCatchableDelay) * 0.1D;
            if (MaterialManager.MATERIAL_SET.contains(worldserver.getBlockState(new BlockPos(xPos, (double)minY, zPos)).getMaterial())) {
               if ((double)this.rand.nextFloat() < 0.15D) {
                  ParticlesManager.INSTANCE.getBubbleParticles(liquidBlock).spawn(worldserver, xPos, yPos - 0.1D, zPos, 1, (double)sin, 0.1D, (double)cos, 0.0D);
               }

               float zOffset = sin * 0.04F;
               float xOffset = cos * 0.04F;
               ParticlesManager.INSTANCE.getWakeParticles(liquidBlock).spawn(worldserver, xPos, yPos, zPos, 0, (double)xOffset, 0.01D, (double)(-zOffset), 1.0D);
               ParticlesManager.INSTANCE.getWakeParticles(liquidBlock).spawn(worldserver, xPos, yPos, zPos, 0, (double)(-xOffset), 0.01D, (double)zOffset, 1.0D);
            }
         } else {
            this.motionY = -0.4D * (double)MathHelper.nextFloat(this.rand, 0.6F, 1.0F);
            this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            double d3 = this.getEntityBoundingBox().minY + 0.5D;
            ParticlesManager.INSTANCE.getBubbleParticles(liquidBlock).spawn(worldserver, this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.2D);
            ParticlesManager.INSTANCE.getWakeParticles(liquidBlock).spawn(worldserver, this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.2D);
            this.ticksCatchable = MathHelper.getInt(this.rand, 20, 40);
         }
      } else if (this.ticksCaughtDelay > 0) {
         this.ticksCaughtDelay -= i;
         float f5 = 0.15F;
         if (this.ticksCaughtDelay < 20) {
            f5 += (float)(20 - this.ticksCaughtDelay) * 0.05F;
         } else if (this.ticksCaughtDelay < 40) {
            f5 += (float)(40 - this.ticksCaughtDelay) * 0.02F;
         } else if (this.ticksCaughtDelay < 60) {
            f5 += (float)(60 - this.ticksCaughtDelay) * 0.01F;
         }

         if (this.rand.nextFloat() < f5) {
            float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
            float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
            int minY = MathHelper.floor(this.getEntityBoundingBox().minY);
            double xPos = this.posX + (double)(MathHelper.sin(f6) * f7) * 0.1D;
            double yPos = (double)(minY + 1);
            double zPos = this.posZ + (double)(MathHelper.cos(f6) * f7) * 0.1D;
            if (MaterialManager.MATERIAL_SET.contains(worldserver.getBlockState(new BlockPos(xPos, (double)minY, zPos)).getMaterial())) {
               ParticlesManager.INSTANCE.getSplashParticles(worldserver.getBlockState(new BlockPos(this.posX, (double)minY, this.posZ)).getBlock()).spawn(worldserver, this.rand, xPos, yPos, zPos);
            }
         }

         if (this.ticksCaughtDelay <= 0) {
            this.fishApproachAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
            this.ticksCatchableDelay = MathHelper.getInt(this.rand, 20, 80);
         }
      } else {
         this.ticksCaughtDelay = MathHelper.getInt(this.rand, 100, 600);
         this.ticksCaughtDelay -= this.lureSpeed * 20 * 5;
      }

   }

   protected void doWaterSplashEffect() {
      Entity entity = (Entity)(this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this);
      float f = entity == this ? 0.2F : 0.9F;
      float f1 = MathHelper.sqrt(entity.motionX * entity.motionX * 0.2D + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.2D) * f;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      this.playSound(this.getSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      if (!this.world.isRemote) {
         float minY = (float)MathHelper.floor(this.getEntityBoundingBox().minY);
         Block liquidBlock = this.world.getBlockState(new BlockPos(this.posX, (double)minY, this.posZ)).getBlock();

         for(int i = 0; (float)i < 1.0F + this.width * 20.0F; ++i) {
            float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            ParticlesManager.INSTANCE.getBubbleParticles(liquidBlock).spawn((WorldServer)this.world, this.posX + (double)f3, (double)(minY + 1.0F), this.posZ + (double)f4, 1, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ, 0.0D);
         }

         for(int j = 0; (float)j < 1.0F + this.width * 20.0F; ++j) {
            float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            ParticlesManager.INSTANCE.getSplashParticles(liquidBlock).spawn((WorldServer)this.world, this.rand, this.posX + (double)f5, (double)(minY + 1.0F), this.posZ + (double)f6);
         }
      }

   }

   public int handleHookRetraction() {
      if (!this.world.isRemote && this.getAngler() != null) {
         int i = 0;
         ItemFishedEvent event = null;
         if (this.caughtEntity != null) {
            this.bringInHookedEntity();
            this.world.setEntityState(this, (byte)31);
            i = this.caughtEntity instanceof EntityItem ? 3 : 5;
         } else if (this.ticksCatchable > 0) {
            List<ItemStack> result = this.getCatch();
            event = new ItemFishedEvent(result, this.inGround ? 2 : 1, this);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               this.setDead();
               return event.getRodDamage();
            }

            for(ItemStack stack : result) {
               EntityItem entityitem = this.getCatchEntityItem(stack);
               double d0 = this.getAngler().posX - this.posX;
               double d1 = this.getAngler().posY - this.posY;
               double d2 = this.getAngler().posZ - this.posZ;
               double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
               entityitem.motionX = d0 * 0.1D;
               entityitem.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
               entityitem.motionZ = d2 * 0.1D;
               this.world.spawnEntity(entityitem);
               this.getAngler().world.spawnEntity(new EntityXPOrb(this.getAngler().world, this.getAngler().posX, this.getAngler().posY + 0.5D, this.getAngler().posZ + 0.5D, this.rand.nextInt(6) + 1));
               Item item = stack.getItem();
               if (item == Items.FISH || item == Items.COOKED_FISH || item == nightkosh.advanced_fishing.core.Items.FISH) {
                  this.getAngler().addStat(StatList.FISH_CAUGHT, 1);
               }
            }

            i = 1;
         }

         if (this.inGround) {
            i = 2;
         }

         this.setDead();
         return event == null ? i : event.getRodDamage();
      } else {
         return 0;
      }
   }

   protected EntityItem getCatchEntityItem(ItemStack stack) {
      return new EntityItem(this.world, this.posX, this.posY + 0.5D, this.posZ, stack);
   }

   protected EntityItem getFireproofCatchEntityItem(ItemStack stack) {
      return new EntityFireproofItem(this.world, this.posX, this.posY + 0.5D, this.posZ, stack);
   }

   protected List<ItemStack> getCatch() {
      List<ItemStack> result = new ArrayList(1);
      Block liquidBlock = this.world.getBlockState(new BlockPos(this.posX, (double)MathHelper.floor(this.getEntityBoundingBox().minY), this.posZ)).getBlock();
      List<ItemStack> tempList = CatchManager.INSTANCE.getCatch(liquidBlock).getCatch(this.world, this.getPosition(), ((float)this.luck + this.getAngler().getLuck()) * 1.5F);
      result.add(tempList.get(this.rand.nextInt(tempList.size())));
      return result;
   }
}
