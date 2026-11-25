package cursedflames.bountifulbaubles.item.throwable;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.entity.EntityGrenade;
import cursedflames.bountifulbaubles.entity.EntityTerrariaThrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemGrenade extends ItemTerrariaThrowable {
   public ItemGrenade(String name) {
      super(name, BountifulBaubles.TAB, 15);
   }

   public EntityTerrariaThrowable getThrownEntity(World world, EntityPlayer player) {
      return new EntityGrenade(world, player);
   }
}
