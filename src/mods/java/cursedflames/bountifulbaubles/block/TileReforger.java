package cursedflames.bountifulbaubles.block;

import baubles.api.cap.BaublesCapabilities;
import cursedflames.bountifulbaubles.ModConfig;
import cursedflames.bountifulbaubles.baubleeffect.EnumBaubleModifier;
import cursedflames.bountifulbaubles.util.XpUtil;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileReforger extends GenericTileEntity {
   private ItemStackHandler stackHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         TileReforger.this.markDirty();
         ItemStack stack = this.getStackInSlot(slot);
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }

         NBTTagCompound tag = stack.getTagCompound();
         if (!tag.hasKey("baubleModifier")) {
            EnumBaubleModifier.generateModifier(stack);
         }

         if (!tag.hasKey("reforgeCost")) {
            tag.setInteger("reforgeCost", TileReforger.this.getReforgeCost(TileReforger.this.world.rand));
         }

      }
   };

   public TileReforger() {
   }

   public NBTTagCompound writeDataToNBT(NBTTagCompound tag) {
      tag.setTag("items", this.stackHandler.serializeNBT());
      return tag;
   }

   public void readDataFromNBT(NBTTagCompound tag) {
      if (tag.hasKey("items")) {
         this.stackHandler.deserializeNBT((NBTTagCompound)tag.getTag("items"));
      }

   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler) : super.getCapability(capability, facing));
   }

   public NBTTagCompound getBlockBreakNBT() {
      return null;
   }

   public void loadBlockPlaceNBT(NBTTagCompound tag) {
   }

   public void tryReforge(EntityPlayer player) {
      ItemStack stack = this.stackHandler.getStackInSlot(0);
      if (!stack.isEmpty() && stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
         int xp = XpUtil.getPlayerXP(player);
         NBTTagCompound tag = stack.getTagCompound();
         int xpCost = -1;
         if (tag.hasKey("reforgeCost")) {
            xpCost = tag.getInteger("reforgeCost");
         }

         boolean creative = player.isCreative();
         if ((xp >= xpCost || creative) && xpCost != -1) {
            if (!creative) {
               XpUtil.addPlayerXP(player, -xpCost);
            }

            EnumBaubleModifier.generateModifier(stack);
            tag.setInteger("reforgeCost", this.getReforgeCost(this.world.rand));
            float vol = (float)(Math.random() * 0.3D + 0.9D);
            float pitch = (float)(Math.random() * 0.3D + 0.85D);
            this.world.playSound((EntityPlayer)null, this.pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, vol, pitch);
         }

      }
   }

   private int getReforgeCost(Random rand) {
      int min = ModConfig.reforgeCostMin.getInt(80);
      int max = ModConfig.reforgeCostMax.getInt(320);
      if (min > max) {
         int min_ = min;
         min = max;
         max = min_;
      }

      return this.world.rand.nextInt(max - min + 1) + min;
   }
}
