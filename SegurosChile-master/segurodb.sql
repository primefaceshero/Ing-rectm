-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-06-2017 a las 12:28:09
-- Versión del servidor: 10.1.13-MariaDB
-- Versión de PHP: 5.6.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `segurodb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `rut_cliente` int(11) NOT NULL,
  `dv_cliente` varchar(1) NOT NULL,
  `clave_cliente` int(11) NOT NULL,
  `nombres_cliente` varchar(30) NOT NULL,
  `apellido_pat_cliente` varchar(15) NOT NULL,
  `apellido_mat_cliente` varchar(15) NOT NULL,
  `direccion_cliente` varchar(30) NOT NULL,
  `telefono_cliente` int(11) NOT NULL,
  `correo_cliente` varchar(30) NOT NULL,
  `actividad` varchar(30) NOT NULL,
  `beneficiario1_nombre` varchar(30) NOT NULL,
  `beneficiario2_nombre` varchar(30) NOT NULL,
  `rut_vendedor` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`rut_cliente`, `dv_cliente`, `clave_cliente`, `nombres_cliente`, `apellido_pat_cliente`, `apellido_mat_cliente`, `direccion_cliente`, `telefono_cliente`, `correo_cliente`, `actividad`, `beneficiario1_nombre`, `beneficiario2_nombre`, `rut_vendedor`) VALUES
(1, '1', 18, 'a', 'asd', 'a', 'aaaaaaaaaaaa', 123, 'fel.penailillo@alumnos.duoc.cl', 'asd', 'asd', 'asd', 123),
(19597642, '1', 195976428, 'Felipe', 'Peñailillo', 'Torres', 'mi casita', 93483945, 'fel.penailillo@alumnos.duoc.cl', 'Estudiante', 'Juan', 'Ricardo', 124),
(19597643, '3', 195976438, 'Sebastián Elías', 'Cabello', 'Pasten', 'Pluton', 85985964, 'fel.penailillo@alumnos.duoc.cl', 'Estudiante', 'Ricardo', 'Marcos', 124);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nombreproducto`
--

CREATE TABLE `nombreproducto` (
  `id` int(11) NOT NULL,
  `nombre` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `nombreproducto`
--

INSERT INTO `nombreproducto` (`id`, `nombre`) VALUES
(3, 'Accidentes Personales'),
(2, 'Asistencia en Viaje'),
(4, 'Catastróficos'),
(5, 'Oncológicos'),
(1, 'Seguro de Vida');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `precio`
--

CREATE TABLE `precio` (
  `id` int(11) NOT NULL,
  `valor` int(11) NOT NULL,
  `unidad` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `precio`
--

INSERT INTO `precio` (`id`, `valor`, `unidad`) VALUES
(1, 1000, 'UF'),
(2, 3000, 'UF'),
(3, 5000, 'UF');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL,
  `nombre_producto` varchar(30) NOT NULL,
  `estado_producto` varchar(15) NOT NULL,
  `rut_cliente` int(11) NOT NULL,
  `id_precio` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id_producto`, `nombre_producto`, `estado_producto`, `rut_cliente`, `id_precio`) VALUES
(3, 'Catastróficos', 'Aprobado', 1, 2),
(4, 'Oncológicos', 'Aprobado', 19597642, 1),
(5, 'Accidentes Personales', 'Aprobado', 19597643, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `supervisor`
--

CREATE TABLE `supervisor` (
  `rut_supervisor` int(11) NOT NULL,
  `clave_supervisor` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `supervisor`
--

INSERT INTO `supervisor` (`rut_supervisor`, `clave_supervisor`) VALUES
(1111, '1'),
(1112, '123');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `super_administrador`
--

CREATE TABLE `super_administrador` (
  `rut_admin` int(11) NOT NULL,
  `clave_admin` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `super_administrador`
--

INSERT INTO `super_administrador` (`rut_admin`, `clave_admin`) VALUES
(18597321, '123');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vendedor`
--

CREATE TABLE `vendedor` (
  `rut_vendedor` int(11) NOT NULL,
  `nombre_vendedor` varchar(15) NOT NULL,
  `clave_vendedor` varchar(15) NOT NULL,
  `correo_vendedor` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `vendedor`
--

INSERT INTO `vendedor` (`rut_vendedor`, `nombre_vendedor`, `clave_vendedor`, `correo_vendedor`) VALUES
(123, 'Juan', '123', 'juan@gmail.com'),
(124, 'Jorge', '123', 'jorgito@gmail.com');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`rut_cliente`),
  ADD KEY `rut_vendedor` (`rut_vendedor`);

--
-- Indices de la tabla `nombreproducto`
--
ALTER TABLE `nombreproducto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `nombre` (`nombre`);

--
-- Indices de la tabla `precio`
--
ALTER TABLE `precio`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`),
  ADD KEY `rut_cliente` (`rut_cliente`),
  ADD KEY `id_precio` (`id_precio`),
  ADD KEY `nombre_producto` (`nombre_producto`);

--
-- Indices de la tabla `supervisor`
--
ALTER TABLE `supervisor`
  ADD PRIMARY KEY (`rut_supervisor`);

--
-- Indices de la tabla `super_administrador`
--
ALTER TABLE `super_administrador`
  ADD PRIMARY KEY (`rut_admin`);

--
-- Indices de la tabla `vendedor`
--
ALTER TABLE `vendedor`
  ADD PRIMARY KEY (`rut_vendedor`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `nombreproducto`
--
ALTER TABLE `nombreproducto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT de la tabla `precio`
--
ALTER TABLE `precio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`rut_cliente`) REFERENCES `cliente` (`rut_cliente`),
  ADD CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`id_precio`) REFERENCES `precio` (`id`),
  ADD CONSTRAINT `producto_ibfk_3` FOREIGN KEY (`nombre_producto`) REFERENCES `nombreproducto` (`nombre`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
