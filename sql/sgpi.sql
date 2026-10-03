-- =====================================================================
-- SGPI - Sistema de Gestion de Pedidos e Inventario
-- Esquema de base de datos MySQL (Hostinger)
--
-- En Hostinger la base de datos se crea desde hPanel, por lo que este
-- script solo crea las tablas. Ejecutelo sobre la base ya creada, por
-- ejemplo con MySQL Workbench, DBeaver o la consola de hPanel.
-- =====================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- Tablas de catalogo
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS categorias (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  CHAR(64)     NOT NULL,
    rol            ENUM('ADMINISTRADOR','VENDEDOR','ALMACENERO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS clientes (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    dni        CHAR(8)      NOT NULL UNIQUE,
    telefono   VARCHAR(20)  NOT NULL,
    direccion  VARCHAR(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS productos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    codigo       VARCHAR(30)   NOT NULL UNIQUE,
    nombre       VARCHAR(150)  NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT           NOT NULL DEFAULT 0,
    stock_minimo INT           NOT NULL DEFAULT 0,
    categoria_id INT           NOT NULL,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Tablas transaccionales
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS pagos (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    fecha   DATE          NOT NULL,
    monto   DECIMAL(10,2) NOT NULL,
    metodo  ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS pedidos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    fecha       DATE          NOT NULL,
    estado      ENUM('PENDIENTE','CONFIRMADO','PAGADO','CANCELADO') NOT NULL,
    descuento   DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    total       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cliente_id  INT           NOT NULL,
    vendedor_id INT           NOT NULL,
    pago_id     INT           NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_pedido_vendedor
        FOREIGN KEY (vendedor_id) REFERENCES usuarios(id),
    CONSTRAINT fk_pedido_pago
        FOREIGN KEY (pago_id) REFERENCES pagos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalles (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT           NOT NULL,
    numero          INT           NOT NULL,
    producto_id     INT           NOT NULL,
    cantidad        INT           NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT uq_detalle_numero UNIQUE (pedido_id, numero)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Datos iniciales
-- ---------------------------------------------------------------------

-- Usuario administrador por defecto: admin / admin123 (SHA-256)
INSERT INTO usuarios (nombre, username, password_hash, rol)
SELECT 'Administrador', 'admin',
       '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
       'ADMINISTRADOR'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO categorias (nombre)
SELECT 'Celulares' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Celulares');
INSERT INTO categorias (nombre)
SELECT 'Laptops' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Laptops');
INSERT INTO categorias (nombre)
SELECT 'Accesorios' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Accesorios');
INSERT INTO categorias (nombre)
SELECT 'Audio' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Audio');
