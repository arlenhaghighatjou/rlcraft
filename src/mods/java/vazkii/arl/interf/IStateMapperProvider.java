package vazkii.arl.interf;

import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.property.PropertyString;

public interface IStateMapperProvider {
   PropertyString TEXTURE = new PropertyString("texture");

   @SideOnly(Side.CLIENT)
   IStateMapper getStateMapper();
}
