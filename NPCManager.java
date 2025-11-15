import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;



public class NPCManager implements Serializable{
    transient ArrayList<NPC> NPCs= new ArrayList<>();
    static BufferedImage[] ListOfHeads= fileHandler.loadPngImagesFromFolder("images/heads");
    static BufferedImage[] ListOfStudentBodies= fileHandler.loadPngImagesFromFolder("images/customers");
    static BufferedImage[] ListOfWorkerBodies= fileHandler.loadPngImagesFromFolder("images/special skins");
    transient private CallbackPanel panel;

    public NPCManager(CallbackPanel panel){
    this.panel=panel;
    }
    static class ScheduleConstructor implements Serializable {
        int time;
        String Type;
        int status=0;
        Object Details;
        public ScheduleConstructor (int time,String Type,Object Details){   
        this.time=time;this.Details=Details;this.Type=Type;
        }}
    static public ScheduleConstructor SearchNPCSchedule(NPC npc,int time){
        for(ScheduleConstructor current :npc.scedule)
        if(current.time==time) return current;
        return null;
    }
    static public void AddToScedule(NPC npc,int time,int length, String Type,Object Details){
    for(ScheduleConstructor current : npc.scedule) if(current.time>=time&&current.time<time+length*Main.SessionLength)return;
    npc.scedule=Arrays.copyOf(npc.scedule, npc.scedule.length+length);
    for(int i=0;i<length;i++)
    npc.scedule[npc.scedule.length-(length-i)]= new ScheduleConstructor(time+i*Main.SessionLength, Type, Details);
    }
    public class NPC implements Serializable{
        private byte[]path=new byte[0];
        int type;
        int costume=0;
        String name;
        byte level;
        byte classType;
        transient GameMap.TileData SittingOn;
        transient int x=1,y=1;
        transient byte innerGridX,innerGridY;
        ScheduleConstructor[] scedule = new ScheduleConstructor[0];
        public NPC(int type,String name){
        
        this.type=type;
        this.name=name;
        NPCs.add(this);
        }
        public class Status{
          String status;
          byte[]pathfind;

        }
        public void DrawNPC(){
             if(type==0)
        panel.addImage(ListOfStudentBodies[costume*4+2],(int)((x+innerGridX/100f)*Main.tileSize),(int)((y+innerGridY/100f)*Main.tileSize),0.0035*Main.tileSize,0);
        else 
        panel.addImage(
            ListOfWorkerBodies[(type-1)*4+2]
            ,(int)((x+innerGridX/100f)*Main.tileSize)
            ,(int)((y+innerGridY/100f)*Main.tileSize)
            ,0.0035*Main.tileSize,0);
       
        panel.addImage(ListOfHeads[costume*4+2],(int)((x+innerGridX/100f)*Main.tileSize),(int)((y-0.6f+innerGridY/100f)*Main.tileSize),0.0035*Main.tileSize,0);
        }
        private void walk(){
            if(SittingOn!=null){((BlockManager.ChairClass)SittingOn.descriptor).seated=false;SittingOn=null;}
             
            {
             if(path[path.length-1]==1)innerGridX+=Main.stepsize;
             else if(path[path.length-1]==2)innerGridY+=Main.stepsize;
             else if(path[path.length-1]==3)innerGridX-=Main.stepsize;
             else if(path[path.length-1]==4)innerGridY-=Main.stepsize;
             if(innerGridX >= -50 && innerGridX <= 49 && innerGridY >= -50 && innerGridY <= 49) return;
            
           
             if(!(innerGridX >= -50 && innerGridX <= 49)){x+=innerGridX/Math.abs(innerGridX);innerGridX=(byte)((innerGridX+150)%100-50);}
             if(!(innerGridY >= -50 && innerGridY <= 49)){y+=innerGridY/Math.abs(innerGridY);innerGridY=(byte)((innerGridY+150)%100-50);}
             path=Arrays.copyOf(path,path.length-1);
           
            }
        }
        public void updateNPC(){
           int scheduleTime= ((int)Main.time/Main.SessionLength)*Main.SessionLength;
           if(SittingOn!=null){innerGridX=0;innerGridY=0;}
           for(ScheduleConstructor current :scedule)
           if(current.time==scheduleTime){
            if(Main.time==scheduleTime){
            ScheduleConstructor lastSchedule=(scheduleTime==0)?SearchNPCSchedule(this,(Main.DayLengthInSesions-1)*Main.SessionLength):SearchNPCSchedule(this,scheduleTime-Main.SessionLength);
            if(lastSchedule!=null&&lastSchedule.Type==current.Type&&lastSchedule.Details==current.Details) current.status=lastSchedule.status;
            else {current.status=0;System.out.println((scheduleTime==0)+" "+(Main.DayLengthInSesions-1)*Main.SessionLength);}
            }
            if(current.Type=="Lesson"){

             LessonManager.Lesson lesson = (LessonManager.Lesson)current.Details;
             
             if(current.status==0){current.status=1;
             path=Main.map.pathfind(x,y, lesson.LessonRoom.centerx, lesson.LessonRoom.centery,10);}
             
             else if(current.status==1){
             if(Main.map.map[x][y].room==lesson.LessonRoom)current.status=2;
             else walk();}

             else if(current.status==2){current.status=3;

                path=lesson.LessonRoom.pathfindToChair(x, y, true, true);;
               if(path==null){System.out.println(x+" "+y+"  "+(Main.map.map[x][y].room==lesson.LessonRoom));System.exit(0);}
            }
            else if(current.status==3){
                if(path.length>0)walk();   
                else{if(((BlockManager.ChairClass)Main.map.map[x][y].descriptor).seated==false){
                current.status=4;
                SittingOn=Main.map.map[x][y];
                ((BlockManager.ChairClass)SittingOn.descriptor).seated=true;}
                else current.status=2;
            }
            }
            }
            else if(current.Type=="Teaching"){
            LessonManager.Lesson lesson = (LessonManager.Lesson)current.Details;
             
             if(current.status==0){current.status=1;
             path=Main.map.pathfind(x,y, lesson.LessonRoom.centerx, lesson.LessonRoom.centery,10);}
             
             else if(current.status==1){
             if(Main.map.map[x][y].room==lesson.LessonRoom)current.status=2;
             else walk();}
            else if(current.status==2){
                current.status=3;
                path=lesson.LessonRoom.pathfindToExtras(x, y,0);

            }
            else if(current.status==3){
                   if(path.length>0) walk();
            }
            }
            return;
           }
           //do nothing
           //walk aroung or go home
      if(Main.time-scheduleTime==0) path=Main.RoomHandler.PathfindToNearestRoom(null, x, y);
      else if(path.length>0) walk();
      else {
        if(Math.random()<0.75) return;
        int xChange=0,yChange=0;
        do {
        xChange=(int)Math.round(Math.random()*10)-5;
        yChange=(int)Math.round(Math.random()*10)-5;
        
        
        }while(x+xChange>=Main.map.map.length||
               x+xChange<0||
               y+yChange>=Main.map.map[x+xChange].length||
               y+yChange<0||
               Main.map.map[x+xChange][y+yChange].type!=0||
               Main.map.map[x+xChange][y+yChange].room!=null);

        path=Main.map.pathfind(x, y,x+ xChange, y+yChange,30);
       
      }
}
    }
}
