package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.common.utils.math.vec.ChildVecOrigin;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import com.creativemd.creativecore.common.utils.math.vec.VecOrigin;
import javax.vecmath.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SubWorld extends CreativeWorld {
   public World parentWorld;
   public IVecOrigin origin;
   @SideOnly(Side.CLIENT)
   public boolean shouldRender;

   public static SubWorld createFakeWorld(World world) {
      return (SubWorld)(world instanceof WorldServer ? new SubWorldServer((WorldServer)world) : new SubWorld(world));
   }

   protected SubWorld(World world) {
      super(new SaveHandlerFake(world.getWorldInfo()), world.getWorldInfo(), world.provider, new Profiler(), world.isRemote);
      this.chunkProvider = this.createChunkProvider();
      this.parentWorld = world;
   }

   public MinecraftServer getMinecraftServer() {
      return this.parentWorld.getMinecraftServer();
   }

   protected IChunkProvider createChunkProvider() {
      return new ChunkProviderFake(this, this.saveHandler.getChunkLoader(this.provider), new ChunkGeneratorFake(this));
   }

   protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return ((ChunkProviderFake)this.getChunkProvider()).chunkExists(x, z);
   }

   public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
   }

   public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_) {
      Vector3d pos = new Vector3d(p_190523_2_, p_190523_4_, p_190523_6_);
      this.origin.transformPointToWorld(pos);
      this.parentWorld.spawnAlwaysVisibleParticle(p_190523_1_, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);

      for(int i = 0; i < this.eventListeners.size(); ++i) {
         ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(p_190523_1_, false, true, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
      }

   }

   @SideOnly(Side.CLIENT)
   public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
      this.origin.transformPointToWorld(pos);
      this.parentWorld.spawnParticle(particleType, ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters);
      this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() || ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
   }

   private void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
      this.origin.transformPointToWorld(pos);

      for(int i = 0; i < this.eventListeners.size(); ++i) {
         ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(particleID, ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters);
      }

   }

   public IVecOrigin getOrigin() {
      return this.origin;
   }

   public void setOrigin(Vector3d center) {
      if (this.parentWorld instanceof IOrientatedWorld) {
         this.origin = new ChildVecOrigin(((IOrientatedWorld)this.parentWorld).getOrigin(), center);
      } else {
         this.origin = new VecOrigin(center);
      }

   }

   public boolean hasParent() {
      return true;
   }

   public World getParent() {
      return this.parentWorld;
   }

   public World getRealWorld() {
      return this.parentWorld instanceof SubWorld ? ((SubWorld)this.parentWorld).getRealWorld() : this.parentWorld;
   }

   public Entity getParentEntity() {
      return this.parent;
   }
}
