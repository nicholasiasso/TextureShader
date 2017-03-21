package com.shadower;


import com.badlogic.gdx.math.Vector3;

public class Shaders {

    //Slope of the sun's rays
    public static float slope = -1.0f;

    //Distance the ray casting increments (Lower value is more accurate; Higher is faster)
    private static final double incDist = 10f;

    //Loaded variables
    private int[][] heightMap;
    private Vector3[][] normalVectorMap;

    public Shaders(int[][] heightMap){
        this.loadHeightMap(heightMap);
    }


    /**
     *
     * This loads the heightMap and precomputes the normal vector to the
     * surface at all pixels for use in the directional lighting algorithm
     *
     * @param heightMap
     */
    private void loadHeightMap(int[][] heightMap){
        this.heightMap = heightMap;

        normalVectorMap = new Vector3[512][512];

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {

                if (i > 0 && i < 511 && j > 0 && j < 511) {
                    float leftSlope = this.heightMap[i][j] - this.heightMap[i-1][j];
                    float rightSlope = this.heightMap[i+1][j] - this.heightMap[i][j];
                    float xSlope = (leftSlope + rightSlope) / 2f;

                    float topSlope = this.heightMap[i][j-1] - this.heightMap[i][j];
                    float bottomSlope = this.heightMap[i][j] - this.heightMap[i][j+1];
                    float ySlope = (topSlope + bottomSlope) / 2f;

                    Vector3 normal = new Vector3(1, 0, xSlope).crs(new Vector3(0, 1, ySlope));

                    normalVectorMap[i][j] = normal.nor().scl(-1f);

                } else {
                    normalVectorMap[i][j] = new Vector3(0f, 0f, -1f);
                }
            }
        }
    }

    /**
     *
     * This takes the precomputed normalVectorMap and the preloaded heightMap and
     * creates the directional shading
     *
     * @param angle
     * @return 2D Array of floats representing shading
     */
    public float[][] generateDirectionalLighting(double angle){

        float[][] result = new float[512][512];

        //Procedure
        //1. Dot the normal vector with the light vector and scale to 0-255

        //Calculate Light Vector
        float vecLightX = (float)-Math.cos(angle);
        float vecLightY = (float)-Math.sin(angle);

        Vector3 lightVec = new Vector3(vecLightX, vecLightY, this.slope).nor();

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){

                //Calculate dot product and find angle
                float lightDotNormal = lightVec.dot(normalVectorMap[i][j]);
                double theta = Math.acos(lightDotNormal / 2f);

                //Scale to between 0 and 1 for shading
                float shading = (float)(((Math.PI / 2) - theta));

                //Shadows if the gradient it tilted more than Pi/2 away from the light
                if (shading < 0f){
                    shading = 0f;
                }

                result[i][j] = shading;
            }
        }

        return result;
    }


    /**
     *
     * This ues  simple ray tracing to calculate whether or not a pixel is in shadow.
     *
     * @param angle
     * @return 2D array of boolean representing if a pixel is lighted
     */
    public boolean[][] applySelfShadowing(double angle){
        boolean[][] result = new boolean[512][512];

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){
                result[i][j] = true;
            }
        }

        //Procedure
        //1. Start at each pixel.
        //2. "Travel" one pixel width along the negative light vector (towards the light source)
        //3. If the z value at that point is below the pixel's z value at the int casting of the ray, set to 0.
        //4. Repeat until the ray's z value is at 256

        double reverseAngle = - angle;

        double rayXInc = Math.cos(reverseAngle);
        double rayYInc = Math.sin(reverseAngle);

        for (int i = 0; i < 512; i++){
            for (int j = 0; j < 512; j++){
                double rayX = i,rayY = j, rayZ = this.heightMap[i][j];

                while (rayZ < 255){
                    //Increment ray
                    rayX += incDist * rayXInc;
                    rayY += incDist * rayYInc;
                    rayZ -= incDist * slope;

                    //Check bounds
                    if (rayX > 511 || rayX < 0 || rayY > 511 || rayY < 0){
                        break;
                    }

                    //Check shadow
                    if (this.heightMap[(int)Math.round(rayX)][(int)Math.round(rayY)] > rayZ) {
                        result[i][j] = false;
                        break;
                    }
                }
            }
        }

      return result;
    }
}
