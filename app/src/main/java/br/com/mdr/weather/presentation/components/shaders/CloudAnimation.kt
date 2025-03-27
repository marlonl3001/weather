package br.com.mdr.weather.presentation.components.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import br.com.mdr.weather.core.domain.model.WeatherCondition

val cloudShader = """
    uniform shader composable;
    uniform float2 resolution;
    uniform float time;
    uniform float cloudDensity;
    uniform float dayTimeFactor; // Fator para controlar o ciclo dia/noite
    uniform float cloudDark;
    uniform float cloudCover;
    
    const float cloudscale = 1.1;
    const float speed = 0.003;
    const float cloudlight = 0.3;
    const float cloudalpha = 8.0;
    const float skytint = 0.5; // Controle do tom do céu
    
    const float3 daySkyColour1 = float3(0.2, 0.4, 0.6);
    const float3 daySkyColour2 = float3(0.4, 0.7, 1.0);
    const float3 nightSkyColour1 = float3(0.02, 0.04, 0.1);
    const float3 nightSkyColour2 = float3(0.1, 0.1, 0.2);
    const float4 daySkyColourT1 = float4(daySkyColour1, 0.0);
    const float4 daySkyColourT2 = float4(daySkyColour2, 0.0);
    const float4 nightSkyColourT1 = float4(nightSkyColour1, 0.0);
    const float4 nightSkyColourT2 = float4(nightSkyColour2, 0.0);
    
    const float2x2 m = float2x2(1.6, 1.2, -1.2, 1.6);
    
    float2 hash(float2 p) {
        p = float2(dot(p, float2(127.1, 311.7)), dot(p, float2(269.5, 183.3)));
        return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
    }
    
    float noise(float2 p) {
        const float K1 = 0.366025404; // (sqrt(3)-1)/2
        const float K2 = 0.211324865; // (3-sqrt(3))/6
        float2 i = floor(p + (p.x + p.y) * K1);
        float2 a = p - i + (i.x + i.y) * K2;
        float2 o = (a.x > a.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
        float2 b = a - o + K2;
        float2 c = a - 1.0 + 2.0 * K2;
        float3 h = max(0.5 - float3(dot(a, a), dot(b, b), dot(c, c)), 0.0);
        float3 n = h * h * h * h * float3(dot(a, hash(i + 0.0)), dot(b, hash(i + o)), dot(c, hash(i + 1.0)));
        return dot(n, float3(70.0));
    }
    
    float fbm(float2 n) {
        float total = 0.0, amplitude = 0.1;
        for (int i = 0; i < 7; i++) {
            total += noise(n) * amplitude;
            n = m * n;
            amplitude *= 0.4;
        }
        return total;
    }
    
    float4 main(float2 fragCoord) {
        float2 p = fragCoord / resolution;
        float2 uv = p * float2(resolution.x / resolution.y, 1.0);
        float t = time * speed;
        float q = fbm(uv * cloudscale * 0.5);

        float r = 0.0;
        uv *= cloudscale;
        uv -= q - t;
        float weight = 0.8;
        for (int i = 0; i < 8; i++) {
            r += abs(weight * noise(uv));
            uv = m * uv + t;
            weight *= 0.7;
        }

        float f = 0.0;
        uv = (p - 0.5) * float2(resolution.x / resolution.y, 1.0) * 2.0; // Expande para cobrir a tela

        uv *= cloudscale;
        uv -= q - t;
        weight = 0.7;
        for (int i = 0; i < 8; i++) {
            f += weight * noise(uv);
            uv = m * uv + t;
            weight *= 0.6;
        }

        f *= r + f;

        float c = 0.0;
        t = time * speed * 2.0;
        uv = p * float2(resolution.x / resolution.y, 1.0);
        uv *= cloudscale * 2.0;
        uv -= q - t;
        weight = 0.4;
        for (int i = 0; i < 7; i++) {
            c += weight * noise(uv);
            uv = m * uv + t;
            weight *= 0.6;
        }

        float c1 = 0.0;
        t = time * speed * 3.0;
        uv = p * float2(resolution.x / resolution.y, 1.0);
        uv *= cloudscale * 3.0;
        uv -= q - t;
        weight = 0.4;
        for (int i = 0; i < 7; i++) {
            c1 += abs(weight * noise(uv));
            uv = m * uv + t;
            weight *= 0.6;
        }

        c += c1;

        // Mistura cores do céu para dia e noite
        float4 skycolourDay = mix(daySkyColourT2, daySkyColourT1, p.y);
        float4 skycolourNight = mix(nightSkyColourT2, nightSkyColourT1, p.y);
        float4 skycolour = mix(skycolourNight, skycolourDay, dayTimeFactor);

        // Ajuste das nuvens para dia e noite
        //float4 cloudcolour = float4(1.1, 1.1, 0.9, 1.0) * clamp((cloudDark + cloudlight * c), 0.0, 1.0);
        float brightness = clamp((cloudDark + cloudlight * c), 0.0, 1.0); // Brilho baseado em cloudDark e luz
        float opacity = clamp(f, 0.0, 1.0); // Opacidade baseada em densidade (f)
        float4 cloudcolour = float4(brightness, brightness, brightness, opacity);

        // Controle do alpha baseado na densidade
        f = clamp(cloudDensity * (cloudCover + cloudalpha * f * r), 0.0, 1.0);
        float cloudAlpha = clamp(f, 0.0, 1.0);

        // Mistura final de céu e nuvens com alpha das nuvens
        //float4 result = mix(skycolour, clamp(skytint * skycolour + cloudcolour, 0.0, 1.0), cloudAlpha);
        float4 result = mix(skycolour, clamp(skytint * skycolour + cloudcolour, 0.0, 1.0), f);

        
        return result; // Retorne diretamente o float4 com alpha ajustado
    }
""".trimIndent()

@Composable
fun CloudAnimation(weatherCondition: WeatherCondition) {
    val shader = RuntimeShader(cloudShader)

    shader.setFloatUniform("cloudDensity", weatherCondition.cloudDensity)
    shader.setFloatUniform("dayTimeFactor", weatherCondition.dayTimeFactor)
    shader.setFloatUniform("cloudCover", weatherCondition.cloudCover)
    shader.setFloatUniform("cloudDark", weatherCondition.cloudDark)

    val startTime = remember { System.nanoTime() } // Tempo inicial
    var currentTime by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()
    val timeMs = remember { mutableStateOf(0f) }

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