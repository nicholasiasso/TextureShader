package com.shadower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class LightSource{

    private static Texture tex;
    private static BitmapFont font = new BitmapFont();

    private double angle, radius, xCenter, yCenter;

    //Store these so that they only have to be calculated when the mouse is clicked
    private int x, y;

    public LightSource(int xCenter, int yCenter) {

        this.tex = new Texture("light.png");

        this.xCenter = xCenter;
        this.yCenter = yCenter;

        this.angle = 0.0;
        this.radius = 450;

        this.x = (int) (this.xCenter + Math.cos(angle) * radius) - 16;
        this.y = (int) (this.yCenter + Math.sin(angle) * radius) - 16;

    }

    /**
     *
     * This render the light source and stores the angle of the light
     * It also calculates the slope of the light and draws the text to the screen.
     *
     * @param batch
     */
    public void render(Batch batch) {

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            double mouseX = Gdx.input.getX();
            double mouseY = Gdx.input.getY();

            this.angle = calculateAngle(mouseX, mouseY);

            this.x = (int) (this.xCenter + Math.cos(angle) * radius) - 16;
            this.y = (int) (this.yCenter + Math.sin(angle) * radius) - 16;

            double distFromCenter = Math.sqrt(Math.pow(mouseX - this.xCenter, 2) + Math.pow(mouseY - this.yCenter, 2));
            Shaders.slope = (float)(-300 / distFromCenter);

        }

        String xStr = Double.toString(Math.cos(angle));
        String yStr = Double.toString(Math.sin(angle));
        String zStr = Float.toString(Shaders.slope);

        String vecStr = "[x: " + xStr.substring(0, Math.min(5, xStr.length())) + ", y: " + yStr.substring(0, Math.min(5, yStr.length())) + ", z: " + zStr.substring(0, Math.min(5, zStr.length())) + "]";

        font.draw(batch, vecStr, 20, (int)(this.yCenter * 2 - 20));
        batch.draw(tex, this.x, this.y);

    }


    /**
     *
     * This takes the mouse inputs and calculates the angle of the light source form the center of the screen
     *
     * @param mouseX
     * @param mouseY
     * @return
     */
    private double calculateAngle(double mouseX, double mouseY){
        double angle;
        if ((mouseY - yCenter) != 0){
            angle = Math.atan((mouseX - xCenter)/(mouseY - yCenter));

            if (mouseY > yCenter){
                angle = ((Math.PI * 2) + (angle - Math.PI / 2));
            } else if (mouseY < yCenter){
                angle += Math.PI / 2;
            }
        } else {
            if (mouseX > xCenter) {
                angle = 0;
            } else {
                angle = Math.PI;
            }
        }
        return angle;
    }

    public double getAngle(){
        return this.angle;
    }

    public void dispose(){
        this.tex.dispose();
    }

}
