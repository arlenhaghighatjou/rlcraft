package cursedflames.bountifulbaubles.client.layer;

import baubles.api.render.IRenderBauble.RenderType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IRenderObject {
   void onRenderObject(ItemStack var1, EntityPlayer var2, boolean var3, float var4, float var5);

   RenderType getRenderType();
}
