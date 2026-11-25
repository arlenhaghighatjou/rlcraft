package greymerk.roguelike.dungeon.layout;

import greymerk.roguelike.dungeon.base.BaseRoom;
import greymerk.roguelike.worldgen.Bounded;
import greymerk.roguelike.worldgen.Coord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LevelLayout {
   private final List<DungeonNode> nodes = new ArrayList();
   private final List<DungeonTunnel> tunnels = new ArrayList();
   private DungeonNode start;
   private DungeonNode end;

   public LevelLayout() {
   }

   public DungeonNode getStart() {
      return this.start;
   }

   public void setStart(DungeonNode start) {
      this.start = start;
      this.addNode(start);
   }

   public DungeonNode getEnd() {
      return this.end;
   }

   public void setEnd(DungeonNode end) {
      this.end = end;
      this.addNode(end);
   }

   public void addNode(DungeonNode node) {
      this.nodes.add(node);
   }

   public void addTunnel(DungeonTunnel tunnel) {
      this.tunnels.add(tunnel);
   }

   public void addTunnels(List<DungeonTunnel> tunnels) {
      this.tunnels.addAll(tunnels);
   }

   public List<DungeonNode> getNodes() {
      return this.nodes;
   }

   public List<DungeonTunnel> getTunnels() {
      return this.tunnels;
   }

   public void setStartEnd(EaglercraftRandom param1, DungeonNode param2) {
      // $FF: Couldn't be decompiled
   }

   private int largestXZDistance(DungeonNode start, DungeonNode node) {
      return Math.abs(start.getPosition().getX() - node.getPosition().getX()) + Math.abs(start.getPosition().getZ() - node.getPosition().getZ());
   }

   private boolean anyTunnelsOverlap(DungeonNode node, int size) {
      return this.getTunnels().stream().anyMatch((tunnel) -> {
         return node.overlaps(size, tunnel);
      });
   }

   public DungeonNode getBestFit(BaseRoom room) {
      return (DungeonNode)this.findFirstNonOverlappingNode(room).orElseGet(this::findFirstConnectingNode);
   }

   private Optional<DungeonNode> findFirstNonOverlappingNode(BaseRoom room) {
      return this.getNodes().stream().filter(this::isNotEdgeNode).filter(DungeonNode::isNotYetGenerated).filter((node) -> {
         return !node.hasOverlappingNode(room.getSize() + 1, this.getNodes());
      }).findFirst();
   }

   private DungeonNode findFirstConnectingNode() {
      return (DungeonNode)this.getNodes().stream().filter(this::isNotEdgeNode).filter(DungeonNode::isNotYetGenerated).findFirst().orElse((Object)null);
   }

   private boolean overlaps(DungeonNode node, int size) {
      return this.anyTunnelsOverlap(node, size) || node.hasOverlappingNode(size, this.getNodes());
   }

   private boolean isNotEdgeNode(DungeonNode node) {
      return !this.isEdgeNode(node);
   }

   private boolean isEdgeNode(DungeonNode node) {
      return node == this.start || node == this.end;
   }

   public boolean hasEmptyRooms() {
      return this.nodes.stream().filter(this::isNotEdgeNode).anyMatch(DungeonNode::isNotYetGenerated);
   }

   public List<Bounded> getBoundingBoxes() {
      List<Bounded> boxes = new ArrayList();
      boxes.addAll(this.nodes);
      boxes.addAll(this.tunnels);
      return boxes;
   }

   public boolean isStartOrEnd(DungeonNode node) {
      return this.getStart() == node || node == this.getEnd();
   }

   public boolean containsRoomAt(Coord coord) {
      return this.getNodes().stream().anyMatch((node) -> {
         return node.contains(coord);
      });
   }
}
