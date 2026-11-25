package svenhjol.meson.iface;

import java.util.List;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.meson.registry.ProxyRegistry;

public interface IMesonItem {
   default void register(String name) {
      Item item = (Item)this;
      ResourceLocation res = new ResourceLocation(this.getModId() + ":" + name);
      item.setUnlocalizedName(res.toString());
      item.setRegistryName(res);
      ProxyRegistry.register(item);
      ProxyRegistry.items.add(item);
   }

   String getModId();

   public interface IItemColorHandler {
      @SideOnly(Side.CLIENT)
      IItemColor getItemColor();
   }

   public interface IItemCustomModel {
      void registerModels(Item var1);
   }

   public interface IItemVariants {
      List<ResourceLocation> getVariants();
   }
}
