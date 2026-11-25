package xzeroair.trinkets.blocks.tileentities;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.blocks.BlockTeddyBear;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.TileEntityCap.TileEntityProperties;

public class TileEntityTeddyBear extends TileEntity implements ITickable {
   protected int facingRotation;
   protected ItemStack stack = ItemStack.EMPTY;

   public TileEntityTeddyBear() {
   }

   public void update() {
   }

   public void setTeddyBear(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         this.stack = stack;
         Capabilities.getTEProperties(this, (prop) -> {
            NBTTagCompound tag = prop.getTag();
            NBTTagCompound stackTag = new NBTTagCompound();
            stack.writeToNBT(stackTag);
            tag.setTag("TeddyBear", stackTag);
         });
      }

   }

   public ItemStack getTeddyBear() {
      return (ItemStack)Capabilities.getTEProperties(this, this.stack, (prop, rtn) -> {
         if (rtn.isEmpty()) {
            NBTTagCompound tag = prop.getTag();
            if (tag.hasKey("TeddyBear")) {
               NBTTagCompound stackTag = tag.getCompoundTag("TeddyBear");
               ItemStack stack = new ItemStack(stackTag);
               return stack;
            }
         }

         return rtn;
      });
   }

   public TileEntityProperties getProperties() {
      return Capabilities.getTEProperties(this);
   }

   @SideOnly(Side.CLIENT)
   public int getRotation() {
      return this.facingRotation;
   }

   public void setRotation(int rotation) {
      this.facingRotation = rotation;
   }

   public void mirror(Mirror mirrorIn) {
      if (this.world != null && this.world.getBlockState(this.getPos()).getValue(BlockTeddyBear.FACING) == EnumFacing.UP) {
         this.facingRotation = mirrorIn.mirrorRotation(this.facingRotation, 16);
      }

   }

   public void rotate(Rotation rotationIn) {
      if (this.world != null && this.world.getBlockState(this.getPos()).getValue(BlockTeddyBear.FACING) == EnumFacing.UP) {
         this.facingRotation = rotationIn.rotate(this.facingRotation, 16);
      }

   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 4, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setByte("Rot", (byte)(this.facingRotation & 255));
      if (!this.stack.isEmpty()) {
         NBTTagCompound stackTag = new NBTTagCompound();
         this.stack.writeToNBT(stackTag);
         compound.setTag("TeddyBear", stackTag);
      }

      return compound;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.facingRotation = compound.getByte("Rot");
      if (compound.hasKey("TeddyBear")) {
         NBTTagCompound stackTag = compound.getCompoundTag("TeddyBear");
         this.stack = new ItemStack(stackTag);
      }

   }
}
