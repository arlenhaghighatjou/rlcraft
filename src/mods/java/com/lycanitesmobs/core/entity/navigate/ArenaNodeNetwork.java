package com.lycanitesmobs.core.entity.navigate;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArenaNodeNetwork {
   public World world;
   public List<ArenaNode> nodes = new ArrayList();
   public ArenaNode centralNode;

   public ArenaNodeNetwork(World world) {
      this.world = world;
   }

   public void addNode(ArenaNode node, ArenaNode... adjacentNodes) {
      if (node != null && !this.nodes.contains(node)) {
         this.addNode(node);

         for(ArenaNode adjacentNode : adjacentNodes) {
            node.addAdjacentNode(adjacentNode);
         }

      }
   }

   public void addNode(ArenaNode node) {
      if (node != null && !this.nodes.contains(node)) {
         this.nodes.add(node);
      }
   }

   public ArenaNode getClosestNode(BlockPos targetPos) {
      int nodesSize = this.nodes.size();
      if (nodesSize <= 0) {
         return null;
      } else if (nodesSize == 1) {
         return (ArenaNode)this.nodes.get(0);
      } else {
         double smallestDistance = 0.0D;
         ArenaNode closestNode = null;

         for(ArenaNode node : this.nodes) {
            double distance = targetPos.distanceSq(node.pos);
            if (closestNode == null || distance < smallestDistance) {
               smallestDistance = distance;
               closestNode = node;
            }
         }

         return closestNode;
      }
   }
}
