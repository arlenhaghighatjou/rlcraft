package mods.betterfoliage.client.chunk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.Client;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\"\u0010\u000e\u001a\u00020\u000f\"\u0004\b\u0000\u0010\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00100\u000b2\u0006\u0010\u0012\u001a\u00020\u0013J1\u0010\u0014\u001a\u0004\u0018\u0001H\u0010\"\u0004\b\u0000\u0010\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00100\u000b2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0012\u001a\u00020\u0013¢\u0006\u0002\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001aH\u0007J\u0010\u0010\u001b\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001cH\u0007J\u0010\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001eH\u0007J0\u0010\u001f\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020 2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\"2\u0006\u0010$\u001a\u00020%H\u0016R\u001d\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\t\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\n¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006&"},
   d2 = {"Lmods/betterfoliage/client/chunk/ChunkOverlayManager;", "Lmods/betterfoliage/client/chunk/IBlockUpdateListener;", "()V", "chunkData", "", "Lnet/minecraft/util/math/ChunkPos;", "Lmods/betterfoliage/client/chunk/ChunkOverlayData;", "getChunkData", "()Ljava/util/Map;", "layers", "", "Lmods/betterfoliage/client/chunk/ChunkOverlayLayer;", "getLayers", "()Ljava/util/List;", "clear", "", "T", "layer", "pos", "Lnet/minecraft/util/math/BlockPos;", "get", "world", "Lnet/minecraft/world/IBlockAccess;", "(Lmods/betterfoliage/client/chunk/ChunkOverlayLayer;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;", "handleLoadChunk", "event", "Lnet/minecraftforge/event/world/ChunkEvent$Load;", "handleLoadWorld", "Lnet/minecraftforge/event/world/WorldEvent$Load;", "handleUnloadChunk", "Lnet/minecraftforge/event/world/ChunkEvent$Unload;", "notifyBlockUpdate", "Lnet/minecraft/world/World;", "oldState", "Lnet/minecraft/block/state/IBlockState;", "newState", "flags", "", "BetterFoliage-MC1.12"}
)
public final class ChunkOverlayManager implements IBlockUpdateListener {
   @NotNull
   private static final Map<ChunkPos, ChunkOverlayData> chunkData = (Map)(new LinkedHashMap());
   @NotNull
   private static final List<ChunkOverlayLayer<?>> layers = (List)(new ArrayList());
   public static final ChunkOverlayManager INSTANCE;

   @NotNull
   public final Map<ChunkPos, ChunkOverlayData> getChunkData() {
      return chunkData;
   }

   @NotNull
   public final List<ChunkOverlayLayer<?>> getLayers() {
      return layers;
   }

   @Nullable
   public final <T> T get(@NotNull ChunkOverlayLayer<T> layer, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(layer, "layer");
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      ChunkOverlayData var10000 = (ChunkOverlayData)chunkData.get(new ChunkPos(pos));
      if (var10000 != null) {
         ChunkOverlayData data = var10000;
         Object var5 = data.get(layer, pos);
         boolean var6 = false;
         boolean var7 = false;
         int $i$a$-let-ChunkOverlayManager$get$1 = 0;
         if (var5 != ChunkOverlayData.Companion.getUNCALCULATED()) {
            return (T)var5;
         } else {
            Object newValue = layer.calculate(world, pos);
            data.set(layer, pos, newValue);
            return (T)newValue;
         }
      } else {
         return (T)null;
      }
   }

   public final <T> void clear(@NotNull ChunkOverlayLayer<T> layer, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(layer, "layer");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      ChunkOverlayData var10000 = (ChunkOverlayData)chunkData.get(new ChunkPos(pos));
      if (var10000 != null) {
         var10000.clear(layer, pos);
      }

   }

