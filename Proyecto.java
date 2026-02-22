import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class Monitor {

    private int tablero[][] = new int[6][6];
    private boolean simulador = true;
    private Consumidor vehiculo_recargar;


/*
Constructor del monitor, inicializa el tablero con -1 para indicar que las posiciones están vacías.
*/

    Monitor(int tablero[][]){
        this.tablero = tablero;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
            tablero[i][j] = -1;
            }
        }
    }

/*
Este metodo es de tipo synchronized para asegurar que solo un hilo pueda acceder a él a la vez. Verifica si el vehículo se puede cargar en la posición 
especificada sin colisionar con otros vehículos o salir de los límites del tablero. Si es posible, carga el vehículo en el tablero y devuelve true;
de lo contrario, devuelve false.
*/

    public synchronized boolean carga_inicial(int id, char orientation, int row, int column, int length) {

        // Verificar colisiones y límites del tablero al cargar dicha simulacion

        for (int i = 0; i < length; i++) {
            if(orientation == 'h') {
                if(tablero[row][column + i] != -1 || column + i >= 6) {
                    return false;
                }
            } else {
                if(tablero[row + i][column] != -1 || row + i >= 6) {
                    return false;
                }
            }
        }

        // Si no hay colisiones ni se sale de los límites, cargar el vehículo en el tablero con su ID correspondiente
        
        for (int i = 0; i < length; i++) {
            if(orientation == 'h') {
                tablero[row][column + i] = id;
            } else {
                tablero[row + i][column] = id;
            }
        }

        return true;
    }

    public boolean isSimulador() {
        return simulador;
    }

    public boolean isGoal(Consumidor vehiculo){

        // Verificar si el vehículo con ID 0 ha llegado a la posición de salida (5, 2) para finalizar el simulador.

        if(vehiculo.get_ID() == 0) {
            if(vehiculo.get_orientation() == 'h') {
                if(vehiculo.get_column() + vehiculo.get_length() - 1 >= 5) {
                    return true;
                }
            } else {
                if(vehiculo.get_row() + vehiculo.get_length() - 1 >= 5) {
                    return true;
                }
            }
        }

        return false;
    }

    /* Aqui vemos si es posible Mover el vehiculo */

    public synchronized void MoverVehiculo(Consumidor vehiculo){

        //Para poder moverse ferificamos que el vehiculo tenga bateria, si no la tiene, 
        // se queda esperando a que el cargador lo recargue, 
        // si el simulador ya terminó, se sale del método para evitar quedarse esperando indefinidamente.

        while(vehiculo.get_baterry() == 0 && simulador) {
            try {
                SolicitarRecarga(vehiculo);
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if(!simulador) {
            return;
        }   

        // Intentamos mover el vehículo en ambas direcciones (hacia adelante y hacia atrás). Si el movimiento es posible, se realiza el movimiento.

        if(Intento_de_Mover(vehiculo, 1)){
            Mover(vehiculo, 1);
        }
        else if(Intento_de_Mover(vehiculo, -1)){
            Mover(vehiculo, -1);
        }
        else{ // Si no se puede mover en ninguna dirección, el vehículo se queda esperando a que otro vehículo se mueva y libere espacio.
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Verificar si el vehículo con ID 0 ha llegado a la posición de salida para finalizar el simulador.

        if(isGoal(vehiculo)) {
            simulador = false;
            System.out.println("El vehículo con ID 0 ha llegado a la salida. Simulador finalizado.");
        }

        // Notificar a otros vehículos que pueden intentar moverse ahora que este vehículo ha realizado su movimiento, 
        // lo que podría liberar espacio en el tablero para otros vehículos.

        notifyAll();
    }

    // Este método verifica si el vehículo puede moverse en la dirección especificada sin colisionar con otros vehículos o salir de los límites del tablero.

    public boolean Intento_de_Mover(Consumidor vehiculo, int direction) {

        // Se hace una copia de tablero para verificar el movimiento sin modificar el tablero original, 
        // se eliminan las posiciones del vehículo actual para evitar colisiones consigo mismo.

        int row = vehiculo.get_row();
        int column = vehiculo.get_column();
        int tableroaux[][] = new int[6][6];

         for (int i = 0; i < 6; i++){
                for (int j = 0; j < 6; j++) {
                    tableroaux[i][j] = tablero[i][j];
                    if(tableroaux[i][j] == vehiculo.get_ID()) {
                        tableroaux[i][j] = -1;
                    }
                }
            }

        // Verificar si el movimiento es posible en la dirección especificada, dependiendo de la orientación del vehículo (horizontal o vertical).  

        if(vehiculo.get_orientation() == 'h') {

            if(direction == 1){
                column += 1;

                if(column + vehiculo.get_length() > 6) {
                    return false;
                }

                for (int i = 0; i < vehiculo.get_length(); i++) {
                    if(tableroaux[row][column + i] != -1 || column + i > 6) {
                        return false;
                    }
                }

                return true;

            } else {
                column -= 1;

                if(column < 0) {
                    return false;
                }

                for (int i = 0; i < vehiculo.get_length(); i++) {
                    if(tableroaux[row][column + i] != -1 || column + i < 0) {
                        return false;
                    }
                }

                return true;

                }
        } else {
                if(direction == 1){
                    row += 1;

                        if(row + vehiculo.get_length() > 6) {
                            return false;
                        }

                    for (int i = 0; i < vehiculo.get_length(); i++) {
                        if(tableroaux[row + i][column] != -1 || row + i > 6) {
                            return false;
                        }
                    }

                    return true;

                } else {
                    row -= 1;

                    if(row < 0) {
                        return false;
                    }

                    for (int i = 0; i < vehiculo.get_length(); i++) {
                        if(tableroaux[row + i][column] != -1 || row + i < 0) {
                            return false;
                        }
                    }
                    return true;
                }

            }
        }


    // Este método realiza el movimiento del vehículo en la dirección especificada, actualizando el tablero con la nueva posición del vehículo.
        
    public void Mover(Consumidor vehiculo, int direction) {

        // Se eliminan las posiciones del vehículo actual para evitar colisiones consigo mismo, 
        // luego se actualizan las posiciones del vehículo en el tablero según la dirección de movimiento.

        int length = vehiculo.get_length();
        int id = vehiculo.get_ID();
        char orientation = vehiculo.get_orientation();
        int battery = vehiculo.get_baterry();

        for (int i = 0; i < 6; i++){
                for (int j = 0; j < 6; j++) {
                    if(tablero[i][j] == id) {
                        tablero[i][j] = -1;
                    }
                }
            }
        if (orientation == 'h') {
        vehiculo.set_column(vehiculo.get_column() + direction);
        } else {
        vehiculo.set_row(vehiculo.get_row() + direction);
        }

        int row = vehiculo.get_row();
        int column = vehiculo.get_column();

        for (int i = 0; i < length; i++) {
            if(orientation == 'h') {
                battery -= 1;
                tablero[row][column + i] = id;
                

            } else {
                battery -= 1;
                tablero[row + i][column] = id;

            }
        }
        vehiculo.set_battery(battery);
        imprimir_tablero();

    }    

    // Este método se llama cuando un vehículo necesita recargar su batería. 
    // El vehículo se asigna a la variable vehiculo_recargar y el hilo del productor (cargador) es notificado para que pueda recargar el vehículo.

    public synchronized void SolicitarRecarga(Consumidor vehiculo) {
        this.vehiculo_recargar = vehiculo;
        notifyAll();
    }

    // Este método es llamado por el hilo del productor (cargador) para recargar el vehículo asignado a la variable vehiculo_recargar.

    public synchronized void RecargarVehiculo() {
        try {
            while(vehiculo_recargar == null && simulador) {
                wait();
            }

            if(vehiculo_recargar != null) {
            vehiculo_recargar.set_battery(10);
            vehiculo_recargar = null;
            notifyAll();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }


    public void imprimir_tablero() {

        System.out.println("---------------------");
        for (int i = 0; i < 6; i++) {
                System.out.print("| ");
            for (int j = 0; j < 6; j++) {
                if(tablero[i][j] == -1) {
                    System.out.print(" | ");
                } else {
                System.out.print(tablero[i][j] + " | ");
            }
            
        }
        System.out.println();
            System.out.println("---------------------");
        System.out.println();
    }

    }
}


class Productor extends Thread {
    Monitor monitor;

    public Productor(Monitor monitor) {
        this.monitor = monitor;
    }

    public void run() {
        while(monitor.isSimulador()) {
            monitor.RecargarVehiculo();
        }

    }
}

class Consumidor extends Thread {

    // Atributos para el consumidor (Vehiculo)
    private Monitor monitor;
    private int ID;
    private char orientation;
    private int row;
    private int column;
    private int length;
    private int battery;

    public Consumidor(Monitor monitor, int ID, char orientation, int row, int column, int length, int battery) {
        this.monitor = monitor;
        this.ID = ID;
        this.orientation = orientation;
        this.row = row;
        this.column = column;
        this.length = length;
        this.battery = battery;
    }

    public void run() {

        while(monitor.isSimulador()) {
            monitor.MoverVehiculo(this);
            try {
                Thread.sleep(800); // Simula el tiempo que tarda el vehículo en intentar moverse
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public void imprimirDatos() {
        System.out.println("ID: " + ID);
        System.out.println("Orientation: " + orientation);
        System.out.println("Length: " + length);
        System.out.println("Row: " + row);
        System.out.println("Column: " + column);
        System.out.println("Battery: " + battery);
    }

    public boolean vehiculo_cargar() {
        return monitor.carga_inicial(ID, orientation, row, column, length);
    }

    public int get_baterry() {
        return battery;
    }

    public int get_ID() {
        return ID;
    }

    public int get_row() {
        return row;
    }

    public int get_column() {
        return column;
    }

     public char get_orientation() {
        return orientation;
    }

     public int get_length() {
        return length;
     }
    
    public void set_row(int row) {
        this.row = row;
    }

    public void set_column(int column) {
        this.column = column;
    }

    public void set_battery(int battery) {
        this.battery = battery;
    }


}
public class Proyecto {
public static void main(String args[] ) throws FileNotFoundException {

    String archivo = args[0];

    File file = new File(archivo);
    Scanner sc = new Scanner(file);

    int Cantidad_de_Vehiculos = 0;
    int Cantidad_de_Cargadores = 0;

    while(sc.hasNextLine()) {
        String line = sc.nextLine();

        if(line.startsWith("Cargadores")){ 
            String[] parts = line.split(", ");
            Cantidad_de_Cargadores = Integer.parseInt(parts[1]);
            System.out.println("Número de cargadores: " + Cantidad_de_Cargadores);
        } else{
            
        Cantidad_de_Vehiculos++;
        }
    }

    int tablero[][] = new int[6][6];
    Monitor estacionamiento = new Monitor(tablero);
    System.out.println("Hola Mundo");
    System.out.println("Cantidad de Vehiculos: " + Cantidad_de_Vehiculos);

    Consumidor[] vehiculos = new Consumidor[Cantidad_de_Vehiculos];

    int i = 0;
    Scanner sc1 = new Scanner(file);
    while(i < Cantidad_de_Vehiculos) {
        String line = sc1.nextLine();

        if(line.startsWith("Cargadores")){ 
            String[] parts = line.split(", ");
            Cantidad_de_Cargadores = Integer.parseInt(parts[1]);
            System.out.println("Número de cargadores: " + Cantidad_de_Cargadores);
        } else{
        String[] parts = line.split(", ");
        int ID = Integer.parseInt(parts[0]);
        char orientation = parts[1].charAt(0);
        int row = Integer.parseInt(parts[2]);
        int column = Integer.parseInt(parts[3]);
        int length = Integer.parseInt(parts[4]);
        int battery = Integer.parseInt(parts[5]);
        vehiculos[i] = new Consumidor(estacionamiento, ID, orientation, row, column, length, battery);
        if(!vehiculos[i].vehiculo_cargar()) {
            System.out.println("Error al cargar el vehículo con ID: " + ID);
            System.exit(1);
        }
        estacionamiento.imprimir_tablero();
        i++;
        }
    }
    i = 0;

    while(i < Cantidad_de_Vehiculos) {
        vehiculos[i].imprimirDatos();
        i++;
    }

    for(i = 0; i < Cantidad_de_Vehiculos; i++) {
        vehiculos[i].start();
    }

    for(i = 0; i < Cantidad_de_Cargadores; i++) {
        Productor cargador = new Productor(estacionamiento);
        cargador.start();
    }   
}

}

