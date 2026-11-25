package reborncore.client.containerBuilder;

import net.minecraft.entity.player.EntityPlayer;
import reborncore.client.containerBuilder.builder.BuiltContainer;

public interface IContainerProvider {
   BuiltContainer createContainer(EntityPlayer var1);
}
