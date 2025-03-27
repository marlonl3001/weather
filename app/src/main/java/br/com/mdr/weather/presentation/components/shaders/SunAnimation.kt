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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged

val sunShader = """
    uniform shader composable;
    uniform float2 resolution; // Resolução da tela
    uniform float time;        // Tempo para animação
    
    // Função auxiliar para gerar números pseudoaleatórios
    float rand(float x) {
        return fract(sin(x) * 71.5413291);
    }
    
    float rand(float2 x) {
        return rand(dot(x, float2(13.4251, 15.5128)));
    }
    
    float noise(float2 x) {
        float2 i = floor(x);
        float2 f = x - i;
        f *= f * (3.0 - 2.0 * f);
        return mix(
            mix(rand(i), rand(i + float2(1.0, 0.0)), f.x),
            mix(rand(i + float2(0.0, 1.0)), rand(i + float2(1.0, 1.0)), f.x), 
            f.y
        );
    }
    
    float fbm(float2 x) {
        float r = 0.0, s = 1.0, w = 1.0;
        for (int i = 0; i < 5; i++) {
            s *= 2.0;
            w *= 0.5;
            r += w * noise(s * x);
        }
        return r;
    }
    
    // Renderiza o Sol
    float3 render(float2 uv) {
        // Posição do Sol
        float2 spos = uv - float2(0, -0.4); // Ajustar a posição inicial
        float sun = exp(-1000.0 * dot(spos, spos)); // Efeito de brilho
        float3 scol = float3(255.0 / 255.0, 155.0 / 255.0, 102.0 / 255.0) * sun * 10.0; // Cor do Sol
    
        return scol;
    }
    
    // Função principal
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / resolution; // Coordenadas normalizadas
        uv.x += time / 1500.0 - 0.8; // Movimento horizontal
        uv.y -= time / 1500.0 + 0.48; // Movimento vertical
        uv.x *= resolution.x / resolution.y; // Ajuste de proporção
    
        float3 color = render(uv); // Calcula a cor do Sol
        return half4(color, 1.0);  // Define o RGBA do fragmento
    }
    """.trimIndent()

@Composable
fun SunAnimation() {
    val shader = RuntimeShader(sunShader)

    val startTime = remember { System.nanoTime() } // Tempo inicial
    var currentTime by remember { mutableFloatStateOf(0f) }

    val scope = rememberCoroutineScope()
    val timeMs = remember { mutableFloatStateOf(0f) }

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