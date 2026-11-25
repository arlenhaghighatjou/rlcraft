package wiresegal.classyhats.client.keybind;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import kotlin.Metadata;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"},
   d2 = {"Lwiresegal/classyhats/client/keybind/KeyBindHandler;", "", "()V", "KEY", "Lnet/minecraft/client/settings/KeyBinding;", "getKEY", "()Lnet/minecraft/client/settings/KeyBinding;", "classyhats"}
)
public final class KeyBindHandler {
   @NotNull
   private static final KeyBinding KEY = new KeyBinding(LibrarianLib.INSTANCE.getPROXY().translate("keybind.classyhats.hats_inventory", new Object[0]), 35, "key.categories.inventory");
   public static final KeyBindHandler INSTANCE;

   @NotNull
   public final KeyBinding getKEY() {
      return KEY;
   }

   private KeyBindHandler() {
   }

   static {
      KeyBindHandler var0 = new KeyBindHandler();
      INSTANCE = var0;
   }
}
