import java.io.Serializable;
import java.util.Arrays;

public class LessonManager{
    public Lesson[] lessons=new Lesson[0];
    public void addLesson(RoomManager.Room room,String LessonType,int LessonTime,int LessonLength){
        lessons= Arrays.copyOf(lessons, lessons.length+1);
        lessons[lessons.length-1] = new Lesson();
        room.AddToScedule(LessonTime, LessonLength, 1, (Object)lessons[lessons.length-1]);
        RoomManager.ScheduleConstructor schedule=room.findScheduleBasedOnTime(LessonTime);
        if(schedule==null||(Lesson) (schedule.desc)!=lessons[lessons.length-1]){lessons= Arrays.copyOf(lessons, lessons.length-1);return;}
        
        lessons[lessons.length-1].LessonRoom=room;
        lessons[lessons.length-1].LessonType=LessonType;
        lessons[lessons.length-1].LessonTime=LessonTime;
        lessons[lessons.length-1].LessonLength=LessonLength;
  

    }
    public void AddStudent(Lesson lesson,NPCManager.NPC student){
        NPCManager.AddToScedule(student, lesson.LessonTime,lesson.LessonLength, "Lesson", (Object)lesson);
    }
    public void AddTeacher(Lesson lesson,NPCManager.NPC teacher){
        NPCManager.AddToScedule(teacher, lesson.LessonTime,lesson.LessonLength, "Teaching", (Object)lesson);
    }
    public static class Lesson implements Serializable {
    NPCManager.NPC[] students= new NPCManager.NPC[0];
    NPCManager.NPC teacher;
    RoomManager.Room LessonRoom;
    String LessonType;
    int LessonTime;
    int LessonLength;
    
    }
   
    public Lesson FindLesson(RoomManager.Room room,String LessonType,int LessonTime,NPCManager.NPC  teacher){
    for(Lesson lesson : lessons) 
    if(room==null||lesson.LessonRoom==room)
    if(LessonType==null||lesson.LessonType==LessonType)
    if(LessonTime==-1||lesson.LessonTime==LessonTime)
    if(teacher==null||lesson.teacher==teacher)
    return lesson;
    return null;
    }

}