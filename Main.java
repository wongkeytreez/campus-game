import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channel;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;


public class Main{
    public static int fps=5;
    public static int tileSize=50;
    public static int stepsize=250/fps;
    public static int DayLengthInSesions=3;
    public static int SessionLength=30;
    public static CallbackPanel panel =new CallbackPanel(800,600);
    public static GameMap map = new GameMap(20, 10);
    public static BuildingManager BuildingHandler = new BuildingManager(panel);
    public static MSCManager MSCHandler = new MSCManager();
    public static NPCManager NPCHandler = new NPCManager(panel);
    public static RoomManager RoomHandler= new RoomManager();
    public static LessonManager Lessonhandler = new LessonManager();
    public static BlockManager BlockHandler = new BlockManager(map,panel);
    //time is day- hour - minute, lets say, 2936
    // 2936 / (60*24) = 2 r 56
    // 56/60 = 0 r 56
    // 56/1 = 56
    // time = 2 days,0hr,56 mins
    private static void Save(){
        fileHandler.saveFile(
            new fileHandler.SaveStruct(
                NPCHandler.NPCs.toArray(new NPCManager.NPC[0]),
                RoomHandler.rooms, Lessonhandler.lessons, map.map),
                 "hi");
    }
    private static void load(){
        fileHandler.SaveStruct save = 
        fileHandler.loadFile("hi");
        if(save.npcs!=null)
        NPCHandler.NPCs=new ArrayList<>(Arrays.asList(save.npcs));
        Lessonhandler.lessons=save.lessons;
        RoomHandler.rooms=save.rooms;
        map.map=save.map;

    }
    public static float time=0;
    public static void main(String[] args) {
    BlockHandler.AddBlock(1,0, 0, 3, 4);
    BlockHandler.AddBlock(1,0, 0, 4, 4);
    BlockHandler.AddBlock(1,0, 0, 5, 4);
    BlockHandler.AddBlock(1,0, 0, 6, 4);
    BlockHandler.AddBlock(1,0, 0, 7, 5);
    BlockHandler.AddBlock(4,0, 1, 8, 4);

    BlockHandler.AddBlock(3,0, 0, 2, 2);
    BlockHandler.AddBlock(3,0, 0, 3, 2);
    BlockHandler.AddBlock(3,0, 0, 4, 2);
    BlockHandler.AddBlock(3,0, 0, 5, 2);
    BlockHandler.AddBlock(3,0, 0, 6, 2);
    BlockHandler.AddBlock(3,0, 0, 7, 2);
    BlockHandler.AddBlock(3,0, 0, 8, 2);
    BlockHandler.AddBlock(3,0, 0, 9, 2);
    BlockHandler.AddBlock(3,0, 0, 2, 3);

    NPCManager.NPC npc1= NPCHandler.new NPC(1,"e");
    NPCManager.NPC npc2= NPCHandler.new NPC(1,"e");
    NPCManager.NPC npc3= NPCHandler.new NPC(1,"e");
    NPCManager.NPC npc4= NPCHandler.new NPC(1,"e");
    NPCManager.NPC npc5= NPCHandler.new NPC(1,"e");
    NPCManager.NPC npc6= NPCHandler.new NPC(2,"teacher");
    RoomHandler.AddRoomTiles(new int[]{3,3,3,4,4,4,5,5,5}, new int[]{3,4,5,3,4,5,3,4,5},"LearningRoom");
    RoomHandler.AddRoomTiles(new int[]{6,6,6,7,7,7,8,8,8,9,9,9}, new int[]{3,4,5,3,4,5,3,4,5,3,4,5},"LearningRoom");
    Lessonhandler.addLesson(RoomHandler.rooms[0],"craft",0,2);
    Lessonhandler.AddStudent(Lessonhandler.lessons[0], npc1);
    Lessonhandler.AddStudent(Lessonhandler.lessons[0], npc2);
    Lessonhandler.AddStudent(Lessonhandler.lessons[0], npc3);
    Lessonhandler.AddStudent(Lessonhandler.lessons[0], npc4);
    Lessonhandler.AddStudent(Lessonhandler.lessons[0], npc5);
    Lessonhandler.AddTeacher(Lessonhandler.lessons[0], npc6);
    System.out.println(((BlockManager.ExtrasClass)map.map[8][4].descriptor).type);
    //Save();
    //load();
    panel.addButton(3, 3, 50, 50, null, ()->{
        if(BuildingHandler.PreBuildMode==true){
        BuildingHandler.PreBuildMode=false;
        BuildingHandler.BuildMode=false;
        
        }else{
        BuildingHandler.BuildingRoom=false;
        BuildingHandler.PreBuildMode=true;
        }
    });
    panel.addButton(3, 53, 50, 50, null, ()->{
        if(BuildingHandler.PreBuildMode==true){
        BuildingHandler.PreBuildMode=false;
        BuildingHandler.BuildMode=false;
        
        }else{
        BuildingHandler.BuildingRoom=true;
        BuildingHandler.RoomType="LearningRoom";
        BuildingHandler.PreBuildMode=true;
        }
    });
    panel.addCallback(()->{
    for(int x=0;x<map.map.length;x++){
    for(int y=0;y<map.map[x].length;y++){
        BlockHandler.draw(map.map[x][y],x,y);
    }}
    });

    panel.addCallback(()->{
    npc1.DrawNPC();
    for(NPCManager.NPC npc:NPCHandler.NPCs){
        npc.DrawNPC();
        npc.updateNPC();
    }
    });
    

    while (true){try{
        Thread.sleep(1000/fps); 
        panel.repaint();
        time=(time+1/(fps/5f))%(DayLengthInSesions*SessionLength);
        System.out.println(time);
    } catch (InterruptedException e) {e.printStackTrace();}}
}
   
}