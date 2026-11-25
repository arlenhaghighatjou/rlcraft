package wiresegal.classyhats.event;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.client.keybind.KeyBindHandler;
import wiresegal.classyhats.network.PacketHatGuiOpen;

@EventBusSubscriber(
   modid = "classyhats"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u0010\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\fH\u0007R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\r"},
   d2 = {"Lwiresegal/classyhats/event/GuiEvents;", "", "()V", "SLOT_HAT", "Lnet/minecraft/util/ResourceLocation;", "getSLOT_HAT", "()Lnet/minecraft/util/ResourceLocation;", "onInputKey", "", "event", "Lnet/minecraftforge/fml/common/gameevent/InputEvent$KeyInputEvent;", "onTextureStitchPre", "Lnet/minecraftforge/client/event/TextureStitchEvent$Pre;", "classyhats"}
)
public final class GuiEvents {
   @NotNull
   private static final ResourceLocation SLOT_HAT = new ResourceLocation("classyhats", "gui/slot_hat");
   public static final GuiEvents INSTANCE;

   @NotNull
   public final ResourceLocation getSLOT_HAT() {
      return SLOT_HAT;
   }

   @SubscribeEvent
   @JvmStatic
   @SideOnly(Side.CLIENT)
   public static final void onTextureStitchPre(@NotNull Pre event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      event.getMap().registerSprite(SLOT_HAT);
   }

   @SubscribeEvent
   @JvmStatic
   @SideOnly(Side.CLIENT)
   public static final void onInputKey(@NotNull KeyInputEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == null && KeyBindHandler.INSTANCE.getKEY().isPressed()) {
         PacketHandler.NETWORK.sendToServer((IMessage)(new PacketHatGuiOpen(0)));
      }

   }

   private GuiEvents() {
   }

   static {
      GuiEvents var0 = new GuiEvents();
      INSTANCE = var0;
   }
}
