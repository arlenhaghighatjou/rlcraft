package xzeroair.trinkets.traits.abilities.interfaces;

public interface IToggleAbility extends IAbilityInterface {
   boolean abilityEnabled();

   IToggleAbility toggleAbility(boolean var1);

   IToggleAbility toggleAbility(int var1);
}
