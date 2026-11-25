package reborncore.client.containerBuilder;

import net.minecraft.entity.player.EntityPlayer;
import reborncore.client.containerBuilder.builder.BuiltContainer;

public interface IRightClickHandler {
   boolean handleRightClick(int var1, EntityPlayer var2, BuiltContainer var3);
}
