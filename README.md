# Proyecto_LdP - Luis Hernández 29.661.835

Para este **proyecto** se **pensó** en una estrategia para poder implementar el modelo Monitor de una manera sencilla.

Para ello podemos decir que el Monitor (estacionamiento) es el encargado de administrar el uso de los recursos **críticos** y banderas para dicho proyecto; entre los recursos que gestiona se encuentran:
* **Tablero**: este es el recurso **crítico**.
* **Simulador**: esta bandera ayuda a controlar todos los hilos dentro de la **simulación**; la misma acaba cuando el **vehículo** ID 0 llega al final.
* **Variable de tipo Consumidor**: esta representa el vehículo
* **Variable de tipo Productor**: esta representa a los cargadores

# Métodos Importantes

Para realizar este proyecto se **apoyó** en la idea de simplificar la **situación**, por lo cual se **intentó** minimizar las variables **synchronized**. Se definieron 4 métodos sincronizados y otras variables no sincronizadas que ayudan a que la **simulación** siga su curso.

## FUNCIONES SYNCHRONIZED
* `carga_inicial(id, orientation, row, column, length)`
* `MoverVehiculo(Consumidor)`
* `SolicitarRecarga(Consumidor)`
* `RecargarVehiculo()`

## FUNCIONES NO SYNCHRONIZED

### MONITOR
* `isSimulador()`
* `isGoal()`
* `Intento_de_Mover(Consumidor, dirección)`
* `Mover(Consumidor, dirección)`
* `imprimir_tablero()`

### CONSUMIDOR
* `public boolean vehiculo_cargar()`
* `public int get_battery()`
* `public int get_ID()`
* `public int get_row()`
* `public int get_column()`
* `public char get_orientation()`
* `public int get_length()`
* `public void set_row(int row)`
* `public void set_column(int column)`
* `public void set_battery(int battery)`

# Estrategia y Línea de Vida

La estrategia seguida para el flujo de los hilos es la siguiente:

```text
Consumidor
└── MoverVehiculo(this)
    └── Ver Carga
        ├── Si batería > 0
        │   └── Intento_de_Mover (direcciones según orientación)
        │       ├── Si true: Actualiza tablero -> notifyAll() -> Próximo
        │       └── Si false: Colisión detectada -> wait()
        └── Si batería == 0
            └── SolicitarRecarga() -> wait()
```
```text
Productor
└── Verificar carga
        ├── Si tiene carga
        │   └── wait()
        └── Si no tiene carga
            └── RecargarVehiculo() -> notifyAll()
```
# Instrucciones de Uso

Para utilizar el programa se debe considerar lo siguiente:

* Se debe pasar un archivo .txt por parámetro en el main.

* El archivo .txt deberá tener un formato donde, tras cada coma (,), haya un espacio; por ejemplo: 0, h, 2, 3, 3, 10.

# Casos de Prueba
Se suministran 6 casos de prueba. Los primeros 4 se ejecutan de manera exitosa, mientras que los últimos 2 están diseñados para fallar en el inicio.

Explicación de caso5.txt y caso6.txt
Estos casos están pensados para generar errores específicos:

* caso5.txt: Presenta un solapamiento al inicializar el tablero.

* caso6.txt: Contiene una orientación errada para los vehículos.

# Correr el proyecto

Utilizamos un archivo Makefile para que no haya ningun tipo de problema a la hora de compilar el proyecto, para poder usarlo unicamente se tiene que seguir los siguientes pasos:

* Primero, se habre la terminal de linux y se hace **make**
* Segundo, una vez compilado la ejecucion viene dada por **java Proyecto nombreDelArchivo.txt**
