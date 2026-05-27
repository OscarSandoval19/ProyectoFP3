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

### A1: Detalle Técnico: Implementación del Motor Basado en Colecciones (Integrante B)

La estrategia `CollectionsTreeStrategy` se desarrolló bajo la premisa de aprovechar al máximo las implementaciones nativas e iterativas del Java Development Kit (JDK), priorizando la estabilidad y el manejo seguro de la memoria en árboles de alta densidad o profundidad extrema.

**1. Almacenamiento Dinámico (Listas Planas)**

A diferencia de los modelos basados en punteros anidados, este motor almacena el estado del árbol en memoria utilizando una estructura intencionalmente aplanada:
* **`ArrayList<NodeDTO> nodes`**: Un vector dinámico estándar que contiene todos los nodos del sistema de manera contigua en memoria.
* **Ventaja Arquitectónica:** Al mantener una lista plana, se evita la fragmentación de la memoria en el "Heap" de Java. Las relaciones jerárquicas no se almacenan como variables físicas dentro de cada objeto, sino que se calculan al vuelo en tiempo de ejecución utilizando el `parentId` de los DTOs, lo que mantiene el consumo de RAM extremadamente bajo y estable.

**2. Ejecución de Algoritmos Seguros (Evitando el StackOverflow)**

Uno de los mayores retos en la teoría de grafos es el desbordamiento de pila (*StackOverflowError*) cuando se procesan árboles excesivamente profundos mediante recursividad pura. Este motor resuelve ese problema delegando los recorridos a estructuras de almacenamiento temporal iterativas:
* **DFS (Depth-First Search) Iterativo:** Se sustituyó la llamada recursiva del sistema por una estructura **Pila (Stack)** explícita utilizando `ArrayDeque<String>`. Al usar los métodos `push()` y `pop()`, el motor desciende hasta las hojas más lejanas almacenando las referencias en la memoria principal (Heap) en lugar de la pila de ejecución, garantizando que el sistema jamás colapse por la profundidad del árbol de carpetas.
* **BFS (Breadth-First Search):** Se implementó mediante el patrón de **Cola (Queue)**, nuevamente utilizando `ArrayDeque<String>` pero operando con los métodos `offer()` y `poll()`. Esto permite barrer la jerarquía de archivos nivel por nivel, de izquierda a derecha.

**3. Trazabilidad Jerárquica y Detección de Colisiones**

Para cumplir con las 11 operaciones obligatorias del motor, se desarrollaron algoritmos específicos para la validación y rastreo de rutas:
* **Reconstrucción de Rutas (`getAncestors` / `getPath`):** Se utilizó la interfaz `Deque` nativa. A medida que el algoritmo salta desde el archivo hijo hacia su padre, utiliza el método `addFirst()` para insertar cada ancestro en la parte superior de la cola. Esto garantiza que la ruta resultante se devuelva matemáticamente ordenada desde la Carpeta Raíz hasta el Archivo final.
* **Detección de Ciclos (`hasCycles`):** Es el mecanismo de defensa crítico del motor. Para evitar la paradoja de que "una carpeta se mueva al interior de sí misma", se implementó un algoritmo DFS híbrido con *Backtracking*. Utiliza una lista `visited` para rastrear las colisiones y una lista `recursionStack` que se limpia (`remove`) al retroceder, garantizando que el sistema rechace cualquier mutación que genere un bucle cerrado, protegiendo así la integridad matemática del sistema y de la base de datos relacional.


## C1: Documentación de integración: configuración de estrategia y almacenamiento

La integración del proyecto permite cambiar la forma en que trabaja el sistema sin modificar directamente el código fuente. Para esto se utilizan propiedades configurables en Spring Boot, principalmente dentro del archivo:

```txt
tree-app/src/main/resources/application.properties
```

También existe un archivo específico para MongoDB:

```txt
tree-app/src/main/resources/application-mongo.properties
```

Las propiedades más importantes son:

```properties
app.tree-strategy
app.storage
```

Estas propiedades permiten separar la lógica del árbol, el almacenamiento y la interfaz web, haciendo que el proyecto sea más flexible y fácil de probar.

### 1. Configuración de la estrategia del árbol (`app.tree-strategy`)

La propiedad `app.tree-strategy` define qué implementación del motor jerárquico será utilizada para procesar los nodos, recorridos y validaciones del árbol.

