package shadows.placebo.interfaces;

import net.minecraft.item.EnumDyeColor;

public interface IFlowerEnum extends IPropertyEnum {
   boolean hasFlowers();

   EnumDyeColor getColor();
}
