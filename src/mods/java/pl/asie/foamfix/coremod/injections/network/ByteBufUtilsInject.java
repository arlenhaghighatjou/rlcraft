package pl.asie.foamfix.coremod.injections.network;

import com.google.common.base.Utf8;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.nio.charset.StandardCharsets;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.Validate;

public class ByteBufUtilsInject {
   public ByteBufUtilsInject() {
   }

   public static void writeUTF8String(ByteBuf to, String string) {
      int length = Utf8.encodedLength(string);
      Validate.isTrue(ByteBufUtils.varIntByteCount(length) < 3, "The string is too long for this encoding.", new Object[0]);
      ByteBufUtils.writeVarInt(to, length, 2);
      to.writeCharSequence(string, StandardCharsets.UTF_8);
   }
}
