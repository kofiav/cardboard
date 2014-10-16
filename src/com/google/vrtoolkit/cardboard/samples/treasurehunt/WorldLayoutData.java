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

/**
 * Created by cjr on 6/18/14.
 */
public final class WorldLayoutData {

    public static final float[] CUBE_FOUND_COLORS = new float[] {
            // front, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // right, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // back, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // left, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // top, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // bottom, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
    };
    
	// Define points for a cube.		
	
	// X, Y, Z
	final float[] CUBE_COORDS =
	{
			// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
			// if the points are counter-clockwise we are looking at the "front". If not we are looking at
			// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
			// usually represent the backside of an object and aren't visible anyways.
			
			// Front face
			-1.0f, 1.0f, 1.0f,				
			-1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 
			-1.0f, -1.0f, 1.0f, 				
			1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			
			// Right face
			1.0f, 1.0f, 1.0f,				
			1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, 1.0f,				
			1.0f, -1.0f, -1.0f,
			1.0f, 1.0f, -1.0f,
			
			// Back face
			1.0f, 1.0f, -1.0f,				
			1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,				
			-1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			
			// Left face
			-1.0f, 1.0f, -1.0f,				
			-1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, 1.0f, 
			-1.0f, -1.0f, -1.0f,				
			-1.0f, -1.0f, 1.0f, 
			-1.0f, 1.0f, 1.0f, 
			
			// Top face
			-1.0f, 1.0f, -1.0f,				
			-1.0f, 1.0f, 1.0f, 
			1.0f, 1.0f, -1.0f, 
			-1.0f, 1.0f, 1.0f, 				
			1.0f, 1.0f, 1.0f, 
			1.0f, 1.0f, -1.0f,
			
			// Bottom face
			1.0f, -1.0f, -1.0f,				
			1.0f, -1.0f, 1.0f, 
			-1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, 1.0f, 				
			-1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, -1.0f,
	};	
	
	// R, G, B, A
	final float[] CUBE_COLORS =
	{				
			// Front face (red)
			1.0f, 0.0f, 0.0f, 1.0f,				
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,				
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			
			// Right face (green)
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			
			// Back face (blue)
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			
			// Left face (yellow)
			1.0f, 1.0f, 0.0f, 1.0f,				
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,				
			1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			
			// Top face (cyan)
			0.0f, 1.0f, 1.0f, 1.0f,				
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,				
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			
			// Bottom face (magenta)
			1.0f, 0.0f, 1.0f, 1.0f,				
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,				
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f
	};
	
	// X, Y, Z
	// The normal is used in light calculations and is a vector which points
	// orthogonal to the plane of the surface. For a cube model, the normals
	// should be orthogonal to the points of each face.
	final float[] CUBE_NORMALS =
	{												
			// Front face
			0.0f, 0.0f, 1.0f,				
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,				
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			
			// Right face 
			1.0f, 0.0f, 0.0f,				
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,				
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			
			// Back face 
			0.0f, 0.0f, -1.0f,				
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,				
			0.0f, 0.0f, -1.0f,
			0.0f, 0.0f, -1.0f,
			
			// Left face 
			-1.0f, 0.0f, 0.0f,				
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,				
			-1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f,
			
			// Top face 
			0.0f, 1.0f, 0.0f,			
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,				
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			
			// Bottom face 
			0.0f, -1.0f, 0.0f,			
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,				
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f
	};
	
	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[] CUBE_TEXTURE =
	{												
			// Front face
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,				
			
			// Right face 
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,	
			
			// Back face 
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,	
			
			// Left face 
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,	
			
			// Top face 
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,	
			
			// Bottom face 
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f
	};

    public static final float[] FLOOR_COORDS = new float[] {
            200f, 0, -200f,
            -200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, 200f,
    };

    public static final float[] FLOOR_NORMALS = new float[] {
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };

    public static final float[] FLOOR_COLORS = new float[] {
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
    };

}