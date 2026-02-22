# Proyecto_LdP - Luis Hernandez 29.661.835

Para este proyeco se penso en una estrategia para poder implementar el modelo Monitor de una manera sencilla

Para ello podemos decir que el Monitor (estacionamiento) es el encargado de administrar el uso de los recursos criticos y banderas para dicho proyecto, entre lo que podemos obtener como recursos son:
  - Tablero, este es el recurso critico
  - Simulador, esta bandera lo que me ayuda hacer es controlar todos los hilos dentro de la simulacion, esta misma acaba cuando el vehiculo ID 0 llega al final
  - Contamos con una variable de tipo Consumidor el cual hace que ese vehiculo en particular se pueda recargar

# Metodos Importantes

Para realizar este proyecto se apoyo en una idea de simplificar la situacion, por lo cual se intento en minimizar las variables syncronized en la cual se tiene 4 y ademas tiene otras variables no de tipo syncronized pero las cuales ayuda a que la simulacion siga su curso sin necesidad de sincronizar

  # FUNCIONES SYNCRONIZED
    - carga_inicial(id, orientation, row, column, lenght)
    - MoverVehiculo(Consumidor)
    - SolicitarRecarga(Consumidor)
    - RecargarVehiculo()

  # FUNCIONES NO SYNCRONIZED
    MONITOR
      - isSimulador()
      - isGoal()
      - Intento_de_Mover(Consumidor, direccion)
      - Mover(Consumidor, direccion)
      - imprimir_tablero()
    
      CONSUMIDOR
        - public boolean vehiculo_cargar()
        - public int get_baterry()
        - public int get_ID()
        - public int get_row()
        - public int get_column()
        - public char get_orientation()
        - public int get_length()
        - public void set_row(int row)
        - public void set_column(int column)
        - public void set_battery(int battery)

Ahora bien, la estrategia que se siguio o la linea de vida que se tiene del proyecto es:

```text
Consumidor
└── MoverVehiculo(this)
    └── Ver Carga
        ├── Si bateria > 0
        │   └── Intento_de_Mover (direcciones según orientación)
        │       ├── Si true: Actualiza tablero -> notifyAll() -> Proximo
        │       └── Si false: Colisión detectada -> wait()
        └── Si bateria == 0
            └── SolicitarRecarga() -> wait()
```
```text
Productor
└── Verificar carga
        ├── Si tiene carga
        │   └── wait()
        └── Si no tiene carga
            └── RecargarVehiculo() -> notifyall()
```
Y ya con esa linea de vida se consiguio que el proyecto sea posible

Ahora bien, a la hora de empezar a utilizar el programa tenemos que tener lo siguiente:
  - Un .txt que se pasara por parametro en el main
  - El .txt debera tener un formato en el cual por cada ',' debe de haber un espacio, ejemplo: 0, h, 2, 3, 3, 10
