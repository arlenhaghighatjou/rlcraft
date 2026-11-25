package vazkii.quark.base.item;

import vazkii.arl.interf.IVariantHolder;

public interface IQuarkItem extends IVariantHolder {
   default String getModNamespace() {
      return "quark";
   }
}