Ejemplo:

```properties
app.tree-strategy=custom
```

Con esta configuración se utiliza el motor personalizado, basado en nodos enlazados, referencias al nodo padre, mapa de hijos e índice global de búsqueda. Esta estrategia representa la jerarquía de forma directa dentro de la memoria.

También puede configurarse así:

```properties
app.tree-strategy=collections
```

Con esta opción se utiliza la estrategia basada en colecciones nativas de Java, como `ArrayList` y `ArrayDeque`, para ejecutar recorridos y validaciones de forma iterativa.

| Valor | Descripción |
|---|---|
| `custom` | Usa la implementación personalizada del árbol. |
| `collections` | Usa la implementación basada en colecciones nativas de Java. |

El beneficio de esta configuración es que permite comparar ambas estrategias sin cambiar los controladores, servicios ni endpoints. Solo se modifica la propiedad, se reinicia la aplicación y el backend trabaja con la estrategia seleccionada.

Para ejecutar nuevamente el proyecto se puede usar:

```bash
mvn spring-boot:run
```

### 2. Configuración del almacenamiento (`app.storage`)

La propiedad `app.storage` define dónde se guardarán los datos del sistema.

Ejemplo:

```properties
app.storage=memory
```

| Valor | Tipo de almacenamiento | Descripción |
|---|---|---|
| `memory` | Memoria temporal | Guarda los datos mientras la aplicación está encendida. Al reiniciar, la información se pierde. |
| `postgres` | Base de datos relacional | Guarda los datos en PostgreSQL usando tablas y relaciones. |
| `mongo` | Base de datos documental | Guarda los datos en MongoDB usando colecciones y documentos. |

### 3. Uso con almacenamiento en memoria

Para usar memoria temporal:

```properties
app.storage=memory
```

Este modo es útil para pruebas rápidas, ya que no requiere configurar una base de datos externa. La desventaja es que los datos se pierden al detener o reiniciar la aplicación.

### 4. Uso con PostgreSQL

Para usar PostgreSQL:

```properties
app.storage=postgres
```

También se deben configurar las propiedades de conexión en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/progra3db
spring.datasource.username=progra3
spring.datasource.password=progra3
spring.datasource.driver-class-name=org.postgresql.Driver
```

PostgreSQL permite guardar los nodos de forma permanente en una base de datos relacional. Además, el proyecto puede utilizar los archivos `schema.sql` y `data.sql` para crear tablas y cargar datos iniciales.

Si se trabaja con Docker, se puede levantar el servicio con:

```bash
docker compose up -d
```

### 5. Uso con MongoDB

Para usar MongoDB se utiliza el archivo:

```txt
tree-app/src/main/resources/application-mongo.properties
```

Ejemplo de configuración:

```properties
app.storage=mongo
spring.data.mongodb.uri=mongodb://localhost:27017/tree_db
```

MongoDB permite guardar los datos en documentos, lo cual puede ser útil para estructuras jerárquicas más flexibles.

Para ejecutar la aplicación usando el perfil de MongoDB:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mongo
```

### 6. Combinaciones posibles

| Estrategia | Almacenamiento | Uso recomendado |
|---|---|---|
| `custom` | `memory` | Probar rápidamente el motor personalizado. |
| `collections` | `memory` | Comparar el comportamiento usando colecciones Java. |
| `custom` | `postgres` | Usar el motor personalizado con persistencia relacional. |
| `collections` | `postgres` | Probar colecciones Java con PostgreSQL. |
| `custom` | `mongo` | Usar el motor personalizado con almacenamiento documental. |
| `collections` | `mongo` | Probar colecciones Java con MongoDB. |

En general, esta configuración demuestra que el proyecto tiene una arquitectura desacoplada, porque puede cambiar la estrategia interna del árbol y el almacenamiento sin afectar directamente al frontend.

---

## C2: Documentación del Frontend, OpenAPI y conexión con el Backend

El frontend del proyecto se encuentra dentro del módulo `tree-app`, específicamente en la carpeta:

```txt
tree-app/src/main/resources/static
```

Al estar dentro de `resources/static`, Spring Boot puede servir estos archivos directamente al navegador cuando la aplicación está en ejecución.

### 1. Archivos principales del Frontend

