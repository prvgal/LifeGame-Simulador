# El Juego de la Vida | Simulador

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


Simulación del autómata celular de Conway en Java Swing, con gráficas
de población viva y de huecos en tiempo real. Este simulador es capaz de simular cualquier cosa que sea computable. 

## Configuraciones iniciales
- Aleatorio
- Islas de bacterias
- Cañones de planeadores (Gosper Glider Gun)

## Compilar y ejecutar
```bash
javac -d out $(find src -name "*.java")
java -cp out lifesim.Main
```


## 📄 Licencia

Este proyecto se distribuye bajo la licencia **GNU General Public License v3.0 (GPLv3)**. 

Cualquier persona es libre de usar, modificar y redistribuir este código, siempre que mantenga la misma licencia y libere el código fuente de las modificaciones o trabajos derivados. Consulta el archivo [LICENSE](LICENSE) para conocer todos los detalles.
