import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.io.Serializable;
public class RoomManager {
    Room[] rooms = new Room[0];
    

    public void AddRoomTile(int x,int y,String type){
    Room room =new Room();
    Main.map.map[x][y].room=room;

    room.RoomType=type;
    room.xs=Arrays.copyOf(room.xs, room.xs.length+1);
    room.xs[room.xs.length-1] =x;
    room.ys=Arrays.copyOf(room.ys, room.ys.length+1);
    room.ys[room.ys.length-1] =y;

    rooms= Arrays.copyOf(rooms, rooms.length+1);
    rooms[rooms.length-1]= room;

    Main.map.map[x][y].room=room;
     
    update();
    }
    public byte[] PathfindToNearestRoom(String RoomType,int x,int y){
        
    GameMap.TileData[][] map = Main.map.map;
    ArrayDeque<PathfindNode> ToDo = new ArrayDeque<>();
    HashSet<String> visited = new HashSet<>();

    PathfindNode StartNode = new PathfindNode(x, y, null);
    ToDo.add(StartNode);
    ToDo.add(new PathfindNode(x + 1, y, StartNode));
     ToDo.add(new PathfindNode(x, y + 1, StartNode)); 
     ToDo.add(new PathfindNode(x - 1, y, StartNode));
      ToDo.add(new PathfindNode(x, y - 1, StartNode));
    visited.add(x + "," + y);

    while (!ToDo.isEmpty()) {
        PathfindNode ParentNode = ToDo.poll();

        // bounds + room/type checks
        if (ParentNode.x < 0 || ParentNode.y < 0 
                || ParentNode.x >= map.length 
                || ParentNode.y >= map[ParentNode.x].length
                || (map[ParentNode.x][ParentNode.y].room != map[x][y].room&&
                map[ParentNode.x][ParentNode.y].room!=null&&
                map[ParentNode.x][ParentNode.y].room.RoomType != RoomType)
                || map[ParentNode.x][ParentNode.y].type != 0 
        )
            continue;

        String key = ParentNode.x + "," + ParentNode.y;
        if (!visited.add(key)) continue; // add returns false if already visited

        // success condition
        if ((RoomType==null&&map[ParentNode.x][ParentNode.y].room==null)||(map[ParentNode.x][ParentNode.y].room!=null&&
                map[ParentNode.x][ParentNode.y].room.RoomType == RoomType)) {
            List<Byte> pathList = new ArrayList<>();
            for (PathfindNode p = ParentNode; p.parent != null; p = p.parent) {
                if (p.x != p.parent.x) {
                    pathList.add((byte) (p.x == p.parent.x + 1 ? 1 : 3));
                } else {
                    pathList.add((byte) (p.y == p.parent.y + 1 ? 2 : 4));
                }
            }
            byte[] path = new byte[pathList.size()];
            for (int i = 0; i < path.length; i++) path[i] = pathList.get(i);
            return path;
        }

        // enqueue neighbors
        ToDo.add(new PathfindNode(ParentNode.x + 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x - 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y + 1, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y - 1, ParentNode));
    }

  System.out.println(x+" "+y);
return null;
    }
    public void AddRoomTiles(int[] xs,int[] ys,String type){
    if(xs.length!=ys.length)return;

    Room room=new Room();

    room.RoomType=type;
    room.xs=new int[xs.length];
    room.ys=new int[ys.length];
    for(int i=0;i<xs.length;i++){
    room.xs[i]=xs[i];
    room.ys[i]=ys[i];
    Main.map.map[xs[i]][ys[i]].room=room;
    }
    rooms= Arrays.copyOf(rooms, rooms.length+1);
    rooms[rooms.length-1]= room;
    
    update();

    }
    private void mergerooms( RoomManager.Room ThisRoom, RoomManager.Room Neighbour,GameMap.TileData[][] map){
for(RoomManager.ScheduleConstructor current:Neighbour.Schedule) {
                        if(ThisRoom.findScheduleBasedOnTime(current.Time)!=null)continue;
                }
                for(RoomManager.ScheduleConstructor current:Neighbour.Schedule) {
                    ThisRoom.AddToScedule(current.Time, 1, current.ScheduleType, current.desc);
                    //lesson
                    if(current.ScheduleType==1){
                        ((LessonManager.Lesson)current.desc).LessonRoom=ThisRoom;
                        
                    }
                        
                }
                int[]neighbourRoomxs=Neighbour.xs;
                int[]neighbourRoomys=Neighbour.ys;
                //delete neighbour room from rooms list
                for(int i=0;i<rooms.length;i++){ if(rooms[i]!=Neighbour)continue;
                System.arraycopy(rooms,i+1, rooms, i, rooms.length-i-1);
                rooms=Arrays.copyOf(rooms, rooms.length-1);
                break;}
                //set the tiles that were connected to neighbouring rooms to this room
               
                for(int i=0;i<neighbourRoomxs.length;i++)
                Main.map.map[neighbourRoomxs[i]][neighbourRoomys[i]].room=ThisRoom;
                //add positions from neighbour room to current room
               int oldLenX = ThisRoom.xs.length;
                ThisRoom.xs = Arrays.copyOf(ThisRoom.xs, oldLenX + neighbourRoomxs.length);
                System.arraycopy(neighbourRoomxs, 0, ThisRoom.xs, oldLenX, neighbourRoomxs.length);

                int oldLenY = ThisRoom.ys.length;
                ThisRoom.ys = Arrays.copyOf(ThisRoom.ys, oldLenY + neighbourRoomys.length);
                System.arraycopy(neighbourRoomys, 0, ThisRoom.ys, oldLenY, neighbourRoomys.length);
                
    }
    public void update(){
    GameMap.TileData[][] map = Main.map.map;
    for(int x=0;x<map.length;x++){
      for(int y=0;y<map[x].length;y++){
            if(map[x][y].room==null)continue;
            if(x+1<map.length)if(map[x+1][y].room!=null&&map[x+1][y].room.RoomType==map[x][y].room.RoomType&&map[x+1][y].room!=map[x][y].room)
            mergerooms( map[x][y].room,  map[x+1][y].room,Main.map.map);
            if(y+1<map[x].length)if(map[x][y+1].room!=null&&map[x][y+1].room.RoomType==map[x][y].room.RoomType&&map[x][y+1].room!=map[x][y].room)
            mergerooms( map[x][y].room,  map[x][y+1].room,Main.map.map);
        }
    }
     bigone: for (Room room : rooms) {
    int sumXs = 0;
    int sumYs = 0;
    for (int i = 0; i < room.xs.length; i++) {
        sumXs += room.xs[i];
        sumYs += room.ys[i];
    }

    room.centerx = Math.round(sumXs / (float) room.xs.length);
    room.centery = Math.round(sumYs / (float) room.ys.length);
System.out.println(room.centerx +" "+room.centery+" "+map[room.centerx][room.centery].type );
    // If the initial center is blocked, run BFS
    if (map[room.centerx][room.centery].type != 0) {
        ArrayDeque<Point> ToDo = new ArrayDeque<>();
        HashSet<String> visited = new HashSet<>();

        ToDo.add(new Point(room.centerx + 1, room.centery));
        ToDo.add(new Point(room.centerx - 1, room.centery));
        ToDo.add(new Point(room.centerx, room.centery + 1));
        ToDo.add(new Point(room.centerx, room.centery - 1));
        visited.add(room.centerx + "," + room.centery);

        while (!ToDo.isEmpty()) {
            Point parent = ToDo.poll();
            String key = parent.x + "," + parent.y;

            if (visited.contains(key)) continue;
            visited.add(key);

            // bounds check
            if (parent.x < 0 || parent.x >= map.length ||
                parent.y < 0 || parent.y >= map[0].length
                || map[parent.x][parent.y].room!=room) {
                continue;
            }

            // found a free spot
            if (map[parent.x][parent.y].type == 0) {
                room.centerx = parent.x;
                room.centery = parent.y;
                System.out.println(key);
                continue bigone;
            }

            // expand neighbors
            ToDo.add(new Point(parent.x + 1, parent.y));
            ToDo.add(new Point(parent.x - 1, parent.y));
            ToDo.add(new Point(parent.x, parent.y + 1));
            ToDo.add(new Point(parent.x, parent.y - 1));
        }

        // no free tile found
        room.centerx = -1;
        room.centery = -1;
    }
}

    }
    private static class PathfindNode{
        int x,y;
        PathfindNode parent;
        public PathfindNode(int x, int y, PathfindNode parent){
this.x=x;this.y=y;this.parent=parent;
        }
    }
    public static class ScheduleConstructor implements Serializable {
        int Time,ScheduleType;
        Object desc;
        public ScheduleConstructor(int Time,int ScheduleType,Object desc) {
            this.Time=(Time/Main.SessionLength)*Main.SessionLength;
            this.ScheduleType=ScheduleType;
            this.desc=desc;
        }
        
    }
        public static class Room implements Serializable{
        String RoomType;
        int centerx,centery;
        int[] xs=new int[0];
        int[] ys=new int[0];
        public ScheduleConstructor[]Schedule = new ScheduleConstructor[0];
        public void AddToScedule(int StartTime,int Length,int ScheduleType,Object desc){
        for(ScheduleConstructor current:Schedule){
        if(current.Time>=StartTime&&current.Time<=StartTime+(Length-1)*Main.SessionLength)return;
        }
        Schedule=Arrays.copyOf(Schedule,Schedule.length+Length);
        for(int i=0;i<Length;i++)
        
        Schedule[Schedule.length-(Length-i)]= new ScheduleConstructor(StartTime+i*Main.SessionLength,ScheduleType,desc);
        }
        public ScheduleConstructor findScheduleBasedOnTime(int time){
            time=(time/Main.SessionLength)*Main.SessionLength;
            for(ScheduleConstructor current:Schedule)if(current.Time==time)return current;
            return null;
        }
      public byte[] pathfindToChair(int startx, int starty, Boolean Notseated, Boolean connected) {
    GameMap.TileData[][] map = Main.map.map;
    ArrayDeque<PathfindNode> ToDo = new ArrayDeque<>();
    HashSet<String> visited = new HashSet<>();

    PathfindNode StartNode = new PathfindNode(startx, starty, null);
    ToDo.add(StartNode);
    ToDo.add(new PathfindNode(startx + 1, starty, StartNode)); ToDo.add(new PathfindNode(startx, starty + 1, StartNode)); ToDo.add(new PathfindNode(startx - 1, starty, StartNode)); ToDo.add(new PathfindNode(startx, starty - 1, StartNode));
    visited.add(startx + "," + starty);

    while (!ToDo.isEmpty()) {
        PathfindNode ParentNode = ToDo.poll();

        // bounds + room/type checks
        if (ParentNode.x < 0 || ParentNode.y < 0 
                || ParentNode.x >= map.length 
                || ParentNode.y >= map[ParentNode.x].length
                || map[ParentNode.x][ParentNode.y].room != this
                || (map[ParentNode.x][ParentNode.y].type != 0 && map[ParentNode.x][ParentNode.y].type != 1)
                || (Notseated && map[ParentNode.x][ParentNode.y].type == 1
                && ((BlockManager.ChairClass) map[ParentNode.x][ParentNode.y].descriptor).seated))
            continue;

        String key = ParentNode.x + "," + ParentNode.y;
        if (!visited.add(key)) continue; // add returns false if already visited

        // success condition
        if (map[ParentNode.x][ParentNode.y].type == 1) {
            List<Byte> pathList = new ArrayList<>();
            for (PathfindNode p = ParentNode; p.parent != null; p = p.parent) {
                if (p.x != p.parent.x) {
                    pathList.add((byte) (p.x == p.parent.x + 1 ? 1 : 3));
                } else {
                    pathList.add((byte) (p.y == p.parent.y + 1 ? 2 : 4));
                }
            }
            byte[] path = new byte[pathList.size()];
            for (int i = 0; i < path.length; i++) path[i] = pathList.get(i);
            return path;
        }

        // enqueue neighbors
        ToDo.add(new PathfindNode(ParentNode.x + 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x - 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y + 1, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y - 1, ParentNode));
    }

    System.out.println("e");
    return null;
}


public byte[] pathfindToExtras(int startx, int starty, int type) {
    GameMap.TileData[][] map = Main.map.map;
    ArrayDeque<PathfindNode> ToDo = new ArrayDeque<>();
    HashSet<String> visited = new HashSet<>();

    PathfindNode StartNode = new PathfindNode(startx, starty, null);
    ToDo.add(StartNode);
    ToDo.add(new PathfindNode(startx + 1, starty, StartNode)); ToDo.add(new PathfindNode(startx, starty + 1, StartNode)); ToDo.add(new PathfindNode(startx - 1, starty, StartNode)); ToDo.add(new PathfindNode(startx, starty - 1, StartNode));
    visited.add(startx + "," + starty);

    while (!ToDo.isEmpty()) {
        PathfindNode ParentNode = ToDo.poll();

        // bounds + room/type checks
        if (ParentNode.x < 0 || ParentNode.y < 0 
                || ParentNode.x >= map.length 
                || ParentNode.y >= map[ParentNode.x].length
                || map[ParentNode.x][ParentNode.y].room != this
                || (map[ParentNode.x][ParentNode.y].type != 0 && map[ParentNode.x][ParentNode.y].type != 4)
                || (map[ParentNode.x][ParentNode.y].type == 4
                && ((BlockManager.ExtrasClass) map[ParentNode.x][ParentNode.y].descriptor).type != type))
            continue;

        String key = ParentNode.x + "," + ParentNode.y;
        if (!visited.add(key)) continue;

        // success condition
        if (map[ParentNode.x][ParentNode.y].type == 4) {
            List<Byte> pathList = new ArrayList<>();
            for (PathfindNode p = ParentNode; p.parent != null; p = p.parent) {
                if (p.x != p.parent.x) {
                    pathList.add((byte) (p.x == p.parent.x + 1 ? 1 : 3));
                } else {
                    pathList.add((byte) (p.y == p.parent.y + 1 ? 2 : 4));
                }
            }
            byte[] path = new byte[pathList.size()];
            for (int i = 0; i < path.length; i++) path[i] = pathList.get(i);
            return path;
        }

        // enqueue neighbors
        ToDo.add(new PathfindNode(ParentNode.x + 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x - 1, ParentNode.y, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y + 1, ParentNode));
        ToDo.add(new PathfindNode(ParentNode.x, ParentNode.y - 1, ParentNode));
    }

    System.out.println("e");
    return null;
}


    }
}
