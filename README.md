"Proyecto-final" 

https://trello.com/invite/b/6a0209a60a157be99a3ba6e4/ATTI8a5446c7388416e69c8968fe63e884397A5A66AE/semana-1-2

El proyecto consiste en construir un Sistema de Gestión Jerárquica diseñado específicamente para simular un Explorador de Carpetas y Archivos Virtuales.
​El objetivo principal de la aplicación es demostrar cómo separar la lógica matemática de los árboles y grafos (los algoritmos de búsqueda y validación) de la infraestructura visual y de almacenamiento (la página web y las bases de datos).
# ​¿Cómo funciona el proyecto?
​El funcionamiento del sistema se divide en tres capas totalmente independientes que se comunican entre sí:
# ​1. El Motor Lógico (tree-engine)
​Es el "cerebro" del proyecto. No sabe nada de páginas web ni de bases de datos; solo entiende de nodos y estructuras de datos.
​Los Componentes: Define las reglas a través de una interfaz común (TreeAlgorithmStrategy).
​Los Algoritmos: Aquí es donde se ejecutan los recorridos puros en Java: la búsqueda a lo profundo (DFS) para encontrar un archivo, la búsqueda a lo ancho (BFS), la detección de bucles o ciclos (para evitar que una carpeta sea hija de sí misma) y el cálculo de la altura o profundidad del árbol.
# ​2. El Orquestador y Almacenamiento (tree-app - Backend)
​Es el "intermediario" que conecta las peticiones que vienen desde la pantalla del usuario con el motor lógico y los datos reales.
​Persistencia Dinámica: Guarda la información de las carpetas y archivos. Según cómo se configure, puede guardarlos de forma temporal en la memoria de la computadora, en una base de datos relacional tradicional (PostgreSQL) o en una base de datos orientada a documentos (MongoDB).
​El Flujo: Cuando el usuario pide ver el explorador, esta capa extrae los datos de la base de datos, se los pasa al motor para que este ordene el árbol jerárquicamente y luego emite la respuesta final.
# ​3. La Interfaz de Usuario (Frontend)
​Es la pantalla visual que ve el usuario en su navegador web (HTML/CSS/JavaScript).
​La Experiencia: Muestra un formulario para crear nuevos nodos introduciendo un nombre, seleccionando si es una CARPETA (Folder) o un ARCHIVO (File), y eligiendo cuál será su carpeta contenedora (Padre).
​Renderizado: Dibuja de manera visual y recursiva el árbol en la pantalla, aplicando sangrías o indentaciones para que el usuario distinga claramente qué archivos están metidos dentro de qué carpetas.
​Borrado Seguro: Si el usuario presiona el botón de eliminar en una carpeta, la interfaz avisa al backend para que este aplique un borrado en cascada, eliminando esa carpeta y absolutamente todos los archivos y subcarpetas que contenía en su interior.
