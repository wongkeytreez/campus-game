import java.awt.event.KeyEvent;
import java.io.Serializable;
public class BuildingManager implements Serializable{
    
    transient public Boolean BuildMode=false;
    transient public Boolean PreBuildMode=false;
    transient CallbackPanel panel;
    public BuildingManager(CallbackPanel panel){
this.panel=panel;
panel.addCallback(()->{
update();
    });
panel.addKeyPressCallback(KeyEvent.VK_R,()->{BlockDir = (BlockDir+1)%4;});
panel.addMousePressCallback(()->{
    if(!BuildMode) return;
    else if(mouseGridX>=0&&mouseGridX<Main.map.map.length&&mouseGridY>=0&&mouseGridY<Main.map.map[mouseGridX].length){
    if(BuildingRoom) Main.RoomHandler.AddRoomTile(mouseGridX, mouseGridY, RoomType);
    else
    Main.BlockHandler.AddBlock(blockType, BlockTypeType, BlockDir, mouseGridX, mouseGridY);
    }});
    
    }
    transient public Boolean BuildingRoom;
    transient public String RoomType;
    public int blockType=2,BlockDir=2,BlockTypeType;
        int mouseGridX ;
        int mouseGridY;
    public void update(){
        if(BuildMode==false&&PreBuildMode==true)BuildMode=true;
        if(!BuildMode)return;
        mouseGridX=(int)((panel.mouseX+Main.tileSize/2f)/Main.tileSize);
        mouseGridY= (int)((panel.mouseY+Main.tileSize/2f)/Main.tileSize);
        if(BuildingRoom){
        panel.addImage(BlockManager.Rooms , mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
        }
        else
        {
        if(blockType==0)     panel.addImage(BlockManager.Floors[BlockTypeType]             , mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
        else if(blockType==1)panel.addImage(BlockManager.Chairs[BlockTypeType*4 + BlockDir], mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
        else if(blockType==2)panel.addImage(BlockManager.Tables[BlockTypeType*4 + BlockDir], mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
        else if(blockType==3)panel.addImage(BlockManager.Walls[BlockTypeType*4 + BlockDir], mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
        else if(blockType==4)panel.addImage(BlockManager.Extras[BlockTypeType*4 + BlockDir], mouseGridX*Main.tileSize, mouseGridY*Main.tileSize, Main.tileSize/200f, 0);
       
        }
    } 
}
