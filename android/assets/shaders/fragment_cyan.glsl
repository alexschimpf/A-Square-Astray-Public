#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoords);

    if ((v_color.r > 0.0 && v_color.g == 0.0 && v_color.b == 0.0) ||
            (v_color.r == 1.0 && v_color.g < 1.0 && v_color.b < 1.0)) {
        gl_FragColor = v_color * vec4(texColor.r, texColor.g, texColor.b, texColor.a);
    } else {
        gl_FragColor = v_color * vec4(0.0, texColor.g, texColor.b, texColor.a);
    }


}
