
attribute vec3 a_Position;
attribute vec2 a_TexCoord;
uniform mat4 u_MvpMatrix;
uniform vec3 uKa;
uniform vec3 uKd;
uniform vec3 uKs;

varying vec2 v_TexCoord;

attribute vec3 a_Normal;         //法向量
varying vec4 vDiffuse;          //用于传递给片元着色器的散射光最终强度
varying vec4 vAmbient;          //用于传递给片元着色器的环境光最终强度
varying vec4 vSpecular;          //用于传递给片元着色器的镜面光最终强度

void main(){
    gl_Position = u_MvpMatrix*vec4(a_Position,1);
    v_TexCoord = a_TexCoord;

    vec3 lightLocation=vec3(0.0,-200.0,-500.0);      //光照位置
    vec3 camera=vec3(0,200.0,0);
    float shininess=10.0;             //粗糙度，越小越光滑
    //    vec4 a,d,s;
    //    calculateLight(normalize(a_Normal),a,d,s,camera,pos,vec4(uKa,1.0),vec4(uKd,1.0),vec4(uKs,1.0));
    //    vAmbient=a;
    //    vDiffuse=d;
    //    vSpecular=s;


    vec3 newNormal=normalize((u_MvpMatrix*vec4(a_Normal+a_Position,1)).xyz-(u_MvpMatrix*vec4(a_Position,1)).xyz);
    vec3 vp=normalize(lightLocation-(u_MvpMatrix*vec4(a_Position,1)).xyz);
    vDiffuse=vec4(uKd,1.0)*max(0.0,dot(newNormal,vp));                //计算散射光的最终强度

    vec3 eye= normalize(camera-(u_MvpMatrix*vec4(a_Position,1)).xyz);
    vec3 halfVector=normalize(vp+eye);    //求视线与光线的半向量
    float nDotViewHalfVector=dot(newNormal,halfVector);   //法线与半向量的点积
    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));     //镜面反射光强度因子
    vSpecular=vec4(uKs,1.0)*powerFactor;               //计算镜面光的最终强度

    vAmbient=vec4(uKa,1.0);
}

////定位光光照计算的方法
//void calculateLight(             //定位光光照计算的方法
//vec3 normal,               //法向量
//vec4 vA,
//vec4 vD,
//vec4 vS,
//vec3 camera,
//vec3 lightLocation,        //光源位置
//vec4 lightAmbient,         //环境光强度
//vec4 lightDiffuse,         //散射光强度
//vec4 lightSpecular         //镜面光强度
//){
//    vA=lightAmbient;         //直接得出环境光的最终强度
//    vec3 normalTarget=a_Position+normal;   //计算变换后的法向量
//    vec3 newNormal=(u_MvpMatrix*vec4(normalTarget,1)).xyz-(u_MvpMatrix*vec4(a_Position,1)).xyz;
//    newNormal=normalize(newNormal);   //对法向量规格化
//    //计算从表面点到摄像机的向量
//    vec3 eye= normalize(camera-(u_MvpMatrix*vec4(a_Position,1)).xyz);
//    //计算从表面点到光源位置的向量vp
//    vec3 vp= normalize(lightLocation-(u_MvpMatrix*vec4(a_Position,1)).xyz);
//    vp=normalize(vp);//格式化vp
//    vec3 halfVector=normalize(vp+eye);    //求视线与光线的半向量
//    float shininess=50.0;             //粗糙度，越小越光滑
//    float nDotViewPosition=max(0.0,dot(newNormal,vp));    //求法向量与vp的点积与0的最大值
//    vD=lightDiffuse*nDotViewPosition;                //计算散射光的最终强度
//    float nDotViewHalfVector=dot(newNormal,halfVector);   //法线与半向量的点积
//    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));     //镜面反射光强度因子
//    vS=lightSpecular*powerFactor;               //计算镜面光的最终强度
//}