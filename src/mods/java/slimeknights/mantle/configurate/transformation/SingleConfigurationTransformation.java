package slimeknights.mantle.configurate.transformation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import slimeknights.mantle.configurate.ConfigurationNode;

class SingleConfigurationTransformation extends ConfigurationTransformation {
   private final MoveStrategy strategy;
   private final Map<Object[], TransformAction> actions;
   private final ThreadLocal<ConfigurationTransformation.NodePath> sharedPath = new ThreadLocal<ConfigurationTransformation.NodePath>() {
      protected ConfigurationTransformation.NodePath initialValue() {
         return new ConfigurationTransformation.NodePath();
      }
   };

   protected SingleConfigurationTransformation(Map<Object[], TransformAction> actions, MoveStrategy strategy) {
      this.actions = actions;
      this.strategy = strategy;
   }

   public static ConfigurationTransformation.Builder builder() {
      return new ConfigurationTransformation.Builder();
   }

   public void apply(ConfigurationNode node) {
      for(Entry<Object[], TransformAction> ent : this.actions.entrySet()) {
         this.applySingleAction(node, ent.getKey(), 0, node, (TransformAction)ent.getValue());
      }

   }

   protected void applySingleAction(ConfigurationNode start, Object[] path, int startIdx, ConfigurationNode node, TransformAction action) {
      for(int i = startIdx; i < path.length; ++i) {
         if (path[i] == WILDCARD_OBJECT) {
            if (node.hasListChildren()) {
               List<? extends ConfigurationNode> children = node.getChildrenList();

               for(int cI = 0; cI < children.size(); ++cI) {
                  path[i] = cI;
                  this.applySingleAction(start, path, i + 1, (ConfigurationNode)children.get(cI), action);
               }

               path[i] = WILDCARD_OBJECT;
            } else {
               if (!node.hasMapChildren()) {
                  return;
               }

               for(Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
                  path[i] = ent.getKey();
                  this.applySingleAction(start, path, i + 1, (ConfigurationNode)ent.getValue(), action);
               }

               path[i] = WILDCARD_OBJECT;
            }

            return;
         }

         node = node.getNode(path[i]);
         if (node.isVirtual()) {
            return;
         }
      }

      ConfigurationTransformation.NodePath immutablePath = (ConfigurationTransformation.NodePath)this.sharedPath.get();
      immutablePath.arr = path;
      Object[] transformedPath = action.visitPath(immutablePath, node);
      if (transformedPath != null && !Arrays.equals(path, transformedPath)) {
         this.strategy.move(node, start.getNode(transformedPath));
         node.setValue((Object)null);
      }

   }
}
