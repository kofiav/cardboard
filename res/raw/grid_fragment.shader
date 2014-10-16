precision highp float;

uniform sampler2D u_Texture;
uniform vec3 u_LightPos;

varying vec4 v_Color;
varying vec3 v_Grid;
varying float v_isFloor;
varying vec2 v_TexCoordinate;
varying vec4 v_Position;
varying vec3 v_Normal;


void main() {
    float depth = gl_FragCoord.z / gl_FragCoord.w; // calculate world-space distance

    if (v_isFloor > 0.5) {
        if ((mod(abs(v_Grid[0]), 10.0) < 0.1) || (mod(abs(v_Grid[2]), 10.0) < 0.1)) {
            gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)
                    + min(1.0, depth / 90.0) * v_Color;
        } else {
            gl_FragColor = v_Color;
        }
    } else {
	    // Will be used for attenuation.
	    float distance = length(u_LightPos - v_Position);
	 
	    // Get a lighting direction vector from the light to the vertex.
	    vec3 lightVector = normalize(u_LightPos - v_Position);
	 
	    // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	    // pointing in the same direction then it will get max illumination.
	    float diffuse = max(dot(v_Normal, lightVector), 0.0);
	 
	    // Add attenuation.
	    diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));
	 
	    // Add ambient lighting
	    diffuse = diffuse + 0.3;
	 
	    // Multiply the color by the diffuse illumination level and texture value to get final output color.
	    //gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));  
	    gl_FragColor = diffuse * texture2D(u_Texture, v_TexCoordinate);    
	 }
}