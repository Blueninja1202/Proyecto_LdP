# Variables
JC = javac
JVM = java
MAIN = Proyecto
SOURCES = Proyecto.java
CLASSES = Proyecto.class Monitor.class Consumidor.class Productor.class

# Regla por defecto: compilar todo
all: compile

# Regla para compilar
compile:
	$(JC) $(SOURCES)

# Regla para ejecutar (puedes cambiar 'input.txt' por el nombre de tu archivo de prueba)
run: compile
	$(JVM) $(MAIN) input.txt

# Regla para limpiar los archivos .class generados
clean:
	rm -f *.class

# Ayuda
help:
	@echo "Opciones disponibles:"
	@echo "  make         - Compila el proyecto"
	@echo "  make run     - Compila y ejecuta la simulación con input.txt"
	@echo "  make clean   - Elimina los archivos .class"
