package dev.satyrn.wolfarmor.common.network.packets;

import dev.satyrn.wolfarmor.common.network.MessageBase;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class WolfEatMessage extends MessageBase.ClientMessageBase<WolfEatMessage> {
   private int entityId;
   private ItemStack foodItem;

   public WolfEatMessage() {
   }

   public WolfEatMessage(int entityId, ItemStack foodItem) {
      this.entityId = entityId;
      this.foodItem = foodItem;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.entityId = buffer.readInt();
      this.foodItem = buffer.readItemStack();
   }

   protected void write(PacketBuffer buffer) {
      buffer.writeInt(this.entityId);
      buffer.writeItemStack(this.foodItem);
   }

   protected IMessage process(EntityPlayer player, Side side) {
      World world = player.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityId);
      if (entity != null) {
         Vec3d velocity = new Vec3d(((double)world.rand.nextFloat() - 0.5D) * 0.1D, (double)world.rand.nextFloat() * 0.1D + 0.1D, 0.0D);
         velocity = velocity.rotatePitch(-entity.rotationPitch * 0.017453292F);
         velocity = velocity.rotateYaw(-entity.rotationYaw * 0.017453292F);
         Vec3d position = new Vec3d((double)world.rand.nextFloat() - 0.5D, (double)(-world.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
         position = position.rotatePitch(-entity.rotationPitch * 0.017453292F);
         position = position.rotateYaw(-entity.rotationYaw * 0.017453292F);
         position = position.addVector(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
         if (this.foodItem.getHasSubtypes()) {
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, position.x, position.y, position.z, velocity.x, velocity.y + 0.05D, velocity.z, new int[]{Item.getIdFromItem(this.foodItem.getItem()), this.foodItem.getMetadata()});
         } else {
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, position.x, position.y, position.z, velocity.x, velocity.y + 0.05D, velocity.z, new int[]{Item.getIdFromItem(this.foodItem.getItem())});
         }
      }

      return null;
   }
}