   public void notifyBlockUpdate(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState oldState, @NotNull IBlockState newState, int flags) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Intrinsics.checkParameterIsNotNull(oldState, "oldState");
      Intrinsics.checkParameterIsNotNull(newState, "newState");
      if (chunkData.containsKey(new ChunkPos(pos))) {
         Iterable $this$forEach$iv = (Iterable)layers;
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            ChunkOverlayLayer layer = (ChunkOverlayLayer)element$iv;
            int $i$a$-forEach-ChunkOverlayManager$notifyBlockUpdate$1 = 0;
            layer.onBlockUpdate((IBlockAccess)world, pos);
         }
      }

   }

   @SubscribeEvent
   public final void handleLoadWorld(@NotNull Load event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getWorld() instanceof WorldClient) {
         event.getWorld().addEventListener(this);
      }

   }

   @SubscribeEvent
   public final void handleLoadChunk(@NotNull net.minecraftforge.event.world.ChunkEvent.Load event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getWorld() instanceof WorldClient && !(event.getChunk() instanceof EmptyChunk)) {
         Map var10000 = chunkData;
         Chunk var10001 = event.getChunk();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.chunk");
         ChunkPos var2 = var10001.getPos();
         Intrinsics.checkExpressionValueIsNotNull(var2, "event.chunk.pos");
         var10000.put(var2, new ChunkOverlayData(layers));
      }

   }

   @SubscribeEvent
   public final void handleUnloadChunk(@NotNull Unload event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getWorld() instanceof WorldClient) {
         Map var10000 = chunkData;
         Chunk var10001 = event.getChunk();
         Intrinsics.checkExpressionValueIsNotNull(var10001, "event.chunk");
         var10000.remove(var10001.getPos());
      }

   }

   private ChunkOverlayManager() {
   }

   static {
      ChunkOverlayManager var0 = new ChunkOverlayManager();
      INSTANCE = var0;
      Client var10000 = Client.INSTANCE;
      Level var10001 = Level.INFO;
      Intrinsics.checkExpressionValueIsNotNull(Level.INFO, "Level.INFO");
      var10000.log(var10001, "Initializing client overlay manager");
      MinecraftForge.EVENT_BUS.register(var0);
      boolean var1 = false;
      var1 = false;
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, @NotNull SoundEvent soundIn, @NotNull SoundCategory category, double x, double y, double z, float volume, float pitch) {
      Intrinsics.checkParameterIsNotNull(soundIn, "soundIn");
      Intrinsics.checkParameterIsNotNull(category, "category");
      IBlockUpdateListener.DefaultImpls.playSoundToAllNearExcept(this, player, soundIn, category, x, y, z, volume, pitch);
   }

   public void onEntityAdded(@NotNull Entity entityIn) {
      Intrinsics.checkParameterIsNotNull(entityIn, "entityIn");
      IBlockUpdateListener.DefaultImpls.onEntityAdded(this, entityIn);
   }

   public void broadcastSound(int soundID, @NotNull BlockPos pos, int data) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      IBlockUpdateListener.DefaultImpls.broadcastSound(this, soundID, pos, data);
   }

   public void playEvent(@Nullable EntityPlayer player, int type, @NotNull BlockPos blockPosIn, int data) {
      Intrinsics.checkParameterIsNotNull(blockPosIn, "blockPosIn");
      IBlockUpdateListener.DefaultImpls.playEvent(this, player, type, blockPosIn, data);
   }

   public void onEntityRemoved(@NotNull Entity entityIn) {
      Intrinsics.checkParameterIsNotNull(entityIn, "entityIn");
      IBlockUpdateListener.DefaultImpls.onEntityRemoved(this, entityIn);
   }

   public void notifyLightSet(@NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      IBlockUpdateListener.DefaultImpls.notifyLightSet(this, pos);
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, @NotNull int... parameters) {
      Intrinsics.checkParameterIsNotNull(parameters, "parameters");
      IBlockUpdateListener.DefaultImpls.spawnParticle(this, particleID, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NotNull int... parameters) {
      Intrinsics.checkParameterIsNotNull(parameters, "parameters");
      IBlockUpdateListener.DefaultImpls.spawnParticle(this, id, ignoreRange, minimiseParticleLevel, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
   }

   public void playRecord(@Nullable SoundEvent soundIn, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      IBlockUpdateListener.DefaultImpls.playRecord(this, soundIn, pos);
   }

   public void sendBlockBreakProgress(int breakerId, @NotNull BlockPos pos, int progress) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      IBlockUpdateListener.DefaultImpls.sendBlockBreakProgress(this, breakerId, pos, progress);
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      IBlockUpdateListener.DefaultImpls.markBlockRangeForRenderUpdate(this, x1, y1, z1, x2, y2, z2);
   }
}
