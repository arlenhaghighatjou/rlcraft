package goblinbob.mobends.standard;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.IPreviewer;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.IMutatorFactory;
import goblinbob.mobends.standard.client.renderer.entity.mutated.PlayerRenderer;
import goblinbob.mobends.standard.data.PlayerData;
import goblinbob.mobends.standard.mutators.PlayerMutator;
import goblinbob.mobends.standard.previewer.PlayerPreviewer;
import net.minecraft.client.entity.AbstractClientPlayer;

public class PlayerBender extends EntityBender<AbstractClientPlayer> {
   private PlayerPreviewer previewer = new PlayerPreviewer();
   private String[] alterableParts = new String[]{"head", "body", "leftArm", "rightArm", "leftForeArm", "rightForeArm", "leftLeg", "rightLeg", "leftForeLeg", "rightForeLeg", "totalRotation", "leftItemRotation", "rightItemRotation"};

   public PlayerBender() {
      super("mobends", "player", "mobends.player", AbstractClientPlayer.class, new PlayerRenderer());
   }

   public String[] getAlterableParts() {
      return this.alterableParts;
   }

   public IEntityDataFactory<AbstractClientPlayer> getDataFactory() {
      return PlayerData::new;
   }

   public IMutatorFactory<AbstractClientPlayer> getMutatorFactory() {
      return PlayerMutator::new;
   }

   public IPreviewer<?> getPreviewer() {
      return this.previewer;
   }

   public LivingEntityData<?> getDataForPreview() {
      return PlayerPreviewer.getPreviewData();
   }
}
