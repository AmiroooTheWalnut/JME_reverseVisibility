uniform sampler2D m_TextureA;
uniform sampler2D m_TextureB;
varying vec2 texCoord;

void main() {
    vec4 colorA = texture2D(m_TextureA, texCoord);
    vec4 colorB = texture2D(m_TextureB, texCoord);

    if(colorA.z>0.5 || colorB.z>0.5){
        gl_FragColor = vec4(0.0,0.0,1.0,1.0);
    }else{
        gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    }
}