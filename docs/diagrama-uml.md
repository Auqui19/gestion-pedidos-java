# Diagrama UML de clases - SGPI

Diagrama de clases del dominio implementado en la primera version funcional de
**El Ahorro - Tienda de Tecnologia**. Se incluye herencia (`Persona`),
interfaces (`Identificable`, `Mostrable`), composicion
(`Pedido *-- DetallePedido`) y enumeraciones.

```mermaid
classDiagram
    class Identificable {
        <<interface>>
        +int getId()
    }

    class Mostrable {
        <<interface>>
        +String[] toFila()
    }

    class Persona {
        <<abstract>>
        -int id
        -String nombre
        +getId() int
        +setNombre(String)
    }

    class Usuario {
        -String username
        -String passwordHash
        -Rol rol
        +autenticar(String) boolean
        +esAdministrador() boolean
        +puedeEliminar() boolean
    }

    class Cliente {
        -String dni
        -String telefono
        -String direccion
    }

    class Categoria {
        -int id
        -String nombre
    }

    class Producto {
        -int id
        -String codigo
        -String nombre
        -double precio
        -int stock
        -int stockMinimo
        -Categoria categoria
        +hayStock(int) boolean
        +reducirStock(int)
        +aumentarStock(int)
        +esStockBajo() boolean
    }

    class Pedido {
        -int id
        -LocalDate fecha
        -EstadoPedido estado
        -double descuento
        -double total
        -Cliente cliente
        -Usuario vendedor
        -Pago pago
        -List~DetallePedido~ detalles
        +agregarDetalle(Producto, int) DetallePedido
        +calcularTotal() double
        +confirmar()
        +pagar(Pago)
        +cancelar()
    }

    class DetallePedido {
        -int id
        -int cantidad
        -double precioUnitario
        -double subtotal
        -Producto producto
        +calcularSubtotal() double
    }

    class Pago {
        -int id
        -LocalDate fecha
        -double monto
        -MetodoPago metodo
    }

    class Rol {
        <<enumeration>>
        ADMINISTRADOR
        VENDEDOR
        ALMACENERO
    }

    class EstadoPedido {
        <<enumeration>>
        PENDIENTE
        CONFIRMADO
        PAGADO
        CANCELADO
    }

    class MetodoPago {
        <<enumeration>>
        EFECTIVO
        TARJETA
        YAPE
        PLIN
        TRANSFERENCIA
    }

    Identificable <|.. Persona
    Mostrable <|.. Usuario
    Mostrable <|.. Cliente
    Mostrable <|.. Categoria
    Mostrable <|.. Producto
    Mostrable <|.. Pedido
    Mostrable <|.. DetallePedido
    Mostrable <|.. Pago
    Persona <|-- Usuario
    Persona <|-- Cliente

    Usuario "1" -- "*" Pedido : registra
    Cliente "1" -- "*" Pedido : realiza
    Pedido "1" *-- "*" DetallePedido : contiene
    Producto "1" -- "*" DetallePedido : referencia
    Categoria "1" -- "*" Producto : clasifica
    Pedido "1" -- "0..1" Pago : tiene

    Usuario --> Rol
    Pedido --> EstadoPedido
    Pago --> MetodoPago
```

## Notas de diseno

- **Herencia:** `Usuario` y `Cliente` heredan de la clase abstracta `Persona`.
- **Interfaces:** `Identificable` (contrato de identidad) y `Mostrable`
  (conversion a fila para CSV, base del polimorfismo).
- **Encapsulamiento:** atributos privados y validacion en constructores/setters
  (`Validador`).
- **Composicion:** `Pedido` es dueno de sus `DetallePedido`; si el pedido se
  elimina, sus detalles tambien.
- **Inmutabilidad (RN-05):** `DetallePedido.precioUnitario` y `Pago.monto` son
  `final`, por lo que el precio de venta no cambia despues de registrado.
- **Arquitectura:** separacion en capas `modelo`, `repositorio`, `servicio`,
  `vista`, `util` (estilo MVC/DAO).
