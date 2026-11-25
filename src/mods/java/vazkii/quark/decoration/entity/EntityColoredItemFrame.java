package vazkii.quark.decoration.entity;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.decoration.feature.ColoredItemFrames;

public class EntityColoredItemFrame extends EntityFlatItemFrame {
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityColoredItemFrame.class, DataSerializers.VARINT);
   private static final String TAG_COLOR = "DyeColor";

   public EntityColoredItemFrame(World worldIn) {
      super(worldIn);
   }

   public EntityColoredItemFrame(World worldIn, BlockPos blockPos, EnumFacing face, int color) {
      super(worldIn, blockPos, face);
      this.dataManager.set(COLOR, Integer.valueOf(color));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(COLOR, Integer.valueOf(0));
   }

   public int getColor() {
      return ((Integer)this.dataManager.get(COLOR)).intValue();
   }

   protected void dropFrame() {
      this.entityDropItem(new ItemStack(ColoredItemFrames.colored_item_frame, 1, this.getColor()), 0.0F);
   }

   @Nonnull
   public ItemStack getPickedResult(RayTraceResult target) {
      ItemStack held = this.getDisplayedItem();
      return held.isEmpty() ? new ItemStack(ColoredItemFrames.colored_item_frame, 1, this.getColor()) : held.copy();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("DyeColor", this.getColor());
      super.writeEntityToNBT(compound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.dataManager.set(COLOR, Integer.valueOf(compound.getInteger("DyeColor")));
      super.readEntityFromNBT(compound);
   }
}
