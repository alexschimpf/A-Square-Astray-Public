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
    gl_FragColor = v_color * vec4(texColor.r, 0, texColor.b, texColor.a);
}
