package com.endertech.minecraft.forge.entities;

import com.endertech.common.CommonMath;
import com.endertech.common.CommonTime;
import com.endertech.minecraft.forge.blocks.ForgeExplosiveBlock;
import com.endertech.minecraft.forge.math.GameTime;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FuseEntity extends BlockStateCarrier {
   private int ticksToExplosion;
   private float explosionSize;
   private EntityLivingBase igniter;

   public FuseEntity(World world) {
      super(world);
   }

   public FuseEntity(World world, BlockPos pos, IBlockState carriedState, CommonTime.Interval ignitionTime, float explosionSize, @Nullable EntityLivingBase igniter) {
      super(world, pos, carriedState);
      this.ticksToExplosion = (int)ignitionTime.in(GameTime.SERVER_TICKS);
      this.explosionSize = explosionSize;
      this.igniter = igniter;
      CommonMath.Angle angle = CommonMath.Angle.random();
      this.motionX = -angle.sin() * 0.02D;
      this.motionY = 0.2D;
      this.motionZ = -angle.cos() * 0.02D;
   }

   protected void entityInit() {
      super.entityInit();
      this.preventEntitySpawning = true;
      this.isImmuneToFire = true;
   }

   public void onUpdate() {
      if (this.isServerSide() && this.ticksToExplosion-- <= 0) {
         this.explode();
         this.setDead();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (!this.hasNoGravity()) {
            this.motionY -= 0.04D;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.98D;
         this.motionY *= 0.98D;
         this.motionZ *= 0.98D;
         if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
            this.motionY *= -0.5D;
         }

         this.handleWaterMovement();
         this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   protected void explode() {
      ForgeExplosiveBlock<?> explosive = (ForgeExplosiveBlock)this.getCarriedBlockState().getBlock();
      Explosion explosion = this.world.createExplosion(this.getIgniter(), this.posX, this.posY, this.posZ, this.getExplosionSize(), true);
      explosive.doDestruction(this.world, this.getPosition(), this.getCarriedBlockState(), explosion);
   }

   @Nullable
   public EntityLivingBase getIgniter() {
      return this.igniter;
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger(FuseEntity.Tags.TICKS_TO_EXPLOSION.name(), this.ticksToExplosion);
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.ticksToExplosion = compound.getInteger(FuseEntity.Tags.TICKS_TO_EXPLOSION.name());
   }

   public float getExplosionSize() {
      return this.explosionSize;
   }

   @SideOnly(Side.CLIENT)
   public static class Renderer extends Render<FuseEntity> {
      public Renderer(RenderManager renderManagerIn) {
         super(renderManagerIn);
         this.shadowSize = 0.5F;
      }

      @Nullable
      protected ResourceLocation getEntityTexture(FuseEntity entity) {
         return TextureMap.LOCATION_BLOCKS_TEXTURE;
      }

      public void doRender(FuseEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
         IBlockState state = entity.getCarriedBlockState();
         if (state != null) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);
            this.bindEntityTexture(entity);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(state, entity.getBrightness());
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
            if (this.renderOutlines) {
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getTeamColor(entity));
               blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
            }

            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
         }
      }

      public static class Factory implements IRenderFactory<FuseEntity> {
         public Factory() {
         }

         public Render<? super FuseEntity> createRenderFor(RenderManager manager) {
            return new FuseEntity.Renderer(manager);
         }
      }
   }

   private static enum Tags {
      TICKS_TO_EXPLOSION;

      private Tags() {
      }
   }
}
