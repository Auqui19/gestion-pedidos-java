# SGPI - Sistema de Gestion de Pedidos e Inventario

Primera version funcional del proyecto final del curso **Tecnicas de
Programacion Orientada a Objetos** (Ingenieria de Sistemas Computacionales,
UPN). Caso de negocio: tienda de tecnologia **"El Ahorro"**.

## Descripcion

Aplicacion de **consola en Java** que gestiona usuarios, productos,
inventario, clientes, pedidos y reportes. Aplica POO (herencia, interfaces,
encapsulamiento, composicion y enums) y persiste la informacion en una base de
datos **MySQL** mediante **JDBC (patron DAO)**.

## Integrantes

- Sebastian Alonso Auqui Tasayco
- Nallely Alexandra Quispe Cristan
- Angelo Roberto Soto Martinez

## Tecnologias

- Java 17+
- Interfaz de usuario por consola
- MySQL (Hostinger) con JDBC puro (patron DAO / Repository)
- Maven
- Git / GitHub

## Estructura del proyecto

```text
src/main/java/com/elahorro/sgpi
├── Main.java            Lanzador (menu de consola)
├── config/              Conexion (Singleton JDBC a MySQL)
├── modelo/              Persona, Usuario, Cliente, Categoria, Producto,
│   └── enums/           Pedido, DetallePedido, Pago + Identificable, Mostrable
├── repositorio/         DAO JDBC (Repositorio, *DAO)
├── servicio/            Sistema y servicios de negocio (*Service)
├── vista/               Consola y AplicacionConsola (interfaz de consola)
├── util/                Validador, Cifrador (SHA-256)
└── pruebas/             PruebasSistema (evidencia de los 20 RF)
src/main/resources/      db.properties (configuracion MySQL)
src/test/java/com/elahorro/sgpi/ModeloTest.java
sql/                     sgpi.sql (esquema de la base de datos)
docs/                    requerimientos.md, diagrama-uml.md, evidencia.md
```

## Configuracion de la base de datos

1. En **hPanel de Hostinger**, cree una base de datos y un usuario MySQL.
2. Active **Remote MySQL** y agregue su **IP publica** a la whitelist.
3. Ejecute `sql/sgpi.sql` sobre la base creada (MySQL Workbench, DBeaver o
   phpMyAdmin de hPanel). El script crea las tablas e inserta el administrador
   por defecto.
4. Copie `src/main/resources/db.properties.example` como
   `db.properties` y complete `db.url`, `db.user` y `db.password`.

Puede evitar el archivo usando variables de entorno:
`SGPI_DB_URL`, `SGPI_DB_USER`, `SGPI_DB_PASSWORD`.

> El archivo `db.properties` esta en `.gitignore`: no suba credenciales reales.

## Funcionalidades

- Login con roles (Administrador, Vendedor, Almacenero) y contrasena cifrada.
- CRUD de productos tecnologicos (celulares, laptops, accesorios, audio),
  categorias, clientes y usuarios.
- Busqueda de productos y clientes; alertas de stock minimo.
- Registro de pedidos con multiples detalles, validacion de stock, descuento
  (maximo 20% sin autorizacion), pago y cancelacion con reposicion de stock.
- Reportes de stock bajo y ventas por fecha.

## Ejecucion

### Con Maven

```bash
mvn clean package
java -jar target/sgpi-1.0.0.jar
```

### Sin Maven (javac)

```powershell
$conn = "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar"
javac -encoding UTF-8 -cp $conn -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main/java).FullName
Copy-Item src/main/resources/* target/classes/
java -cp "target/classes;$conn" com.elahorro.sgpi.Main
```

**Credenciales por defecto:** `admin` / `admin123`.

## Evidencia

```powershell
java -cp "target/classes;$conn" com.elahorro.sgpi.pruebas.PruebasSistema
```

Genera 21 verificaciones de los 20 requerimientos prioritarios contra la base
de datos. **Advertencia:** limpia las tablas; use una base de practica.
Ver `docs/requerimientos.md` y `docs/evidencia.md`.

## Documentacion

- `docs/requerimientos.md` - 20 requerimientos con criterios de aceptacion.
- `docs/diagrama-uml.md` - Diagrama UML de clases (Mermaid).
- `docs/evidencia.md` - Salida de pruebas y evidencia de persistencia MySQL.

## Repositorio

https://github.com/Auqui19/gestion-pedidos-java
