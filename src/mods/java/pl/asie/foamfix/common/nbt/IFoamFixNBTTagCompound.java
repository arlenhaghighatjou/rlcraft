package pl.asie.foamfix.common.nbt;

import java.util.Map;
import net.minecraft.nbt.NBTBase;

public interface IFoamFixNBTTagCompound {
   Map<String, NBTBase> foamfix_getTagMap();
}
