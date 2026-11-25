package org.orecruncher.dsurround.client.footsteps;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.lib.MCHelper;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class Association {
   private final IBlockState state;
   private final FootStrikeLocation location;
   private final ObjectArray<IAcoustic> data = new ObjectArray(8);
   private final boolean isNotEmitter;

   public Association(@Nonnull EntityLivingBase entity, @Nonnull IAcoustic[] association) {
      Vec3d vec = entity.getPositionVector();
      this.state = null;
      this.location = new FootStrikeLocation(entity, vec.x, vec.y + 1.0D, vec.z);
      this.data.addAll(association);
      this.isNotEmitter = association == AcousticRegistry.NOT_EMITTER;
   }

   public Association(@Nonnull IBlockState state, @Nonnull FootStrikeLocation pos, @Nonnull IAcoustic[] association) {
      this.state = state;
      this.location = pos;
      this.data.addAll(association);
      this.isNotEmitter = association == AcousticRegistry.NOT_EMITTER;
   }

   @Nonnull
   public IAcoustic[] getData() {
      return (IAcoustic[])this.data.toArray(new IAcoustic[0]);
   }

   public boolean isLiquid() {
      return this.state != null && this.state.getMaterial().isLiquid();
   }

   public SoundType getSoundType() {
      return this.state != null ? MCHelper.getSoundType(this.state) : null;
   }

   public void add(@Nonnull IAcoustic acoustics) {
      this.data.add(acoustics);
   }

   public void add(@Nonnull IAcoustic[] acoustics) {
      this.data.addAll(acoustics);
   }

   public void add(@Nonnull Collection<? extends IAcoustic> collection) {
      this.data.addAll(collection);
   }

   @Nonnull
   public FootStrikeLocation getStrikeLocation() {
      return this.location;
   }

   public boolean hasStrikeLocation() {
      return this.location != null;
   }

   @Nullable
   public BlockPos getStepPos() {
      return this.location != null ? this.location.getStepPos() : null;
   }

   public boolean isNotEmitter() {
      return this.isNotEmitter;
   }
}
