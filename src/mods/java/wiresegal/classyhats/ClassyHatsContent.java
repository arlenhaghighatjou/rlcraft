package wiresegal.classyhats;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.block.BlockHatStand;
import wiresegal.classyhats.item.ItemHat;
import wiresegal.classyhats.item.ItemPhantomThread;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"},
   d2 = {"Lwiresegal/classyhats/ClassyHatsContent;", "", "()V", "HAT", "Lwiresegal/classyhats/item/ItemHat;", "getHAT", "()Lwiresegal/classyhats/item/ItemHat;", "HAT_STAND", "Lwiresegal/classyhats/block/BlockHatStand;", "getHAT_STAND", "()Lwiresegal/classyhats/block/BlockHatStand;", "PHANTOM_THREAD", "Lwiresegal/classyhats/item/ItemPhantomThread;", "getPHANTOM_THREAD", "()Lwiresegal/classyhats/item/ItemPhantomThread;", "classyhats"}
)
public final class ClassyHatsContent {
   @NotNull
   private static final ItemHat HAT = new ItemHat();
   @NotNull
   private static final ItemPhantomThread PHANTOM_THREAD = new ItemPhantomThread();
   @NotNull
   private static final BlockHatStand HAT_STAND = new BlockHatStand();
   public static final ClassyHatsContent INSTANCE;

   @NotNull
   public final ItemHat getHAT() {
      return HAT;
   }

   @NotNull
   public final ItemPhantomThread getPHANTOM_THREAD() {
      return PHANTOM_THREAD;
   }

   @NotNull
   public final BlockHatStand getHAT_STAND() {
      return HAT_STAND;
   }

   private ClassyHatsContent() {
   }

   static {
      ClassyHatsContent var0 = new ClassyHatsContent();
      INSTANCE = var0;
   }
}
