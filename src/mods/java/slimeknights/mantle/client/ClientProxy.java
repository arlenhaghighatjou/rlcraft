package slimeknights.mantle.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.common.CommonProxy;

public class ClientProxy extends CommonProxy {
   private BookLoader bookLoader;

   public ClientProxy() {
   }

   public void preInit() {
      this.bookLoader = new BookLoader();
   }

   public void init() {
      this.bookLoader.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
   }

   public void postInit() {
      ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this.bookLoader);
      MinecraftForge.EVENT_BUS.register(new ExtraHeartRenderHandler());
   }
}
