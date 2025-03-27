package br.com.mdr.weather.presentation.components.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview

val moonShader = """
    uniform shader composable;
uniform float time;
uniform float2 resolution;

const float CLOUDS_SMOOTHNESS = 1.5;
const float LIGHT_INTENSITY = 20.0;
const float ABSORPTION = 5.5;
const float VOLUME_STEPS = 40.0;
const float PI = 3.141592;

float3 hash33(float3 p) {
    return fract(sin(float3(
        dot(p, float3(127.1, 311.7, 74.7)),
        dot(p, float3(269.5, 183.3, 246.1)),
        dot(p, float3(113.5, 271.9, 124.6))
    )) * 43758.5453123);
}

float noise(float3 p) {
    float3 ip = floor(p);
    float3 fp = fract(p);
    fp = fp * fp * (3.0 - 2.0 * fp);
    
    float2 tap = ip.xy + float2(37.0, 17.0) * ip.z + fp.xy;
    vec2 rz = texture(composable, (tap + 0.5) / 256.0).yx;
    return mix(rz.x, rz.y, fp.z);
}

float fbm(float3 x) {   
    float rz = 0.0;
    float a = 0.35;
    for (int i = 0; i < 4; i++) {
        rz += noise(x) * a;
        a *= 0.35;
        x *= 4.0;
    }
    return rz - 0.25;
}

float2 boxIntersection(float3 ro, float3 rd) {
    float3 rad = float3(6.0, 6.0, 2.0);
    float3 m = 1.0 / rd;
    float3 n = m * ro;
    float3 k = abs(m) * rad;
    float3 t1 = -n - k;
    float3 t2 = -n + k;
    float tN = max(max(t1.x, t1.y), t1.z);
    float tF = min(min(t2.x, t2.y), t2.z);
    if (tN > tF || tF < 0.0) return float2(-1.0);
    return float2(tN, tF);
}

void marchVolume(float3 ro, float3 rd, float near, float far, inout float3 color) {    
    float3 vColor = float3(0.0);
    float visibility = 1.0;
    float inside = far - near + hash33(ro).x * 0.01;
    float stepSize = inside / VOLUME_STEPS;
    
    for (float t = near; t <= far; t += stepSize) {
        float3 p = ro + t * rd;
        
        float s = CLOUDS_SMOOTHNESS * 0.01;
        float dens = smoothstep(-s, s, fbm(p + float3(time * 0.055, time * 0.065, 1.0 + time * 0.02))) * 0.1;
        
        float prev = visibility;
        visibility *= exp(-stepSize * dens * ABSORPTION);
        
        float absorption = prev - visibility;
        float light = smoothstep(2.5, 6.5, p.z); // Fake light
        vColor += absorption * dens * light * LIGHT_INTENSITY;
        
        if (visibility < 0.01) break; // Early exit for performance
    }
    
    color = min(vColor, 1.0) + color * visibility;
}

void initCamera(vec2 fragCoord, inout vec3 ro, inout vec3 rd) {
    vec2 uv = (fragCoord - iResolution.xy * 0.5) / iResolution.y;

    // No Android, podemos fixar a posição da câmera ou usar outra lógica
    // Aqui estamos definindo uma posição fixa para o ponto de origem da câmera
    vec2 m = vec2(0.5, 0.5); // Simula um ponto fixo, no centro da tela

    ro = vec3(m.x - 0.5, m.y - 0.5, 0.01) * 0.8;

    vec3 f = normalize(vec3(0.0, 0.0, 1.0) - ro * 0.05),
         r = normalize(cross(vec3(0, 1, 0), f)),
         u = cross(f, r),
         c = ro + f,
         i = c + uv.x * r + uv.y * u;
         
    rd = normalize(i - ro); // direção da câmera
}

half4 main(float2 fragCoord) {
    float3 ro, rd;   
    initCamera(fragCoord, ro, rd);
    
    // Moon rendering
    float moon = dot(rd, float3(0.0, 0.0, 1.0));
    float3 color = (0.7 - fbm(rd * 10.0) * 9.0) * float3(smoothstep(0.995, 0.9955, moon));
    color += (1.25 - color) * pow(moon + 0.1, 6.0) * 0.25;
    
    // Clouds
    float2 hit = boxIntersection(ro - float3(0.0, 0.0, 4.0), rd);
    if (hit.x > 0.0) {
        marchVolume(ro, rd, hit.x, hit.y, color);
    }
    
    return half4(color, 1.0);
}
""".trimIndent()

@Composable
fun MoonAnimation() {
    val shader = RuntimeShader(moonShader)

    val startTime = remember { System.nanoTime() } // Tempo inicial
    var currentTime by remember { mutableFloatStateOf(0f) }

    val scope = rememberCoroutineScope()
    val timeMs = remember { mutableFloatStateOf(0f) }

//    val randomOffset = FloatArray(2).apply {
//        this[0] = Math.random().toFloat() // Random X [0, 1]
//        this[1] = Math.random().toFloat() // Random Y [0, 1]
//    }
    //shader.setFloatUniform("randomOffset", randomOffset[0], randomOffset[1])

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTimeNanos ->
                currentTime = (frameTimeNanos - startTime) / 1_000_000_000f // Converte para segundos
            }
        }
    }

    // Aplica o shader como fundo animado
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                shader.setFloatUniform("resolution", size.width.toFloat(), size.height.toFloat())
            }
            .graphicsLayer {
                clip = true
                shader.setFloatUniform("time", currentTime)
                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(shader, "composable")
                    .asComposeRenderEffect()
            }
    ){}
}

@Preview
@Composable
fun MoonAnimationView() {
    MoonAnimation()
}