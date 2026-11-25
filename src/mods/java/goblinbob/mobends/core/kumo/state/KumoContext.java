package goblinbob.mobends.core.kumo.state;

import goblinbob.mobends.core.data.EntityData;

public class KumoContext implements IKumoContext {
   public EntityData<?> entityData;
   public ILayerState layerState;
   public INodeState currentNode;

   public KumoContext() {
   }

   public EntityData<?> getEntityData() {
      return this.entityData;
   }

   public ILayerState getLayerState() {
      return this.layerState;
   }

   public INodeState getCurrentNode() {
      return this.currentNode;
   }

   public void setCurrentNode(INodeState node) {
      this.currentNode = node;
   }
}
