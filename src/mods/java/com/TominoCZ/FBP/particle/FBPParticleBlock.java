package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;
import javax.vecmath.Vector2d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class FBPParticleBlock extends Particle {
   public BlockPos pos;
   Block block;
   IBlockState blockState;
   BlockModelRenderer mr;
   IBakedModel modelPrefab;
   Minecraft mc;
   EnumFacing facing;
   FBPVector3d prevRot;
   FBPVector3d rot;
   long textureSeed;
   float startingHeight;
   float startingAngle;
   float step = 0.00275F;
   float height;
   float prevHeight;
   float smoothHeight;
   boolean lookingUp;
   boolean spawned = false;
   long tick = -1L;
   boolean blockSet = false;
   TileEntity tileEntity;

   public FBPParticleBlock(World worldIn, double posXIn, double posYIn, double posZIn, IBlockState state, long rand) {
      super(worldIn, posXIn, posYIn, posZIn);
      this.pos = new BlockPos(posXIn, posYIn, posZIn);
      this.mc = Minecraft.getMinecraft();
      this.facing = this.mc.player.getHorizontalFacing();
      this.lookingUp = MathHelper.wrapDegrees(this.mc.player.rotationPitch).floatValue() <= 0.0F;
      this.prevHeight = this.height = this.startingHeight = (float)FBP.random.nextDouble(0.065D, 0.115D);
      this.startingAngle = (float)FBP.random.nextDouble(0.03125D, 0.0635D);
      this.prevRot = new FBPVector3d();
      this.rot = new FBPVector3d();
      switch(this.facing) {
      case EAST:
         this.rot.z = (double)(-this.startingAngle);
         this.rot.x = (double)(-this.startingAngle);
         break;
      case NORTH:
         this.rot.x = (double)(-this.startingAngle);
         this.rot.z = (double)this.startingAngle;
         break;
      case SOUTH:
         this.rot.x = (double)this.startingAngle;
         this.rot.z = (double)(-this.startingAngle);
         break;
      case WEST:
         this.rot.z = (double)this.startingAngle;
         this.rot.x = (double)this.startingAngle;
      }

      this.textureSeed = rand;
      this.block = (this.blockState = state).getBlock();
      this.mr = this.mc.getBlockRendererDispatcher().getBlockModelRenderer();
      this.canCollide = false;
      this.modelPrefab = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
      if (this.modelPrefab == null) {
         this.canCollide = true;
         this.isExpired = true;
      }

      this.tileEntity = worldIn.getTileEntity(this.pos);
   }

   public void onUpdate() {
      if (++this.particleAge >= 10) {
         this.killParticle();
      }

      if (!this.canCollide) {
         IBlockState s = this.mc.world.getBlockState(this.pos);
         if (s.getBlock() != FBP.FBPBlock || s.getBlock() == this.block) {
            if (this.blockSet && s.getBlock() == Blocks.AIR) {
               this.killParticle();
               FBP.FBPBlock.onBlockDestroyedByPlayer(this.mc.world, this.pos, s);
               this.mc.world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), 2);
               return;
            }

            FBP.FBPBlock.copyState(this.mc.world, this.pos, this.blockState, this);
            this.mc.world.setBlockState(this.pos, FBP.FBPBlock.getDefaultState(), 2);
            Chunk c = this.mc.world.getChunkFromBlockCoords(this.pos);
            c.resetRelightChecks();
            c.setLightPopulated(true);
            FBPRenderUtil.markBlockForRender(this.pos);
            this.blockSet = true;
         }

         this.spawned = true;
      }

      if (!this.isExpired && !this.mc.isGamePaused()) {
         this.prevHeight = this.height;
         this.prevRot.copyFrom(this.rot);
         switch(this.facing) {
         case EAST:
            this.rot.z += (double)this.step;
            this.rot.x += (double)this.step;
            break;
         case NORTH:
            this.rot.x += (double)this.step;
            this.rot.z -= (double)this.step;
            break;
         case SOUTH:
            this.rot.x -= (double)this.step;
            this.rot.z += (double)this.step;
            break;
         case WEST:
            this.rot.z -= (double)this.step;
            this.rot.x -= (double)this.step;
         }

         this.height -= this.step * 5.0F;
         this.step *= 1.5678982F;
      }
   }

   public void renderParticle(WorldRenderer buff, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!this.isExpired) {
         if (this.canCollide) {
            Block b = this.mc.world.getBlockState(this.pos).getBlock();
            if (this.block != b && b != Blocks.AIR && this.mc.world.getBlockState(this.pos).getBlock() != this.blockState.getBlock()) {
               this.mc.world.setBlockState(this.pos, this.blockState, 2);
               if (this.tileEntity != null) {
                  this.mc.world.setTileEntity(this.pos, this.tileEntity);
               }

               this.mc.world.sendPacketToServer(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
               FBPRenderUtil.markBlockForRender(this.pos);
               FBP.INSTANCE.eventHandler.removePosEntry(this.pos);
            }

            if (this.tick >= 1L) {
               this.killParticle();
               return;
            }

            ++this.tick;
         }

         if (this.spawned) {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX) - 0.5F;
            float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY) - 0.5F;
            float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ) - 0.5F;
            this.smoothHeight = (float)((double)this.prevHeight + (double)(this.height - this.prevHeight) * (double)partialTicks);
            FBPVector3d smoothRot = this.rot.partialVec(this.prevRot, partialTicks);
            if (this.smoothHeight <= 0.0F) {
               this.smoothHeight = 0.0F;
            }

            FBPVector3d t = new FBPVector3d(0.0D, (double)this.smoothHeight, 0.0D);
            FBPVector3d tRot = new FBPVector3d(0.0D, (double)this.smoothHeight, 0.0D);
            switch(this.facing) {
            case EAST:
               if (smoothRot.z > 0.0D) {
                  this.canCollide = true;
                  smoothRot.z = 0.0D;
                  smoothRot.x = 0.0D;
               }

               t.x = (double)(-this.smoothHeight);
               t.z = (double)this.smoothHeight;
               tRot.x = 1.0D;
               break;
            case NORTH:
               if (smoothRot.z < 0.0D) {
                  this.canCollide = true;
                  smoothRot.x = 0.0D;
                  smoothRot.z = 0.0D;
               }

               t.x = (double)this.smoothHeight;
               t.z = (double)this.smoothHeight;
               break;
            case SOUTH:
               if (smoothRot.x < 0.0D) {
                  this.canCollide = true;
                  smoothRot.x = 0.0D;
                  smoothRot.z = 0.0D;
               }

               t.x = (double)(-this.smoothHeight);
               t.z = (double)(-this.smoothHeight);
               tRot.x = 1.0D;
               tRot.z = 1.0D;
               break;
            case WEST:
               if (smoothRot.z < 0.0D) {
                  this.canCollide = true;
                  smoothRot.z = 0.0D;
                  smoothRot.x = 0.0D;
               }

               t.x = (double)this.smoothHeight;
               t.z = (double)(-this.smoothHeight);
               tRot.z = 1.0D;
            }

            if (FBP.spawnPlaceParticles && this.canCollide && this.tick == 0L && (!FBP.frozen || FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || this.block != Blocks.REDSTONE_BLOCK) && this.mc.gameSettings.particleSetting < 2) {
               this.spawnParticles();
            }

            buff.setTranslation((double)(-this.pos.getX()), (double)(-this.pos.getY()), (double)(-this.pos.getZ()));
            Tessellator.getInstance().draw();
            this.mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            buff.begin(7, DefaultVertexFormats.BLOCK);
            GlStateManager.pushMatrix();
            GlStateManager.enableCull();
            GlStateManager.enableColorMaterial();
            GL11.glColorMaterial(1028, 5634);
            GlStateManager.translate(f5, f6, f7);
            GlStateManager.translate(tRot.x, tRot.y, tRot.z);
            GlStateManager.rotate((float)Math.toDegrees(smoothRot.x), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((float)Math.toDegrees(smoothRot.z), 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-tRot.x, -tRot.y, -tRot.z);
            GlStateManager.translate(t.x, t.y, t.z);
            if (FBP.animSmoothLighting) {
               this.mr.renderModelSmooth(this.mc.world, this.modelPrefab, this.blockState, this.pos, buff, false, this.textureSeed);
            } else {
               this.mr.renderModelFlat(this.mc.world, this.modelPrefab, this.blockState, this.pos, buff, false, this.textureSeed);
            }

            buff.setTranslation(0.0D, 0.0D, 0.0D);
            Tessellator.getInstance().draw();
            GlStateManager.popMatrix();
            this.mc.getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
            buff.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
         }
      }
   }

   private void spawnParticles() {
      if (!(this.mc.world.getBlockState(this.pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockAir)) {
         AxisAlignedBB aabb = this.block.getSelectedBoundingBox(this.blockState, this.mc.world, this.pos);
         Vector2d[] corners = new Vector2d[]{new Vector2d(aabb.minX, aabb.minZ), new Vector2d(aabb.maxX, aabb.maxZ), new Vector2d(aabb.minX, aabb.maxZ), new Vector2d(aabb.maxX, aabb.minZ)};
         Vector2d middle = new Vector2d((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getZ() + 0.5F));

         for(Vector2d corner : corners) {
            double mX = middle.x - corner.x;
            double mZ = middle.y - corner.y;
            mX = mX / -0.5D;
            mZ = mZ / -0.5D;
            this.mc.effectRenderer.addEffect((new FBPParticleDigging(this.mc.world, corner.x, (double)((float)this.pos.getY() + 0.1F), corner.y, mX, 0.0D, mZ, 0.6F, 1.0F, 1.0F, 1.0F, this.block.getActualState(this.blockState, this.mc.world, this.pos), (EnumFacing)null, this.particleTexture)).multipleParticleScaleBy(0.5F).multiplyVelocity(0.5F));
         }

         for(Vector2d corner : corners) {
            if (corner != null) {
               double mX = middle.x - corner.x;
               double mZ = middle.y - corner.y;
               mX = mX / -0.45D;
               mZ = mZ / -0.45D;
               this.mc.effectRenderer.addEffect((new FBPParticleDigging(this.mc.world, corner.x, (double)((float)this.pos.getY() + 0.1F), corner.y, mX / 3.0D, 0.0D, mZ / 3.0D, 0.6F, 1.0F, 1.0F, 1.0F, this.block.getActualState(this.blockState, this.mc.world, this.pos), (EnumFacing)null, this.particleTexture)).multipleParticleScaleBy(0.75F).multiplyVelocity(0.75F));
            }
         }

      }
   }

   public void killParticle() {
      this.isExpired = true;
      FBP.FBPBlock.blockNodes.remove(this.pos);
      FBP.INSTANCE.eventHandler.removePosEntry(this.pos);
   }

   public void setExpired() {
      FBP.INSTANCE.eventHandler.removePosEntry(this.pos);
   }
}
