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

# Regla para limpiar los archivos .class generados
clean:
	rm -f *.class
