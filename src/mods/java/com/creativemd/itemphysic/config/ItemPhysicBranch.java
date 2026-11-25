package com.creativemd.itemphysic.config;

import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.BooleanSegment;
import com.creativemd.igcm.api.segments.FloatSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.creativemd.igcm.api.sorting.ConfigBranchSorting;
import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.physics.ServerPhysic;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class ItemPhysicBranch extends ConfigBranch {
   public ItemPhysicBranch(String name) {
      super(name, new ItemStack(Items.FEATHER));
   }

   public void createChildren() {
      this.registerElement("despawn", new IntegerSegment("despawn time", Integer.valueOf(6000)));
      this.registerElement("pickup", new BooleanSegment("custom pickup", false));
      this.registerElement("pickupWhenSneaking", new BooleanSegment("pickup when sneaking", true));
      this.registerElement("throw", new BooleanSegment("custom throw", true));
      this.registerElement("igniting", new BooleanSegment("enable igniting", true));
      this.registerElement("maximumPickupRange", new FloatSegment("maximum pickup range", 5.0F, 0.1F, 100.0F));
      this.registerElement("pickupMinedImmediately", new BooleanSegment("pickup mined immediately", false));
      this.registerElement("respectRangeWhenMined", new BooleanSegment("respect range when mined", false));
      this.registerElement("swiming", new ConfigBranchSorting("Swimming Items", new ItemStack(Items.BOAT), ServerPhysic.swimmingItems));
      this.registerElement("burning", new ConfigBranchSorting("Burning Items", new ItemStack(Items.FIRE_CHARGE), ServerPhysic.burningItems));
      this.registerElement("undestroyable", new ConfigBranchSorting("Undestroyable Items", new ItemStack(Blocks.BEDROCK), ServerPhysic.undestroyableItems));
      this.registerElement("igniting", new ConfigBranchSorting("Igniting Items", new ItemStack(Blocks.TORCH), ServerPhysic.ignitingItems));
   }

   public boolean requiresSynchronization() {
      return true;
   }

   public void onRecieveFrom(Side side) {
      ItemDummyContainer.despawnItem = ((Integer)this.getValue("despawn")).intValue();
      ItemDummyContainer.customPickup = ((Boolean)this.getValue("pickup")).booleanValue();
      ItemDummyContainer.pickupWhenSneaking = ((Boolean)this.getValue("pickupWhenSneaking")).booleanValue();
      ItemDummyContainer.customThrow = ((Boolean)this.getValue("throw")).booleanValue();
      ItemDummyContainer.enableIgniting = ((Boolean)this.getValue("igniting")).booleanValue();
      ItemDummyContainer.maximumPickupRange = ((Float)this.getValue("maximumPickupRange")).floatValue();
      ItemDummyContainer.pickupMinedImmediately = ((Boolean)this.getValue("pickupMinedImmediately")).booleanValue();
      ItemDummyContainer.respectRangeWhenMined = ((Boolean)this.getValue("respectRangeWhenMined")).booleanValue();
   }

   public void loadExtra(NBTTagCompound nbt) {
   }

   public void saveExtra(NBTTagCompound nbt) {
   }
}
