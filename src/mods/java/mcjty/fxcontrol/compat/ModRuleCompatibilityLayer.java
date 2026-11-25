package mcjty.fxcontrol.compat;

import mcjty.fxcontrol.setup.ModSetup;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.IModRuleCompatibilityLayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ModRuleCompatibilityLayer implements IModRuleCompatibilityLayer {
   public ModRuleCompatibilityLayer() {
   }

   public boolean hasBaubles() {
      return ModSetup.baubles;
   }

   public int[] getAmuletSlots() {
      return BaublesSupport.getAmuletSlots();
   }

   public int[] getBeltSlots() {
      return BaublesSupport.getBeltSlots();
   }

   public int[] getBodySlots() {
      return BaublesSupport.getBodySlots();
   }

   public int[] getCharmSlots() {
      return BaublesSupport.getCharmSlots();
   }

   public int[] getHeadSlots() {
      return BaublesSupport.getHeadSlots();
   }

   public int[] getRingSlots() {
      return BaublesSupport.getRingSlots();
   }

   public int[] getTrinketSlots() {
      return BaublesSupport.getTrinketSlots();
   }

   public ItemStack getBaubleStack(EntityPlayer player, int slot) {
      return BaublesSupport.getStack(player, slot);
   }

   public boolean hasGameStages() {
      return ModSetup.gamestages;
   }

   public boolean hasGameStage(EntityPlayer player, String stage) {
      return GameStageSupport.hasGameStage(player, stage);
   }

   public void addGameStage(EntityPlayer player, String stage) {
      GameStageSupport.addGameStage(player, stage);
   }

   public void removeGameStage(EntityPlayer player, String stage) {
      GameStageSupport.removeGameStage(player, stage);
   }

   public boolean hasLostCities() {
      return ModSetup.lostcities;
   }

   public <T> boolean isCity(IEventQuery<T> query, T event) {
      return LostCitySupport.isCity(query, event);
   }

   public <T> boolean isStreet(IEventQuery<T> query, T event) {
      return LostCitySupport.isStreet(query, event);
   }

   public <T> boolean inSphere(IEventQuery<T> query, T event) {
      return LostCitySupport.inSphere(query, event);
   }

   public <T> boolean isBuilding(IEventQuery<T> query, T event) {
      return LostCitySupport.isBuilding(query, event);
   }

   public boolean hasSereneSeasons() {
      return ModSetup.sereneSeasons;
   }

   public boolean isSpring(World world) {
      return SereneSeasonsSupport.isSpring(world);
   }

   public boolean isSummer(World world) {
      return SereneSeasonsSupport.isSummer(world);
   }

   public boolean isWinter(World world) {
      return SereneSeasonsSupport.isWinter(world);
   }

   public boolean isAutumn(World world) {
      return SereneSeasonsSupport.isAutumn(world);
   }

   public boolean hasEnigmaScript() {
      return ModSetup.enigma;
   }

   public String getPlayerState(EntityPlayer player, String statename) {
      return EnigmaSupport.getPlayerState(player, statename);
   }

   public String getState(World world, String statename) {
      return EnigmaSupport.getState(world, statename);
   }

   public void setPlayerState(EntityPlayer player, String statename, String statevalue) {
      EnigmaSupport.setPlayerState(player, statename, statevalue);
   }

   public void setState(World world, String statename, String statevalue) {
      EnigmaSupport.setState(world, statename, statevalue);
   }

   public String getBiomeName(Biome biome) {
      return biome.biomeName;
   }
}
