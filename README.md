# DuoCash

Tus gastos en dos monedas, sin enredos. Una app de [lacosta.agency](https://lacosta.agency).

App Android nativa (Kotlin + Jetpack Compose). Los datos se guardan en el teléfono.

## Abrir el proyecto

1. Instala [Android Studio](https://developer.android.com/studio) (Ladybug o más nuevo).
2. `File > Open` y elige esta carpeta. Android Studio descarga Gradle y las dependencias solo.
3. Conecta tu teléfono con la depuración USB activa, o crea un emulador, y dale a **Run**.

Pruebas de la lógica de dinero: `./gradlew test`.

## Estructura

```
app/src/main/java/agency/lacosta/duocash/
  domain/     monedas, montos, conversión, totales del mes (sin Android, con pruebas)
  data/       base de datos Room y ajustes con DataStore
  ui/
    theme/    colores, tipografías y ola de lacosta.agency
    home/     inicio: balance del mes y movimientos
    movement/ anotar o editar un gasto o ingreso
    settings/ monedas, tasa propia y tema
app/src/main/res/values/      textos en español (idioma por defecto)
app/src/main/res/values-en/   textos en inglés
```

## Decisiones

- Cada movimiento guarda la tasa del momento en que se anotó y su equivalente. Así un gasto de ayer no cambia de
  valor cuando la tasa sube hoy.
- Los montos se guardan como enteros en la unidad mínima de cada moneda (centavos) para no perder precisión.
- Tipografías Bricolage Grotesque e Instrument Sans incluidas en `res/font` (licencia OFL, ver `licenses/`).

## Plan de la v1

1. **Esqueleto** (esta versión): tema La Costa, base de datos, anotar, editar y listar gastos e ingresos, tasa propia.
2. Tasas automáticas (BCV, paralelo, Binance P2P, dólar blue y demás).
3. Pro: calculadora y widget, presupuestos, deudas y préstamos.
4. Cobro único con Google Play y anuncios en la versión gratis.
5. Respaldo opcional en Google Drive.
6. Ícono final, capturas y ficha de Google Play.
