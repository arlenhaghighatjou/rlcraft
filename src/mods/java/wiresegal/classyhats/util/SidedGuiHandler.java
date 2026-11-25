package wiresegal.classyhats.util;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.capability.CapabilityHatContainer;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.client.gui.GuiHatBag;
import wiresegal.classyhats.client.gui.GuiHatInventory;
import wiresegal.classyhats.container.ContainerHatBag;
import wiresegal.classyhats.container.ContainerHatInventory;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J<\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0006H\u0016J:\u0010\u000e\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0006H\u0016¨\u0006\u000f"},
   d2 = {"Lwiresegal/classyhats/util/SidedGuiHandler;", "Lnet/minecraftforge/fml/common/network/IGuiHandler;", "()V", "getClientGuiElement", "", "id", "", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "world", "Lnet/minecraft/world/World;", "x", "y", "z", "getServerGuiElement", "classyhats"}
)
public final class SidedGuiHandler implements IGuiHandler {
   public static final SidedGuiHandler INSTANCE;

   @Nullable
   public Object getClientGuiElement(int id, @NotNull EntityPlayer player, @Nullable World world, int x, int y, int z) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      switch(id) {
      case 0:
         return new GuiHatInventory(player);
      case 1:
         return new GuiHatBag(player);
      default:
         return null;
      }
   }

   @Nullable
   public Object getServerGuiElement(int id, @NotNull EntityPlayer player, @NotNull World world, int x, int y, int z) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(world, "world");
      switch(id) {
      case 0:
         InventoryPlayer var8 = player.inventory;
         Intrinsics.checkExpressionValueIsNotNull(player.inventory, "player.inventory");
         return new ContainerHatInventory(var8, player);
      case 1:
         IHatContainer capability = CapabilityHatContainer.Companion.getCapability(player);
         InventoryPlayer var10002 = player.inventory;
         Intrinsics.checkExpressionValueIsNotNull(player.inventory, "player.inventory");
         return new ContainerHatBag(var10002, player, capability.getCurrentHatSection());
      default:
         return null;
      }
   }

   private SidedGuiHandler() {
   }

   static {
      SidedGuiHandler var0 = new SidedGuiHandler();
      INSTANCE = var0;
   }
}
