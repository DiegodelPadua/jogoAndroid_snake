package com.example.composelayouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var telaAtual by remember {
                mutableStateOf("inicio")
            }

            when (telaAtual) {

                // TELA INICIAL
                "inicio" -> {
                    TelaInicial(
                        aoClicarStart = {
                            telaAtual = "jogo"
                        }
                    )
                }

                // JOGO
                "jogo" -> {
                    TelaSnake(
                        aoPerder = {
                            telaAtual = "gameover"
                        }
                    )
                }

                // GAME OVER
                "gameover" -> {
                    TelaGameOver(
                        aoClicarRestart = {
                            telaAtual = "inicio"
                        }
                    )
                }
            }
        }
    }
}


// =====================================================
// TELA INICIAL
// =====================================================

@Composable
fun TelaInicial(
    aoClicarStart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .border(
                width = 5.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(25.dp)
            )
            .padding(15.dp)
    ) {

        // SCORE
        Text(
            text = "SCORE: 0050",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // LIVES
        Text(
            text = "LIVES: ● ● ●",
            color = Color.Green,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd)
        )


        // PERSONAGENS
        Image(
            painter = painterResource(
                R.drawable.android_skate
            ),
            contentDescription = "Personagens Android",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
        )


        // JOGADOR
        Image(
            painter = painterResource(
                R.drawable.ic_launcher_foreground
            ),
            contentDescription = "Jogador",
            modifier = Modifier
                .size(55.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 55.dp)
        )


        // PRESS START
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Color.DarkGray,
                    RoundedCornerShape(3.dp)
                )
                .clickable {
                    aoClicarStart()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "PRESS START",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// =====================================================
// POSIÇÃO DA COBRA
// =====================================================

data class Posicao(
    val x: Int,
    val y: Int
)


// =====================================================
// DIREÇÃO DA COBRA
// =====================================================

enum class Direcao {
    CIMA,
    BAIXO,
    ESQUERDA,
    DIREITA
}


// =====================================================
// JOGO SNAKE
// =====================================================

@Composable
fun TelaSnake(
    aoPerder: () -> Unit
) {

    val colunas = 15
    val linhas = 20
    val tamanhoBloco = 20

    // Corpo da cobra
    val cobra = remember {
        mutableStateListOf(
            Posicao(7, 10),
            Posicao(6, 10),
            Posicao(5, 10)
        )
    }

    // Direção
    var direcao by remember {
        mutableStateOf(Direcao.DIREITA)
    }

    // Comida
    var comida by remember {
        mutableStateOf(Posicao(10, 10))
    }

    // Pontuação
    var pontos by remember {
        mutableIntStateOf(0)
    }


    // =====================================================
    // MOVIMENTO DA COBRA
    // =====================================================

    LaunchedEffect(Unit) {

        while (true) {

            delay(300)

            val cabeca = cobra.first()

            // Calcula a nova posição da cabeça
            val novaCabeca = when (direcao) {

                Direcao.CIMA -> {
                    Posicao(
                        cabeca.x,
                        cabeca.y - 1
                    )
                }

                Direcao.BAIXO -> {
                    Posicao(
                        cabeca.x,
                        cabeca.y + 1
                    )
                }

                Direcao.ESQUERDA -> {
                    Posicao(
                        cabeca.x - 1,
                        cabeca.y
                    )
                }

                Direcao.DIREITA -> {
                    Posicao(
                        cabeca.x + 1,
                        cabeca.y
                    )
                }
            }


            // =================================================
            // COLISÃO COM A PAREDE
            // =================================================

            if (
                novaCabeca.x < 0 ||
                novaCabeca.x >= colunas ||
                novaCabeca.y < 0 ||
                novaCabeca.y >= linhas
            ) {

                aoPerder()
                break
            }


            // =================================================
            // COLISÃO COM O CORPO
            // =================================================

            if (cobra.contains(novaCabeca)) {

                aoPerder()
                break
            }


            // =================================================
            // COMEU A COMIDA
            // =================================================

            if (novaCabeca == comida) {

                // Coloca nova cabeça
                cobra.add(
                    index = 0,
                    element = novaCabeca
                )

                // Aumenta pontuação
                pontos += 10


                // Cria nova comida
                do {

                    comida = Posicao(
                        Random.nextInt(colunas),
                        Random.nextInt(linhas)
                    )

                } while (cobra.contains(comida))

            } else {

                // Movimento normal

                // Adiciona nova cabeça
                cobra.add(
                    index = 0,
                    element = novaCabeca
                )

                // Remove o último bloco
                cobra.removeAt(
                    cobra.lastIndex
                )
            }
        }
    }


    // =====================================================
    // INTERFACE
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "SCORE: $pontos",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )


        // =====================================================
        // TABULEIRO
        // =====================================================

        Box(
            modifier = Modifier
                .width((colunas * tamanhoBloco).dp)
                .height((linhas * tamanhoBloco).dp)
                .border(
                    width = 3.dp,
                    color = Color.Gray
                )
        ) {

            // COBRA
            cobra.forEach { parte ->

                Box(
                    modifier = Modifier
                        .size(tamanhoBloco.dp)
                        .offset(
                            x = (parte.x * tamanhoBloco).dp,
                            y = (parte.y * tamanhoBloco).dp
                        )
                        .background(Color.Green)
                )
            }


            // COMIDA
            Box(
                modifier = Modifier
                    .size(tamanhoBloco.dp)
                    .offset(
                        x = (comida.x * tamanhoBloco).dp,
                        y = (comida.y * tamanhoBloco).dp
                    )
                    .background(Color.Red)
            )
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // =====================================================
        // BOTÃO CIMA
        // =====================================================

        Button(
            onClick = {

                if (direcao != Direcao.BAIXO) {
                    direcao = Direcao.CIMA
                }

            }
        ) {

            Text(
                text = "▲",
                fontSize = 20.sp
            )
        }


        // =====================================================
        // BOTÕES ESQUERDA / BAIXO / DIREITA
        // =====================================================

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ESQUERDA
            Button(
                onClick = {

                    if (direcao != Direcao.DIREITA) {
                        direcao = Direcao.ESQUERDA
                    }

                }
            ) {

                Text(
                    text = "◀",
                    fontSize = 20.sp
                )
            }


            // BAIXO
            Button(
                onClick = {

                    if (direcao != Direcao.CIMA) {
                        direcao = Direcao.BAIXO
                    }

                }
            ) {

                Text(
                    text = "▼",
                    fontSize = 20.sp
                )
            }


            // DIREITA
            Button(
                onClick = {

                    if (direcao != Direcao.ESQUERDA) {
                        direcao = Direcao.DIREITA
                    }

                }
            ) {

                Text(
                    text = "▶",
                    fontSize = 20.sp
                )
            }
        }
    }
}


// =====================================================
// GAME OVER
// =====================================================

@Composable
fun TelaGameOver(
    aoClicarRestart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .border(
                width = 5.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(25.dp)
            )
            .padding(15.dp)
    ) {

        // GAME OVER
        Text(
            text = "GAME OVER",
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )


        // RESTART
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Color.DarkGray,
                    RoundedCornerShape(3.dp)
                )
                .clickable {
                    aoClicarRestart()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "RESTART",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}