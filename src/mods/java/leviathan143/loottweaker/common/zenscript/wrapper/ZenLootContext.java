package leviathan143.loottweaker.common.zenscript.wrapper;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.storage.loot.LootContext;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("loottweaker.LootContext")
public class ZenLootContext {
   private final LootContext delegate;

   public ZenLootContext(LootContext delegate) {
      this.delegate = delegate;
   }

   @ZenMethod
   public IEntity lootedEntity() {
      return CraftTweakerMC.getIEntity(this.delegate.getLootedEntity());
   }

   @ZenMethod
   public IPlayer killerPlayer() {
      return CraftTweakerMC.getIPlayer((EntityPlayer)this.delegate.getKillerPlayer());
   }

   @ZenMethod
   public IEntity killer() {
      return CraftTweakerMC.getIEntity(this.delegate.getKiller());
   }

   @ZenMethod
   public float luck() {
      return this.delegate.getLuck();
   }

   @ZenMethod
   public IWorld world() {
      return CraftTweakerMC.getIWorld(this.delegate.getWorld());
   }

   @ZenMethod
   public int lootingModifier() {
      return this.delegate.getLootingModifier();
   }
}
