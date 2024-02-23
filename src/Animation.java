//this contains the information for everything needed to play an animation
public class Animation {
    private int row,frameCount,startFrame,endFrame,fps;

    public Animation(int whichAnim, int frameCount, int fps){
        this(whichAnim,frameCount,0,frameCount-1,fps);
    }
    public Animation(int whichAnim, int frameCount, int startFrame, int endFrame, int fps){
        this.row=whichAnim;
        this.frameCount=frameCount;
        this.startFrame=startFrame;
        this.endFrame=endFrame;
        this.fps=fps;
    }

    public int getRow() {
        return row;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getStart() {
        return startFrame;
    }

    public int getEnd() {
        return endFrame;
    }

    public int getFPS() {
        return fps;
    }
}
