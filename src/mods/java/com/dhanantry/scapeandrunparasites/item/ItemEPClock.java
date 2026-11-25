package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemEPClock extends ItemBase {
   public ItemEPClock(String name, int maxStack, byte v) {
      super(name, maxStack, v);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (!worldIn.isRemote && SRPConfigSystems.useEvolution) {
         SRPWorldData data = SRPWorldData.get(worldIn);
         playerIn.sendStatusMessage(new TextComponentString("" + data.getEvolutionPhase()), true);
      }

      return super.onItemRightClick(worldIn, playerIn, handIn);
   }
}
