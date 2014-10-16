/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

/**
 * A Cardboard sample application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final String TAG = "MainActivity";

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    // We keep the light always position just above the user.
    private final float[] mLightPosInWorldSpace = new float[] {0.0f, 2.0f, 0.0f, 1.0f};
    private final float[] mLightPosInEyeSpace = new float[4];

    private static final int COORDS_PER_VERTEX = 3;

    private final WorldLayoutData DATA = new WorldLayoutData();

    private FloatBuffer mFloorVertices;
    private FloatBuffer mFloorColors;
    private FloatBuffer mFloorNormals;

    private FloatBuffer mCubeVertices;
    private FloatBuffer mCubeColors;
    private FloatBuffer mCubeFoundColors;
    private FloatBuffer mCubeNormals;
    private FloatBuffer mCubeTextureCoord;

    /*
     * Handles to the attributes in the vertex & fragment shaders
     */
    private int mProgramHandler;
    private int mPositionHandler;
    private int mNormalHandler;
    private int mColorHandler;
    private int mModelViewProjectionHandler;
    private int mLightPosHandler;
    private int mModelViewHandler;
    private int mModelHandler;
    private int mIsFloorHandler;
    
    private int mTextureUniformHandler;
    private int mTextureCoordinateHandler;    
    private int mTextureDataHandler;
    private int mTextureFoundHandler;

    private float[] mModelCube;
    private float[] mCamera;
    private float[] mView;
    private float[] mHeadView;
    private float[] mModelViewProjection;
    private float[] mModelView;
    private float[] mModelFloor;

    private int mScore = 0;
    private float mObjectDistance = 12f;
    private float mFloorDepth = 20f;

    private Vibrator mVibrator;

    private CardboardOverlayView mOverlayView;


    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     * @param func
     */
    private static void checkGLError(String func) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        mModelCube = new float[16];
        mCamera = new float[16];
        mView = new float[16];
        mModelViewProjection = new float[16];
        mModelView = new float[16];
        mModelFloor = new float[16];
        mHeadView = new float[16];
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        mOverlayView.show3DToast("Pull the magnet when you find an object.");
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well

        /*
         * Make a cube
         */
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(DATA.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        mCubeVertices = bbVertices.asFloatBuffer();
        mCubeVertices.put(DATA.CUBE_COORDS);
        mCubeVertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(DATA.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        mCubeColors = bbColors.asFloatBuffer();
        mCubeColors.put(DATA.CUBE_COLORS);
        mCubeColors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(DATA.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        mCubeFoundColors = bbFoundColors.asFloatBuffer();
        mCubeFoundColors.put(DATA.CUBE_FOUND_COLORS);
        mCubeFoundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(DATA.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        mCubeNormals = bbNormals.asFloatBuffer();
        mCubeNormals.put(DATA.CUBE_NORMALS);
        mCubeNormals.position(0);
        
        ByteBuffer bbTexture = ByteBuffer.allocateDirect(DATA.CUBE_TEXTURE.length * 4);
        bbTexture.order(ByteOrder.nativeOrder());
        mCubeTextureCoord = bbTexture.asFloatBuffer();
        mCubeTextureCoord.put(DATA.CUBE_TEXTURE);
        mCubeTextureCoord.position(0);

        /* 
         * Make a floor
         */
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(DATA.FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        mFloorVertices = bbFloorVertices.asFloatBuffer();
        mFloorVertices.put(DATA.FLOOR_COORDS);
        mFloorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(DATA.FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        mFloorNormals = bbFloorNormals.asFloatBuffer();
        mFloorNormals.put(DATA.FLOOR_NORMALS);
        mFloorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(DATA.FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        mFloorColors = bbFloorColors.asFloatBuffer();
        mFloorColors.put(DATA.FLOOR_COLORS);
        mFloorColors.position(0);

        // Load the vertex, grid & texture shaders
        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);

        mProgramHandler = ShaderHelper.createAndLinkProgram(vertexShader, gridShader,
                          new String[] {"a_Position",  
                                        "a_Color", 
                                        "a_Normal", 
                                        "a_TexCoordinate"
                                       }
                          );
        /*
         * Enables depth test. For depth test, the depth buffer is used to determine where
         * in the z-axis a fragment is placed.
         */
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        /*
         * Position Cube & Floor
         */
        Matrix.setIdentityM(mModelCube,     // The matrix
                                0               // Offset ??
        );
        
        Matrix.translateM(mModelCube,       // The matrix
                              0,                // Offset ??
                              0,                // x-co-ordinate
                              0,                // y-co-ordinate
                             -mObjectDistance   // z-co-ordinate
        ); // Cube appears -mObjectDistance units behind the user

        Matrix.setIdentityM(mModelFloor,    // The matrix
                                0               // Offset ??
        );
        
        Matrix.translateM(mModelFloor,      // The matrix
                              0,                // Offset ??
                              0,                // x-co-ordinate
                             -mFloorDepth,      // y-co-ordinate
                              0                 // z-co-ordinate
        ); // Floor appears -mFloorDepth units below user
        
        // Load the texture
        mTextureDataHandler = TextureHelper.loadTexture(this, R.drawable.robot);
        mTextureFoundHandler = TextureHelper.loadTexture(this, R.drawable.usb_android);

        checkGLError("onSurfaceCreated");
    }

    /**
     * Converts a raw text file into a string.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        
        /*
         *  Install the specified program as the current rendering state
         */
        GLES20.glUseProgram(mProgramHandler);

        /*
         * Get the location of various uniform variables.
         * 
         * Uniform variables: variables that act as constants for the duration of 
         * a draw call. The u_MVP 4x4 matrix applies to all vertices and not any
         * particular vertex, hence why it is a uniform variable.
         */
        mModelViewProjectionHandler = GLES20.glGetUniformLocation(mProgramHandler,  /* The program to be queried */
                                                                "u_MVP"           /* The name of the uniform variable 
                                                                                     whose location is to be queried  */
        );
        mLightPosHandler = GLES20.glGetUniformLocation(mProgramHandler, "u_LightPos");
        mModelViewHandler = GLES20.glGetUniformLocation(mProgramHandler, "u_MVMatrix");
        mModelHandler = GLES20.glGetUniformLocation(mProgramHandler, "u_Model");
        mIsFloorHandler = GLES20.glGetUniformLocation(mProgramHandler, "u_IsFloor");
        
        /*
         * Build the Model part of the ModelView matrix.
         */
        // Rotates mModelCube in place, by angle TIME_DELTA around point (x, y, z)
        Matrix.rotateM(mModelCube,     // The matrix 
                           0,              // Offset ??
                           TIME_DELTA,     // The angle of rotation
                           0.5f,           // x-co-ordinate
                           0.5f,           // y-co-ordinate
                           1.0f            // z-co-ordinate
        );

        /*
         *  Build the camera matrix and apply it to the ModelView.
         */
        // Defines a viewing transformation in terms of an eye-point, a centre of view
        // and an up-vector
        Matrix.setLookAtM(mCamera,     // The matrix
                              0,           // Offset ??
                              0.0f,        // eye x
                              0.0f,        // eye y
                              CAMERA_Z,    // eye z
                              0.0f,        // C.O.V. x
                              0.0f,        // C.O.V. y
                              0.0f,        // C.O.V. z
                              0.0f,        // up-vector x
                              1.0f,        // up-vector y
                              0.0f         // up-vector z
        );

        headTransform.getHeadView(mHeadView, 0);

        checkGLError("onReadyToDraw");
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye(EyeTransform transform) {
        /*
         * Clears the color buffer and the depth buffer
         * Color Buffer: 
         * Depth Buffer: used to determine where in the 
         *               z-axis a fragment is placed.
         */
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        /*
         * Get the location of an attribute variable.
         * 
         * Attribute variables: variables that represent vertex attributes.
         * Examples of vertex attributes include: position, normal & color.
         */
        mPositionHandler = GLES20.glGetAttribLocation(mProgramHandler, /* The program to be queried */ 
                                                   "a_Position"      /* The name of the attribute */
        );
        mNormalHandler = GLES20.glGetAttribLocation(mProgramHandler, "a_Normal");
        mColorHandler = GLES20.glGetAttribLocation(mProgramHandler, "a_Color");
        mTextureUniformHandler = GLES20.glGetUniformLocation(mProgramHandler, "u_Texture");
        mTextureCoordinateHandler = GLES20.glGetAttribLocation(mProgramHandler, "a_TexCoordinate");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGLError("glActiveTexture");
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandler);
        checkGLError("glBindTexture");
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandler, 0);     
        checkGLError("glUniform1i");

        /*
         * Enable the arrays of different vertex attributes
         */
        GLES20.glEnableVertexAttribArray(mPositionHandler);
        checkGLError("mPositionHandler");
        GLES20.glEnableVertexAttribArray(mNormalHandler);
        checkGLError("mNormalHandler");
        GLES20.glEnableVertexAttribArray(mColorHandler);
        checkGLError("mColorHandler");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandler);
        checkGLError("mTextureCoordinateHandler");

        // Apply the eye transformation to the camera.
        /*
         * Multiplies two 4x4 matrices together and stores the result in a third
         * 4x4 matrix.
         */
        Matrix.multiplyMM(mView,                   // View matrix
                          0,                           // Offset ??
                          transform.getEyeView(),      // ??
                          0,                           // Offset ??
                          mCamera,                     // ??
                          0                            // Offset ??
        );

        // Set the position of the light
        /*
         * Multiplies a 4 element column vector by a 4x4 matrix and stores the result 
         * in a 4 element column vector. 
         */
        Matrix.multiplyMV(mLightPosInEyeSpace,      // 4 element column vector
                          0,                            // Offset ??
                          mView,                        // View matrix
                          0,                            // Offset ??
                          mLightPosInWorldSpace,        // 4 element column vector
                          0                             // Offset ??
        );
        
        /*
         * Specify the value of a uniform variable for the current program object
         */
        GLES20.glUniform3f(mLightPosHandler,          // Uniform variable
                           mLightPosInEyeSpace[0],        // x-coordinate
                           mLightPosInEyeSpace[1],        // y-coordinate
                           mLightPosInEyeSpace[2]     // z-coordinate
         );

        /* 
         * Build the ModelView and ModelViewProjection matrices for calculating cube position 
         * and light.
         */
        Matrix.multiplyMM(mModelView, 
                          0, 
                          mView, 
                          0, 
                          mModelCube, 
                          0
        );
        Matrix.multiplyMM(mModelViewProjection, 
                          0, 
                          transform.getPerspective(), 
                          0, 
                          mModelView, 
                          0
        );
        
        drawCube();
        
        // Set mModelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(mModelView, 0, mView, 0, mModelFloor, 0);
        Matrix.multiplyMM(mModelViewProjection, 0, transform.getPerspective(), 0,
                mModelView, 0);

        drawFloor(transform.getPerspective());
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    /**
     * Draw the cube. We've set all of our transformation matrices. Now we simply pass them into
     * the shader.
     */
    public void drawCube() {

        /*
         * Set the value of mIsFloorParam (a uniform variable to 0)
         * Indicates that the cube is not a floor
         */
        GLES20.glUniform1f(mIsFloorHandler, 0f);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(mModelHandler,       // The location of the uniform value to be modified
                                      1,                   // The number of matrices that are to be modified
                                      false,               /* True if the matrix should be transposed as the 
                                                              values are loaded */
                                      mModelCube,          // ??
                                      0                    // ??
                                  
       );

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(mModelViewHandler, 1, false, mModelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, mCubeVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(mModelViewProjectionHandler, 1, false, mModelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(mNormalHandler, 3, GLES20.GL_FLOAT,
                false, 0, mCubeNormals);
        
        // Set the texture positions of the cube
        GLES20.glVertexAttribPointer(mTextureCoordinateHandler, 2, GLES20.GL_FLOAT,
                false, 0, mCubeTextureCoord);

        if (isLookingAtObject()) {
          GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureFoundHandler);
          checkGLError("glBindTexture");
            GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false,
                    0, mCubeFoundColors);
        } else {
          GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandler);
          checkGLError("glBindTexture");
            GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false,
                    0, mCubeColors);
        }
        
        GLES20.glUniform1i(mTextureUniformHandler, 0);     
        checkGLError("glUniform1i");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        
        checkGLError("Drawing cube");
    }

    /**
     * Draw the floor. This feeds in data for the floor into the shader. Note that this doesn't
     * feed in data about position of the light, so if we rewrite our code to draw the floor first,
     * the lighting might look strange.
     */
    public void drawFloor(float[] perspective) {
        // This is the floor!
        GLES20.glUniform1f(mIsFloorHandler, 1f);

        // Set ModelView, MVP, position, normals, and color
        GLES20.glUniformMatrix4fv(mModelHandler, 1, false, mModelFloor, 0);
        GLES20.glUniformMatrix4fv(mModelViewHandler, 1, false, mModelView, 0);
        GLES20.glUniformMatrix4fv(mModelViewProjectionHandler, 1, false, mModelViewProjection, 0);
        GLES20.glVertexAttribPointer(mPositionHandler, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, mFloorVertices);
        GLES20.glVertexAttribPointer(mNormalHandler, 3, GLES20.GL_FLOAT, false, 0, mFloorNormals);
        GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false, 0, mFloorColors);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        checkGLError("drawing floor");
    }

    /**
     * Increment the score, hide the object, and give feedback if the user pulls the magnet while
     * looking at the object. Otherwise, remind the user what to do.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");
        if (isLookingAtObject()) {
            mScore++;
            mOverlayView.show3DToast("Found it! Look around for another one.\nScore = " + mScore);
            hideObject();
        } else {
            mOverlayView.show3DToast("Look around to find the object!");
        }
        // Always give user feedback
        mVibrator.vibrate(50);
    }

    /**
     * Find a new random position for the object.
     * We'll rotate it around the Y-axis so it's out of sight, and then up or down by a little bit.
     */
    private void hideObject() {
        float[] rotationMatrix = new float[16];
        float[] posVec = new float[4];
        // First rotate in XZ plane, between 90 and 270 deg away, and scale so that we vary
        // the object's distance from the user.
        float angleXZ = (float) Math.random() * 180 + 90;
        Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
        float oldObjectDistance = mObjectDistance;
        mObjectDistance = (float) Math.random() * 15 + 5;
        float objectScalingFactor = mObjectDistance / oldObjectDistance;
        Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor, objectScalingFactor);
        Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, mModelCube, 12);
        // Now get the up or down angle, between -20 and 20 degrees
        float angleY = (float) Math.random() * 80 - 40; // angle in Y plane, between -40 and 40
        angleY = (float) Math.toRadians(angleY);
        float newY = (float)Math.tan(angleY) * mObjectDistance;
        Matrix.setIdentityM(mModelCube, 0);
        Matrix.translateM(mModelCube, 0, posVec[0], newY, posVec[2]);
    }

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     * @return
     */
    private boolean isLookingAtObject() {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];
        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(mModelView, 0, mHeadView, 0, mModelCube, 0);
        Matrix.multiplyMV(objPositionVec, 0, mModelView, 0, initVec, 0);
        float pitch = (float)Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float)Math.atan2(objPositionVec[0], -objPositionVec[2]);
        Log.i(TAG, "Object position: X: " + objPositionVec[0]
                + "  Y: " + objPositionVec[1] + " Z: " + objPositionVec[2]);
        Log.i(TAG, "Object Pitch: " + pitch +"  Yaw: " + yaw);
        return (Math.abs(pitch) < PITCH_LIMIT) && (Math.abs(yaw) < YAW_LIMIT);
    }
    
}
