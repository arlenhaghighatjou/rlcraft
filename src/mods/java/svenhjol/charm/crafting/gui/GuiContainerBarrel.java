package svenhjol.charm.crafting.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import svenhjol.meson.MesonContainer;
import svenhjol.meson.MesonGuiContainer;
import vazkii.quark.api.IChestButtonCallback;
import vazkii.quark.api.IItemSearchBar;

@InterfaceList({@Interface(
   iface = "vazkii.quark.api.IChestButtonCallback",
   modid = "quark"
), @Interface(
   iface = "vazkii.quark.api.IItemSearchBar",
   modid = "quark"
)})
public class GuiContainerBarrel extends MesonGuiContainer implements IChestButtonCallback, IItemSearchBar {
   public GuiContainerBarrel(Container container, InventoryPlayer playerInv) {
      super("tile.charm:barrel.name", (MesonContainer)container, playerInv, new ResourceLocation("charm", "textures/gui/generic27.png"));
   }

   public boolean onAddChestButton(GuiButton button, int buttonType) {
      return true;
   }

   public void onSearchBarAdded(GuiTextField bar) {
   }
}
