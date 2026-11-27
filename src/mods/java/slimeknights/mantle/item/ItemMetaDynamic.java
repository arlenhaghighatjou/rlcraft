package slimeknights.mantle.item;

import net.lax1dude.eaglercraft.v1_8.HString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.util.LocUtils;

public class ItemMetaDynamic extends ItemTooltip {
   private static int MAX = 131071;
   protected boolean[] availabilityMask = new boolean[1];
   protected Int2ObjectOpenHashMap<String> names = new Int2ObjectOpenHashMap();

   public ItemMetaDynamic() {
      this.setHasSubtypes(true);
   }

   public ItemStack addMeta(int meta, String name) {
      if (meta > MAX) {
         throw new IllegalArgumentException(HString.format("Metadata for %s too high, highest supported value is %d. Meta was %d", name, MAX, meta));
      } else if (this.isValid(meta)) {
         throw new IllegalArgumentException(HString.format("Metadata for %s is already taken. Meta %d is %s", name, meta, this.names.get(meta)));
      } else {
         while(meta >= this.availabilityMask.length) {
            this.availabilityMask = Arrays.copyOf(this.availabilityMask, this.availabilityMask.length * 2);
         }

         this.setValid(meta);
         this.names.put(meta, name);
         return new ItemStack(this, 1, meta);
      }
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack stack) {
      int meta = stack.getMetadata();
      return this.isValid(meta) ? super.getUnlocalizedName(stack) + "." + LocUtils.makeLocString((String)this.names.get(meta)) : super.getUnlocalizedName(stack);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i <= this.availabilityMask.length; ++i) {
            if (this.isValid(i)) {
               subItems.add(new ItemStack(this, 1, i));
            }
         }
      }

   }

   public int getMetadata(int damage) {
      int meta = super.getMetadata(damage);
      return this.isValid(meta) ? meta : 0;
   }

   protected void setValid(int meta) {
      this.availabilityMask[meta] = true;
   }

   protected boolean isValid(int meta) {
      return meta <= MAX && meta < this.availabilityMask.length ? this.availabilityMask[meta] : false;
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModels() {
      this.registerItemModels(this);
   }

   @SideOnly(Side.CLIENT)
   protected void registerItemModels(final Item item) {
      String resourceId = Loader.instance().activeModContainer().getModId().toLowerCase();
      for(Int2ObjectMap.Entry<String> entry : this.names.int2ObjectEntrySet()) {
         ModelLoader.setCustomModelResourceLocation(item, entry.getIntKey(), new ModelResourceLocation(item.getRegistryName(), (String)entry.getValue()));
      }
   }
}
