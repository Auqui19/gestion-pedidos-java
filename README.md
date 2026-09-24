# SGPI - Sistema de Gestion de Pedidos e Inventario

Primera version funcional del proyecto final del curso **Tecnicas de
Programacion Orientada a Objetos** (Ingenieria de Sistemas Computacionales,
UPN). Caso de negocio: tienda de tecnologia **"El Ahorro"**.

## Descripcion

Aplicacion de **consola en Java** que gestiona usuarios, productos,
inventario, clientes, pedidos y reportes. Aplica POO (herencia, interfaces,
encapsulamiento, composicion y enums) y persiste la informacion en archivos
**CSV**.

## Integrantes

- Sebastian Alonso Auqui Tasayco
- Nallely Alexandra Quispe Cristan
- Angelo Roberto Soto Martinez

## Tecnologias

- Java 17+
- Interfaz de usuario por consola
- Archivos CSV (`FileWriter` / `BufferedReader`)
- Maven (opcional)
- Git / GitHub

## Estructura del proyecto

```text
src/main/java/com/elahorro/sgpi
├── Main.java            Lanzador (menu de consola)
├── modelo/              Persona, Usuario, Cliente, Categoria, Producto,
│   └── enums/           Pedido, DetallePedido, Pago + Identificable, Mostrable
├── repositorio/         Persistencia CSV (Repositorio, *Repositorio)
├── servicio/            Sistema y servicios de negocio (*Service)
├── vista/               Consola y AplicacionConsola (interfaz de consola)
├── util/                Validador, Cifrador (SHA-256), CsvUtil
└── pruebas/             PruebasSistema (evidencia de los 20 RF)
src/test/java/com/elahorro/sgpi/ModeloTest.java
docs/                    requerimientos.md, diagrama-uml.md, evidencia.md
datos/                   Archivos CSV generados en tiempo de ejecucion
```

## Funcionalidades

- Login con roles (Administrador, Vendedor, Almacenero) y contrasena cifrada.
- CRUD de productos tecnologicos (celulares, laptops, accesorios, audio),
  categorias, clientes y usuarios.
- Busqueda de productos y clientes; alertas de stock minimo.
- Registro de pedidos con multiples detalles, validacion de stock, descuento
  (maximo 20% sin autorizacion), pago y cancelacion con reposicion de stock.
- Reportes de stock bajo y ventas por fecha, con exportacion a CSV.

## Ejecucion

### Con Maven

```bash
mvn clean package
java -jar target/sgpi-1.0.0.jar
```

### Sin Maven (javac)

```powershell
javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main/java).FullName
java -cp target/classes com.elahorro.sgpi.Main
```

**Credenciales por defecto:** `admin` / `admin123`.

## Evidencia

```powershell
java -cp target/classes com.elahorro.sgpi.pruebas.PruebasSistema
```

Genera 21 verificaciones de los 20 requerimientos prioritarios. Ver
`docs/requerimientos.md` y `docs/evidencia.md`.

## Documentacion

- `docs/requerimientos.md` - 20 requerimientos con criterios de aceptacion.
- `docs/diagrama-uml.md` - Diagrama UML de clases (Mermaid).
- `docs/evidencia.md` - Salida de pruebas y archivos CSV de ejemplo.

## Repositorio

https://github.com/Auqui19/gestion-pedidos-java
