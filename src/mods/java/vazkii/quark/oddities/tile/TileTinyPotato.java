package vazkii.quark.oddities.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.network.message.MessageSpamlessChat;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.oddities.feature.TinyPotato;

public class TileTinyPotato extends TileSimpleInventory implements ITickable {
   private static final String TAG_NAME = "name";
   private static final String TAG_ANGERY = "angery";
   private static final int TINY_POTATO_CHAT = "tiny potate".hashCode();
   public int jumpTicks = 0;
   public String name = "";
   public int nextDoIt = 0;
   public boolean angery = false;

   public TileTinyPotato() {
   }

   public void interact(EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side) {
      int index = side.getIndex();
      if (index >= 0) {
         ItemStack stackAt = this.getStackInSlot(index);
         if (!stackAt.isEmpty() && stack.isEmpty()) {
            player.setHeldItem(hand, stackAt);
            this.setInventorySlotContents(index, ItemStack.EMPTY);
         } else if (!stack.isEmpty()) {
            ItemStack copy = stack.splitStack(1);
            if (stack.isEmpty()) {
               player.setHeldItem(hand, stackAt);
            } else if (!stackAt.isEmpty()) {
               ItemHandlerHelper.giveItemToPlayer(player, stackAt);
            }

            this.setInventorySlotContents(index, copy);
         }
      }

      this.jump();
      if (!this.world.isRemote) {
         if (this.name.toLowerCase().trim().endsWith("shia labeouf") && this.nextDoIt == 0) {
            this.nextDoIt = 40;
            this.world.playSound((EntityPlayer)null, this.pos, QuarkSounds.BLOCK_TINY_POTATO_SHIA_LABEOUF, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         for(int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack stackAt = this.getStackInSlot(i);
            if (!stackAt.isEmpty() && stackAt.getItem() == Item.getItemFromBlock(TinyPotato.tiny_potato)) {
               MessageSpamlessChat.sendToPlayer(player, TINY_POTATO_CHAT, new TextComponentTranslation("quarkmisc.me_or_my_son", new Object[0]));
               return;
            }
         }
      }

   }

   public void jump() {
      if (this.jumpTicks == 0) {
         this.jumpTicks = 20;
      }

   }

   public void update() {
      if (this.world.rand.nextInt(100) == 0) {
         this.jump();
      }

      if (this.jumpTicks > 0) {
         --this.jumpTicks;
      }

      if (this.nextDoIt > 0) {
         --this.nextDoIt;
      }

   }

   public void markDirty() {
      super.markDirty();
      this.sync();
   }

   public void writeSharedNBT(NBTTagCompound compound) {
      super.writeSharedNBT(compound);
      compound.setString("name", this.name);
      compound.setBoolean("angery", this.angery);
   }

   public void readSharedNBT(NBTTagCompound compound) {
      super.readSharedNBT(compound);
      this.name = compound.getString("name");
      this.angery = compound.getBoolean("angery");
   }

   public int getSizeInventory() {
      return 6;
   }
}
