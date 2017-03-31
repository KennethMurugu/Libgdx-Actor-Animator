package com.zd.kadi.production;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.zd.kadi.production.screens.Screen;


/**
 * Class provides abstractions for Animating Actors.
 * Debug messages are printed by default. Use {@link Screen#setScreenDebug(boolean)}  to change this behaviour
 * @author Kenneth
 * @version 2.0
 */
public class Animator2 {

    public static final String TAG = Animator2.class.getSimpleName();

    private EaseFuncInterface easeFunc;
    /**
     * Show debug messages for the Animator.True by default.
     */
	private boolean ANIMATOR_DEBUG;

    private Vector2 speed,tmpSpeed, destination;
    public Animator2(){
		this.ANIMATOR_DEBUG = true;
        this.speed = new Vector2(10,10);
        this.destination = new Vector2();
        this.tmpSpeed = new Vector2();
        easeFunc = new EaseFuncInterface() {
            @Override
            public float ease(float t) {
                return 2 * t * t;
            }
        };
    }
    public Animator2(Vector2 speed){
		this.ANIMATOR_DEBUG = true;
        this.speed = speed;
        this.destination = new Vector2();
        this.tmpSpeed = new Vector2();
        easeFunc = new EaseFuncInterface() {
            @Override
            public float ease(float t) {
                return 2 * t* t;
            }
        };
    }
    public Animator2(Vector2 speed, EaseFuncInterface easeFunc){
		this.ANIMATOR_DEBUG = true;
        this.speed = speed;
        this.destination = new Vector2(0,0);
        this.tmpSpeed = new Vector2(0,0);
        this.easeFunc = easeFunc;
    }


    /**
     * Call this to begin the animation. The speed can be set using {@link Animator2#setSpeed(Vector2)} and and will be scaled using {@code time}<br>
     * The value of speed is never modified; its value is copied to another temporary variable and actor is animated using this variable.<br>
     * The ease function to use can be set using {@link Animator2#setEaseFunc(EaseFuncInterface)}
     * @param actor the actor to move
     * @param destination the position to move the actor to
     * @param time the current time
     * @return true if Actor has arrived at destination given
     */
    public boolean move(Actor actor, Vector2 destination, float time){
		Screen.DEBUG = ANIMATOR_DEBUG;
        boolean arrived = false;
        this.destination = destination;

        //time = easeIn_Quad(time / 1000f);
		//Need time in seconds; divide by 1000
        time = easeFunc.ease(time / 1000f);
        Screen.print(TAG, "[move] time: " + time);

        float diff_x = Math.abs(destination.x - actor.getX());
        float diff_y = Math.abs(destination.y - actor.getY());

        if(diff_x > diff_y){
            float diff = diff_y / diff_x;
            tmpSpeed = speed.cpy();
            if(diff > 0f) tmpSpeed.scl(time, time * diff);
            else tmpSpeed.scl(time);
        }
        else if(diff_y > diff_x){
            float diff = diff_x / diff_y;
            tmpSpeed = speed.cpy();
            if(diff > 0f) tmpSpeed.scl(time * diff, time);
            else tmpSpeed.scl(time);
        }
        else {
            tmpSpeed = speed.cpy();
            tmpSpeed.scl(time);
        }

//        tmpSpeed = speed.cpy();
//        tmpSpeed.scl(time);

        Screen.print(TAG, "tmpSpeed: x, y: "+tmpSpeed.x+" , "+tmpSpeed.y);
        //arrived = moveY(actor) && moveX(actor);
        boolean arrivedX = moveX(actor), arrivedY = moveY(actor);
        Screen.print(TAG, "actor: x, y: "+ actor.getX() +", "+ actor.getY());
        if(arrivedX && arrivedY) forceToDest(actor);
		
		Screen.DEBUG = !ANIMATOR_DEBUG;
        return arrivedX && arrivedY;
    }

    private boolean moveX(Actor actor){
        boolean arrived;
        //Determine if Left/Right of destination
        if(actor.getX() < destination.x){//Actor on left, moveRight
            Screen.print(TAG, "[moveX] moving right...");
            arrived = moveXRight(actor);
        }
        else if(actor.getX() > destination.x){//Actor on Right, moveLeft
            Screen.print(TAG, "[moveX] moving Left...");
            arrived = moveXLeft(actor);
        }
        else {
            arrived = true;
            Screen.print(TAG, "[moveX] arrived!");
        }
        return arrived;
    }

    private boolean moveXLeft(Actor actor){
        float x = actor.getX();

        x -= tmpSpeed.x;

        actor.setPosition(x, actor.getY());

        if(x <= destination.x){
//            Actor has arrived, force to dest
//            forceToDest();
            return true;
        }
        else return false;
    }
    private boolean moveXRight(Actor actor){
        float x = actor.getX();

        x += tmpSpeed.x;

        actor.setPosition(x, actor.getY());

        if(x >= destination.x){
            //Actor has arrived, force to dest
            //forceToDest();
            return true;
        }
        else return false;
    }

    private boolean moveY(Actor actor){
        boolean arrived = false;

        //Determine if Actor is above/below destination
        if(actor.getY() < destination.y){//Below destination, move Up
            Screen.print(TAG, "[moveY] moving Up...");
            arrived = moveYUp(actor);
        }
        else if(actor.getY() > destination.y){//Above destination, move Down
            Screen.print(TAG, "[moveY] moving Down...");
            arrived = moveYDown(actor);
        }
        else {
            arrived = true;
            Screen.print(TAG, "[moveY] arrived!");
        }

        return arrived;
    }
    private boolean  moveYUp(Actor actor){
        float y = actor.getY();

        y += tmpSpeed.y;

        actor.setPosition(actor.getX(), y);

        if(y >= destination.y){
            //Actor has arrived, force to dest
            //forceToDest();
            return true;
        }
        else return false;
    }
    private boolean  moveYDown(Actor actor){
        float y = actor.getY();

        y -= tmpSpeed.y;

        actor.setPosition(actor.getX(), y);

        if(y <= destination.y){
            //Actor has arrived, force to dest
            //forceToDest();
            return true;
        }
        else return false;
    }

    private void forceToDest(Actor actor){
        actor.setPosition(destination.x, destination.y);
    }

    public void forceToDest(Actor actor, Vector2 destination){
        actor.setPosition(destination.x, destination.y);
    }

    public void setSpeed(Vector2 speed) {
        this.speed = speed;
    }
	
	public void setDebug(boolean debug){
		this.ANIMATOR_DEBUG = debug;
	}

    private float easeIn_Quad(float t) {return 2 * t * t;}

    /**
     * Set the ease function to use for the animation
     * The format for the easeFunc should be a lambda expression e.g<br>
     * <pre><code>easeFunc = (t) -> t * t</code></pre>
     * @param easeFunc
     */
    public void setEaseFunc(EaseFuncInterface easeFunc) {
        this.easeFunc = easeFunc;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

/**
 * Function interface for the Animator ease functions
 */
interface EaseFuncInterface{
    float ease(float t);
}
