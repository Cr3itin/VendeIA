package com.vendeia.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.graphics.drawable.GradientDrawable
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class MainActivity : Activity() {

    private val bg = Color.rgb(11, 13, 18)
    private val card = Color.rgb(22, 25, 34)
    private val purple = Color.rgb(124, 92, 252)
    private val white = Color.WHITE
    private val muted = Color.rgb(165, 170, 185)

    private val apiUrl = "https://vendeia-api.onrender.com/ia"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun text(
        s: String,
        size: Float,
        color: Int = white
    ): TextView =
        TextView(this).apply {
            text = s
            textSize = size
            setTextColor(color)
            setPadding(0, 8, 0, 8)
        }

    private fun box(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            background = GradientDrawable().apply {
                setColor(card)
                cornerRadius = 22f
            }
        }

    private fun button(
        label: String,
        action: () -> Unit
    ): Button =
        Button(this).apply {
            text = label
            setTextColor(white)
            setOnClickListener {
                action()
            }
        }

    private fun showDashboard() {

        val scroll = ScrollView(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 24)
            setBackgroundColor(bg)
        }

        root.addView(
            text(
                "VendeIA",
                28f
            )
        )

        root.addView(
            text(
                "Seu assistente inteligente de vendas",
                15f,
                muted
            )
        )

        val stats = box()

        stats.addView(
            text(
                "R$ 4.280",
                25f
            )
        )

        stats.addView(
            text(
                "Faturamento estimado",
                13f,
                muted
            )
        )

        stats.addView(
            text(
                "R$ 1.742  •  37 pedidos",
                18f
            )
        )

        root.addView(
            stats,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {
                setMargins(0, 18, 0, 12)
            }
        )

        val ai = box()

        ai.addView(
            text(
                "🤖 IA Vendedora",
                20f
            )
        )

        ai.addView(
            text(
                "Digite a mensagem do cliente e eu preparo uma resposta.",
                14f,
                muted
            )
        )

        val input = EditText(this).apply {
            hint = "Ex.: cliente perguntou se tem desconto"
            setTextColor(white)
            setHintTextColor(muted)
        }

        ai.addView(input)

        val result = TextView(this).apply {
            textSize = 15f
            setTextColor(white)
            setPadding(0, 18, 0, 8)
        }

        ai.addView(result)

        ai.addView(
            button("Gerar resposta") {

                val mensagem = input.text.toString().trim()

                if (mensagem.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Digite a mensagem do cliente.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    result.text = "🤖 Pensando..."

                    gerarResposta(
                        mensagem,
                        result
                    )
                }
            }
        )

        root.addView(
            ai,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {
                setMargins(0, 0, 0, 12)
            }
        )

        val products = box()

        products.addView(
            text(
                "📦 Produtos",
                20f
            )
        )

        products.addView(
            text(
                "Placa decorativa MDF  •  R$ 49,90",
                15f
            )
        )

        products.addView(
            text(
                "Quadro personalizado  •  R$ 89,90",
                15f
            )
        )

        products.addView(
            text(
                "Kit presente premium  •  R$ 129,90",
                15f
            )
        )

        products.addView(
            button("Cadastrar produto") {
                showProductForm()
            }
        )

        root.addView(products)

        root.addView(
            text(
                "VendeIA 1.1 • Android",
                12f,
                muted
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(root)

        setContentView(scroll)
    }

    private fun gerarResposta(
        mensagem: String,
        resultado: TextView
    ) {

        Thread {

            try {

                val url = URL(apiUrl)

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                connection.connectTimeout = 15000
                connection.readTimeout = 30000

                connection.doOutput = true

                val json = JSONObject()

                json.put(
                    "mensagem",
                    mensagem
                )

                connection.outputStream.use { output ->

                    output.write(
                        json.toString()
                            .toByteArray(Charsets.UTF_8)
                    )
                }

                val responseCode =
                    connection.responseCode

                val stream =
                    if (responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val responseText =
                    stream.bufferedReader()
                        .use { it.readText() }

                connection.disconnect()

                val respostaJson =
                    JSONObject(responseText)

                val resposta =
                    if (responseCode in 200..299) {
                        respostaJson.optString(
                            "resposta",
                            "A IA não retornou uma resposta."
                        )
                    } else {
                        respostaJson.optString(
                            "erro",
                            "Erro ao conversar com a IA."
                        )
                    }

                runOnUiThread {

                    resultado.text =
                        "🤖 $resposta"
                }

            } catch (e: Exception) {

                runOnUiThread {

                    resultado.text =
                        "❌ Não foi possível conectar à IA.\n\n" +
                        e.message
                }
            }
        }.start()
    }

    private fun showProductForm() {

        val root =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    22,
                    22,
                    22,
                    22
                )

                setBackgroundColor(bg)
            }

        root.addView(
            text(
                "Novo produto",
                26f
            )
        )

        val name =
            EditText(this).apply {

                hint =
                    "Nome do produto"

                setTextColor(white)
                setHintTextColor(muted)
            }

        val price =
            EditText(this).apply {

                hint =
                    "Preço de venda"

                inputType = 2

                setTextColor(white)
                setHintTextColor(muted)
            }

        val cost =
            EditText(this).apply {

                hint =
                    "Custo"

                inputType = 2

                setTextColor(white)
                setHintTextColor(muted)
            }

        val stock =
            EditText(this).apply {

                hint =
                    "Estoque"

                inputType = 2

                setTextColor(white)
                setHintTextColor(muted)
            }

        val desc =
            EditText(this).apply {

                hint =
                    "Descrição"

                setTextColor(white)
                setHintTextColor(muted)
            }

        listOf(
            name,
            price,
            cost,
            stock,
            desc
        ).forEach {

            root.addView(it)
        }

        root.addView(
            button("Salvar produto") {

                Toast.makeText(
                    this,
                    "Produto salvo localmente nesta versão.",
                    Toast.LENGTH_SHORT
                ).show()

                showDashboard()
            }
        )

        root.addView(
            button("Voltar") {

                showDashboard()
            }
        )

        setContentView(root)
    }
}
