package org.jwaresoftware.mcmods.armorunder.runtime;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.api.mod.IModInventoryRenderingHelper;

public class ModRuntimeSP extends ModRuntimeSkeleton {
   private final Minecraft MINECRAFT = Minecraft.getMinecraft();
   private IModInventoryRenderingHelper _inventoryModelRegistrar = new ModInventoryModelInit();

   public ModRuntimeSP() {
   }

   protected String getId() {
      return "SP";
   }

   public boolean hasUI() {
      return true;
   }

   public EntityPlayer getClientPlayer() {
      return this.MINECRAFT.player;
   }

   public World getClientWorld() {
      return this.MINECRAFT.world;
   }

   public IThreadListener getThreadListener(MessageContext context) {
      return (IThreadListener)(context.side.isClient() ? this.MINECRAFT : context.getServerHandler().player.mcServer);
   }

   public EntityPlayer getPlayer(MessageContext context) {
      return (EntityPlayer)(context.side.isClient() ? this.MINECRAFT.player : context.getServerHandler().player);
   }

   protected IModInventoryRenderingHelper getRenderingHelper() {
      return this._inventoryModelRegistrar;
   }

   protected void makeModItems() {
      boolean reset_tab = this.getConfig().useModTab();
      if (reset_tab) {
         this.setupModTab();
      }

      super.makeModItems();
      if (reset_tab) {
         this.resetItemsTab();
      }

   }

   private void setupModTab() {
      final Item tabIcon = ModItems.icon;
      CreativeTabs tab = new CreativeTabs("armorunder") {
         @SideOnly(Side.CLIENT)
         public ItemStack getTabIconItem() {
            return new ItemStack(tabIcon);
         }
      };
      this.getConfig().setTab(tab);
   }

   protected void installModHandlers() {
      super.installModHandlers();
      (new LinedArmorTooltip()).register(MinecraftForge.EVENT_BUS);
      if (this.getConfig().includeSpecialLiners()) {
         (new CustomLinedArmorTooltip()).register(MinecraftForge.EVENT_BUS);
      }

   }

   protected void registerRenderModels() {
      ModItems.instances.forEach((item) -> {
         this.doRenderSetupOrFail(item.oid(), item);
      });
   }

   private void resetItemsTab() {
      CreativeTabs tab = this.getOwnedTab();
      ModItems.instances.forEach((item) -> {
         item.setCreativeTab(tab);
      });
   }
}
