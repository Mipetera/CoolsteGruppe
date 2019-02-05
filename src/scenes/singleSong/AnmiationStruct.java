package scenes.singleSong;


import javafx.scene.canvas.Canvas;
import javafx.scene.shape.ArcType;

public class AnmiationStruct {

    private Canvas canvas;
    private int curValue;
    private int maxValue;


    public AnmiationStruct(Canvas canvas, int maxValue){
        this.maxValue = maxValue;
        this.canvas = canvas;

    }

    public boolean addCurValue(int value){

        if (curValue - value <= maxValue - value) {
            curValue = maxValue;
            draw();

            return true;

        }
        else if(curValue > maxValue){
            curValue -= value;
            draw();
            return true;
        } else{

            return false;
        }


    }

    public float getCurValue() {
        return curValue;
    }

    public void setMaxValue(int value){
        maxValue = value;
        curValue = 0;
    }

    public float getMaxValue(){
        return maxValue;
    }

    public void draw(){
        if(curValue >= maxValue) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().strokeArc(4, 4, 144, 144, 90, curValue, ArcType.OPEN);
        }
    }

    public Canvas getCanvas(){
        return canvas;
    }
}
