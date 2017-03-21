package com.shadower;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PixelMap {

    Shaders shader;
    int[][] heightMap;
    Texture img;
    Pixmap pixmap;

    double oldAngle = -1;
    float[][] combinedShading;

    public PixelMap(String inputFile){
        this.heightMap = this.parsePNG(inputFile);
        this.shader = new Shaders(heightMap);
    }


    /**
     *
     * This does the computation to render the heightMap when light is cast onto it from [angle]
     *
     * @param batch
     * @param angle
     */
    public void render(Batch batch, double angle){
        float[][] directionalShading;
        boolean[][] selfShading;

        if (this.oldAngle != angle) {

            this.oldAngle = angle;

            directionalShading = this.shader.generateDirectionalLighting(angle);
            selfShading = this.shader.applySelfShadowing(angle);
            this.combinedShading = this.combineShaders(directionalShading, selfShading);

        }

        this.renderPixels(batch, this.combinedShading);
    }


    /**
     *
     * Cleans up the saved variables
     *
     */
    public void dispose(){
        this.heightMap = null;
    }


    /**
     *
     * This loads in a PNG and converts it to a 2D array of ints representing the height
     *
     * @param inputFile
     * @return
     */
    private int[][] parsePNG(String inputFile){

        int[][] parsedPNG = new int[512][512];

        BufferedImage bufImg = null;
        try {
            bufImg = ImageIO.read(new File(inputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){
                parsedPNG[i][j] = bufImg.getRGB(i, j) % 257;
            }
        }

        return parsedPNG;
    }


    /**
     *
     * This layers the directional lighting array and the self shadowing array
     *
     * @param directionalShading
     * @param selfShading
     * @return
     */
    private float[][] combineShaders(float[][] directionalShading, boolean[][]selfShading){
        float[][] combined = new float[512][512];

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){
                if (selfShading[i][j]){
                    combined[i][j] = directionalShading[i][j];
                } else {
                    combined[i][j] = 0f;
                }
            }
        }

        return combined;
    }


    /**
     *
     * This merged the 2D array of shaded values and renders it to the screen
     *
     * @param batch
     * @param shadedPixels
     */
    private void renderPixels(Batch batch, float[][] shadedPixels){

        this.pixmap = new Pixmap(512, 512, Pixmap.Format.RGB888);

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){
                float shade = shadedPixels[i][j];
                pixmap.drawPixel(i, j, Color.rgba8888(shade, shade, shade, 0.99f));
            }
        }

            this.img = new Texture(pixmap);

            batch.draw(this.img, ShaderMain.width / 2 - 256, ShaderMain.height / 2 - 256);
    }


    /**
     * This cleans up after an image rendering
     */
    public void cleanup(){
        this.img.dispose();
        this.pixmap.dispose();
    }
}
