-- Datos de ejemplo para desarrollo, pruebas y el video. Los ids son fijos para que las pruebas los conozcan.
-- Los USUARIOS (correo + contrasena BCrypt) los crea la clase UsuariosIniciales al arrancar,
-- con los MISMOS ids de los vendedores y consumidores de este archivo.
--
-- Vendedores (solo ids; el vendedor no es un agregado del dominio):
--   a1111111-1111-1111-1111-111111111111  Cerveceria del Quindio
--   a2222222-2222-2222-2222-222222222222  Licores La Montana

-- ---------------------------------------------------------------- BEBIDAS
INSERT INTO bebidas (id, vendedor_id, nombre, precio_monto, precio_moneda, tipo_material, tipo_capacidad_ml, tipo_retornable, grupo_alcoholico, estado) VALUES
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000001', 'a1111111-1111-1111-1111-111111111111', 'Cerveza Rubia Artesanal', 5000,  'COP', 'VIDRIO',   330, TRUE,  'FERMENTADA',  'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000002', 'a1111111-1111-1111-1111-111111111111', 'Cerveza Negra Artesanal', 6000,  'COP', 'VIDRIO',   330, TRUE,  'FERMENTADA',  'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000003', 'a2222222-2222-2222-2222-222222222222', 'Cerveza Roja Artesanal',  5500,  'COP', 'PLASTICO', 500, TRUE,  'FERMENTADA',  'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000004', 'a2222222-2222-2222-2222-222222222222', 'Aguardiente Tradicional', 45000, 'COP', 'VIDRIO',   750, TRUE,  'DESTILADA',   'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000005', 'a2222222-2222-2222-2222-222222222222', 'Ron Anejo',               60000, 'COP', 'VIDRIO',   750, TRUE,  'DESTILADA',   'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000006', 'a1111111-1111-1111-1111-111111111111', 'Malta Sin Alcohol',       3000,  'COP', 'ALUMINIO', 330, FALSE, 'SIN_ALCOHOL', 'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000007', 'a1111111-1111-1111-1111-111111111111', 'Jugo de Naranja',         4000,  'COP', 'PLASTICO', 500, TRUE,  'SIN_ALCOHOL', 'PUBLICADA'),
                                                                                                                                                            ('b0000000-0000-0000-0000-000000000008', 'a1111111-1111-1111-1111-111111111111', 'Cerveza Descatalogada',   4500,  'COP', 'VIDRIO',   330, TRUE,  'FERMENTADA',  'ELIMINADA');

-- ---------------------------------------------------------------- CONSUMIDORES
-- Ana: mayor de edad y con 5 compras historicas (puede comprar mas de 5 unidades).
-- Carlos: mayor de edad con 3 compras (si pide mas de 5 unidades rompe la regla RN-09).
-- Sofia: MENOR de edad (si compra alcohol rompe la regla RN-08).
INSERT INTO consumidores (id, nombre, codigo, fecha_nacimiento, compras_historicas) VALUES
                                                                                        ('c1111111-1111-1111-1111-111111111111', 'Ana Maria Gomez', 'ANA001', '1995-04-12', 5),
                                                                                        ('c2222222-2222-2222-2222-222222222222', 'Carlos Ruiz',     'CAR002', '1990-08-30', 3),
                                                                                        ('c3333333-3333-3333-3333-333333333333', 'Sofia Herrera',   'SOF003', '2012-02-14', 2);

-- ---------------------------------------------------------------- ENVASES
INSERT INTO envases (id, tipo_material, tipo_capacidad_ml, tipo_retornable, estado, cantidad_usos) VALUES
                                                                                                       ('e0000000-0000-0000-0000-000000000001', 'VIDRIO',   330, TRUE,  'NUEVO',      0),
                                                                                                       ('e0000000-0000-0000-0000-000000000002', 'PLASTICO', 500, TRUE,  'RETORNADO', 10),
                                                                                                       ('e0000000-0000-0000-0000-000000000003', 'VIDRIO',   750, TRUE,  'DANADO',     3),
                                                                                                       ('e0000000-0000-0000-0000-000000000004', 'ALUMINIO', 330, FALSE, 'NUEVO',      0),
                                                                                                       ('e0000000-0000-0000-0000-000000000005', 'VIDRIO',   330, TRUE,  'DESCARTADO', 30);

-- ---------------------------------------------------------------- COMPRAS
-- Las fechas son antiguas: ninguna se puede cancelar ya (RN-13, solo 24 horas).
-- La compra CANCELADA y la de la bebida ELIMINADA NO cuentan en el ranking.
INSERT INTO compras (id, consumidor_id, bebida_id, vendedor_id, unidades, precio_monto, precio_moneda, fecha, estado) VALUES
                                                                                                                          ('d0000000-0000-0000-0000-000000000001', 'c1111111-1111-1111-1111-111111111111', 'b0000000-0000-0000-0000-000000000001', 'a1111111-1111-1111-1111-111111111111', 6,  5000, 'COP', '2026-10-01 10:00:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000002', 'c1111111-1111-1111-1111-111111111111', 'b0000000-0000-0000-0000-000000000001', 'a1111111-1111-1111-1111-111111111111', 4,  5000, 'COP', '2026-10-05 18:30:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000003', 'c2222222-2222-2222-2222-222222222222', 'b0000000-0000-0000-0000-000000000001', 'a1111111-1111-1111-1111-111111111111', 3,  5000, 'COP', '2026-10-06 20:15:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000004', 'c1111111-1111-1111-1111-111111111111', 'b0000000-0000-0000-0000-000000000004', 'a2222222-2222-2222-2222-222222222222', 2, 45000, 'COP', '2026-10-08 21:00:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000005', 'c2222222-2222-2222-2222-222222222222', 'b0000000-0000-0000-0000-000000000004', 'a2222222-2222-2222-2222-222222222222', 2, 45000, 'COP', '2026-10-09 19:45:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000006', 'c1111111-1111-1111-1111-111111111111', 'b0000000-0000-0000-0000-000000000005', 'a2222222-2222-2222-2222-222222222222', 1, 60000, 'COP', '2026-10-10 22:10:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000007', 'c2222222-2222-2222-2222-222222222222', 'b0000000-0000-0000-0000-000000000005', 'a2222222-2222-2222-2222-222222222222', 2, 60000, 'COP', '2026-10-11 17:20:00', 'CANCELADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000008', 'c3333333-3333-3333-3333-333333333333', 'b0000000-0000-0000-0000-000000000007', 'a1111111-1111-1111-1111-111111111111', 5,  4000, 'COP', '2026-10-12 09:30:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000009', 'c3333333-3333-3333-3333-333333333333', 'b0000000-0000-0000-0000-000000000007', 'a1111111-1111-1111-1111-111111111111', 3,  4000, 'COP', '2026-10-14 11:00:00', 'CONFIRMADA'),
                                                                                                                          ('d0000000-0000-0000-0000-000000000010', 'c1111111-1111-1111-1111-111111111111', 'b0000000-0000-0000-0000-000000000008', 'a1111111-1111-1111-1111-111111111111', 12, 4500, 'COP', '2026-09-20 16:00:00', 'CONFIRMADA');