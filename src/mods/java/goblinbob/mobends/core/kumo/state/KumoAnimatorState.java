package goblinbob.mobends.core.kumo.state;

import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.core.kumo.state.template.LayerTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import java.util.ArrayList;
import java.util.List;

public class KumoAnimatorState<D extends EntityData<?>> {
   private List<ILayerState> layerStates = new ArrayList();
   private KumoContext context = new KumoContext();
   private boolean started = false;

   public KumoAnimatorState(AnimatorTemplate animatorTemplate, IKumoInstancingContext dataProvider) throws MalformedKumoTemplateException {
      if (animatorTemplate.layers == null) {
         throw new MalformedKumoTemplateException("No layers were specified");
      } else {
         for(LayerTemplate template : animatorTemplate.layers) {
            this.layerStates.add(ILayerState.createFromTemplate(dataProvider, template));
         }

      }
   }

   public void update(D entityData, float deltaTime) throws MalformedKumoTemplateException {
      this.context.entityData = entityData;

      for(ILayerState layer : this.layerStates) {
         this.context.layerState = layer;
         if (!this.started) {
            layer.start(this.context);
         }

         layer.update(this.context, deltaTime);
      }

      this.started = true;
   }
}
