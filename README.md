# El Juego de la Vida

Simulación del autómata celular de Conway en Java Swing, con gráficas
de población viva y de huecos en tiempo real.

## Configuraciones iniciales
- Aleatorio
- Islas de bacterias
- Cañones de planeadores (Gosper Glider Gun)

## Compilar y ejecutar
```bash
javac -d out $(find src -name "*.java")
java -cp out lifesim.Main
```