| Archivo | Función |
|---|---|
| `index.html` | Define la estructura visual de la página y los formularios. |
| `styles.css` | Controla el diseño, colores y presentación visual. |
| `app.js` | Contiene la lógica para conectarse con el backend. |
| `openapi.yaml` | Documenta los endpoints disponibles de la API. |

Cada archivo cumple una función específica: `index.html` estructura la página, `styles.css` mejora la apariencia y `app.js` realiza las peticiones al backend.

### 2. Relación con OpenAPI

El archivo `openapi.yaml` funciona como contrato de comunicación entre el frontend y el backend. En él se documentan las rutas disponibles, los métodos HTTP, los parámetros esperados y las respuestas de la API.

Algunos endpoints importantes son:

| Endpoint | Método | Función |
|---|---|---|
| `/nodes/root` | `POST` | Crear el nodo raíz del árbol. |
| `/nodes/{id}` | `GET` | Buscar un nodo por su identificador. |
| `/nodes/{id}` | `DELETE` | Eliminar un nodo. |
| `/tree` | `GET` | Obtener la estructura completa del árbol. |
| `/tree/height` | `GET` | Calcular la altura del árbol. |
| `/tree/validate` | `GET` | Validar que el árbol no tenga errores. |
| `/tree/traversal` | `GET` | Ejecutar recorridos DFS o BFS. |

OpenAPI es importante porque ayuda a que el frontend consuma correctamente las rutas del backend y evita errores de conexión entre ambas partes.

### 3. Conexión del Frontend con el Backend

La conexión entre el frontend y el backend se realiza mediante JavaScript usando `fetch`.

El archivo `app.js` captura los datos ingresados por el usuario, construye la petición HTTP correspondiente, la envía al backend y muestra la respuesta en pantalla.

Cuando la aplicación está corriendo, el frontend puede abrirse desde el navegador usando una dirección como:

```txt
http://localhost:8080/index.html
```

o dependiendo del puerto configurado:

```txt
http://localhost:8081/index.html
```

Como el frontend está dentro del mismo proyecto Spring Boot, puede comunicarse con el backend usando rutas relativas, sin necesidad de escribir la URL completa en cada petición.

### 4. Flujo general de funcionamiento

El flujo de comunicación funciona así:

1. El usuario realiza una acción en la página web.
2. El frontend obtiene los datos del formulario.
3. `app.js` envía una petición HTTP usando `fetch`.
4. El backend recibe la solicitud en un controlador.
5. El servicio procesa la operación usando `tree-app`.
6. El motor lógico de `tree-engine` ejecuta la operación del árbol.
7. Si es necesario, se consulta o modifica el almacenamiento configurado.
8. El backend devuelve una respuesta.
9. El frontend muestra el resultado al usuario.

### 5. Operaciones disponibles desde la interfaz

Desde el frontend, el usuario puede realizar operaciones como:

| Operación | Descripción |
|---|---|
| Crear raíz | Crea el nodo principal del árbol. |
| Agregar hijo | Agrega carpetas o archivos dentro de un nodo padre. |
| Buscar nodo | Localiza un nodo usando su identificador. |
| Eliminar nodo | Borra un nodo y sus descendientes. |
| Ver árbol | Muestra la estructura jerárquica completa. |
| Calcular altura | Obtiene la profundidad máxima del árbol. |
| Validar árbol | Comprueba que no existan ciclos o errores. |
| Recorrido DFS | Recorre el árbol en profundidad. |
| Recorrido BFS | Recorre el árbol por niveles. |

### 6. Ejemplo de funcionamiento

Si el usuario quiere crear un nodo raíz:

1. Escribe el nombre del nodo en la interfaz.
2. Presiona el botón correspondiente.
3. `app.js` envía una petición `POST` a `/nodes/root`.
4. El backend recibe la solicitud.
5. El motor lógico crea la raíz del árbol.
6. El backend devuelve la respuesta.
7. El frontend muestra el resultado en pantalla.

### 7. Recomendación final

Antes de entregar el proyecto, se recomienda verificar que las rutas usadas en `app.js` coincidan con las rutas documentadas en `openapi.yaml` y con los controladores reales del backend.

Esto asegura que el frontend, la documentación OpenAPI y el backend trabajen correctamente en conjunto.