import java.util.*;
import java.awt.Point;
import java.io.Serializable;

public class GameMap implements Serializable{
    public class TileData implements Serializable {
        public byte type ;  // 0 = walkable, others = blocked
        public byte dir;
        public RoomManager.Room room=null;
        public Object descriptor ;
        public TileData(byte Type,byte Dir,Object desc,int x,int y) {// this.x=x;this.y=y;
            dir=Dir;type = Type;descriptor=desc; }
       
    }

    transient TileData[][] map;

    public GameMap(int w, int h) {
        map = new TileData[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                map[i][j] = new TileData((byte) 0,(byte)0,null,i,j);
    }

    public void addTile(byte type,byte dir,Object desc,int x,int y) {
    map[x][y]= new TileData(type,dir,desc,x,y);}
    public byte[] pathfind(int startX, int startY, int endX, int endY){
        return pathfind(startX, startY, endX, endY,0);
    }
    
    // compact A* that doesn't allocate full 2D arrays
public byte[] pathfind(int startX, int startY, int endX, int endY, int randomness) {
    // bounds check
    if (startX < 0 || startX >= map.length || startY < 0 || startY >= map[startX].length ||
        endX   < 0 || endX   >= map.length || endY   < 0 || endY   >= map[endX].length) return null;

    // helpers
    final int[] dx = {+1, 0, -1, 0};
    final int[] dy = {0, +1, 0, -1};
    final byte[] dirCode = {1, 2, 3, 4}; // matches dx/dy order

    // pack coordinate into single int key (safe if rows*cols fits in int)
    final java.util.function.BiFunction<Integer,Integer,Integer> pack = (x,y) -> x * map[x].length + y;

    // open PQ ordered by f = g + h
    PriorityQueue<PathNode> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.g + n.h));
    Map<Integer, PathNode> nodes = new HashMap<>(); // keyed by packed coord
    Set<Integer> closed = new HashSet<>();

    int startKey = pack.apply(startX, startY);
    PathNode start = new PathNode(startX, startY);
    start.g = 0;
    start.h = Math.abs(endX - startX) + Math.abs(endY - startY);
    nodes.put(startKey, start);
    open.add(start);

    while (!open.isEmpty()) {
        PathNode cur = open.poll();
        int curKey = pack.apply(cur.x, cur.y);
        // skip stale entries (we always keep the authoritative node in the map)
        PathNode authoritative = nodes.get(curKey);
        if (authoritative != cur) continue;

        if (cur.x == endX && cur.y == endY) {
            // reconstruct path (reverse then flip)
            List<Byte> pathRev = new ArrayList<>();
            PathNode p = cur;
            while (p.parent != null) {
                int px = p.parent.x, py = p.parent.y;
                for (int i = 0; i < 4; i++) {
                    if (px + dx[i] == p.x && py + dy[i] == p.y) {
                        pathRev.add(dirCode[i]);
                        break;
                    }
                }
                p = p.parent;
            }
            // reverse to get from start->end
            byte[] path = new byte[pathRev.size()];
            for (int i = 0; i < path.length; i++) path[i] = pathRev.get(i);
            return path;
        }

        closed.add(curKey);

        for (int i = 0; i < 4; i++) {
            int nx = cur.x + dx[i], ny = cur.y + dy[i];
            if (nx < 0 || ny < 0 || nx >= map.length || ny >= map[nx].length) continue;
            int nKey = pack.apply(nx, ny);
            if (closed.contains(nKey)) continue;
            if (map[nx][ny].type != 0 && !(nx == endX && ny == endY)) continue;

            int ng = cur.g + 1;
            PathNode next = nodes.get(nKey);
            if (next == null || ng < next.g) {
                // create new authoritative node and overwrite map entry
                PathNode newNode = new PathNode(nx, ny);
                newNode.g = ng;
                newNode.h = Math.abs(endX - nx) + Math.abs(endY - ny) + (randomness > 0 ? (int)(Math.random()*randomness) : 0);
                newNode.parent = cur;
                nodes.put(nKey, newNode);
                open.add(newNode);
            }
        }
    }

    return null; // no path
}

// simple node class
static class PathNode implements Comparable<PathNode> {
    int x, y;
    int g = Integer.MAX_VALUE;
    int h = 0;
    PathNode parent = null;
    PathNode(int x, int y) { this.x = x; this.y = y; }
    @Override public int compareTo(PathNode o) { return Integer.compare(this.g + this.h, o.g + o.h); }
}

}
