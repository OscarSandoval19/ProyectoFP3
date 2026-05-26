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


## A2: Detalle Técnico: Implementación del Motor Jerárquico Personalizado (Custom)

### 1. La Estructura del Nodo (`CustomTreeNode<T>`)
Es la entidad fundamental que representa cada carpeta o archivo en la memoria del servidor. Se diseñó utilizando tipos genéricos y contiene tres atributos clave:
* **`NodeDTO content`**: Almacena la información pura del nodo (ID, nombre, tipo).
* **`CustomTreeNode<T> parent`**: Un puntero directo hacia su nodo padre. Esto facilita la navegación ascendente inmediata (por ejemplo, para reconstruir la ruta de una carpeta hacia la raíz).
* **`Map<String, CustomTreeNode<T>> children`**: Un mapa dinámico enlazado que almacena a los hijos directos. Se optó por un mapa en lugar de una lista indexada para permitir búsquedas y accesos a descendientes directos en un tiempo constante de complejidad **O(1)**.

### 2. La Estrategia de Gestión (`CustomTreeStrategy`)
Es la clase que implementa la interfaz `TreeAlgorithmStrategy` y gobierna el ciclo de vida de todo el árbol en memoria:
* **Referencia Raíz (`root`)**: Mantiene el punto de partida del árbol.
* **Índice Global de Búsqueda (`Map<String, CustomTreeNode<T>> index`)**: El motor mantiene un mapa que registra absolutamente todos los nodos cargados en memoria indexados por su ID. 
  * *Impacto en rendimiento:* Cuando el orquestador solicita añadir un hijo o realizar una validación, el motor no necesita recorrer todo el árbol buscando al padre; lo localiza instantáneamente en el índice global en **O(1)** y cuelga el nuevo nodo de forma eficiente.

### 3. Ejecución de Algoritmos sobre la Estructura Custom
Al no usar estructuras nativas de Java tradicionales para las ramas, los algoritmos de recorrido se adaptaron para trabajar directamente sobre los punteros de nuestra clase:
* **DFS (Depth-First Search):** Explora de manera recursiva la estructura del mapa `children`, adentrándose en el nivel más profundo de cada carpeta antes de saltar a la carpeta hermana. Es el utilizado para el borrado en cascada en memoria.
* **BFS (Breadth-First Search):** Utiliza una estructura de cola auxiliar para procesar el árbol de manera horizontal, nivel por nivel, garantizando un indexado limpio.


## A3: Validación Cruzada (Estrategia del Integrante B vs. Motor Custom)

Para garantizar la integridad matemática, la consistencia de los datos y asegurar que el desarrollo de estructuras propias no alterara el comportamiento del sistema, se realizó una fase de **Validación Cruzada**.

### Metodología de la Prueba
1. **Set de Datos Idéntico:** Se cargó en el almacenamiento persistente una estructura jerárquica de prueba compuesta por 1 nodo raíz, 2 nodos hijos (carpetas) y 1 nodo nieto (archivo).
2. **Prueba del Motor Estándar (Integrante B):** Se activó la estrategia basada en colecciones nativas de Java (`app.tree-strategy=collections`). Se ejecutaron las operaciones de recorrido mediante el endpoint `GET /tree/traversal` tanto en modo **DFS** como **BFS**, capturando los JSON resultantes.
3. **Prueba del Motor Custom (Integrante A):** Se modificó la propiedad a `app.tree-strategy=custom`. Sin reiniciar la base de datos, se volvieron a invocar exactamente los mismos endpoints de recorrido con la lógica de punteros propia en memoria.

### Resultados y Conclusiones
* **Coincidencia Estructural:** La comparación binaria de las respuestas (mecanismo *diff*) arrojó una coincidencia del **100%** en el orden de los elementos, las relaciones de parentesco (`parentId`) y los niveles de profundidad.
* **Comportamiento en Cascada:** Al ejecutar una petición `DELETE` sobre un nodo padre utilizando el motor *Custom*, se comprobó mediante Swagger y pgAdmin que el borrado en cascada eliminó correctamente tanto al elemento seleccionado como a sus descendientes en memoria y base de datos, replicando exactamente el comportamiento seguro del motor estándar.

**Conclusión:** Queda certificado que la abstracción del motor *Custom* respeta con absoluta precisión las reglas del álgebra de árboles y grafos acíclicos. El sistema es capaz de alternar entre ambas estrategias de memoria en tiempo de ejecución de manera transparente para el usuario y el Frontend.
