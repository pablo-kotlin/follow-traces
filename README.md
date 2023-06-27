# Sigue mis huellas

<p align="center">
  <img width="512" alt="Logo" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/73ec4789-9d4d-43e5-ad7e-23764f742eb3">
</p>

Esta app todavía está en una versión beta, pero la mayor parte de sus funcionalidades están 100% habilitadas.

En este proyecto he buscado complementar un aspecto que no pude implementar en mi aplicación anterior, es decir, integrar el SDK de Google Maps para Android.

En Sigue mis huellas nos encontramos con una app en la que podremos buscar, dar de alta y valorar con una reseña, aquellos lugares que permiten el acceso con animales.

## Objetivos

De forma más específica, nos hemos marcado los siguientes objetivos:

1. Poder añadir un nuevo establecimiento a la base de datos, usando como ubicación nuestra geolocalización actual.
2. Acceder a un listado de ubicaciones, de más cercano a más lejano, basada en nuestra ubicación.
3. El listado de ubicaciones tiene que ser visible tanto desde un mapa, como en un recycler view.
4. Se pueden hacer filtros por el tipo de establecimiento de que estoy buscando con mi mascota (Restaurante, zona verde, alojamiento, etc.).
5. Uso de la librería Volley para comunicarse con la Base de Datos (MySQL).

## Tecnologías y herramientas utilizadas en el proyecto

Volley: Librería de Android que facilita el manejo de solicitudes HTTP y la comunicación con servidores. Se utiliza para realizar solicitudes de red de forma eficiente y procesar las respuestas de manera sencilla.

API de Google Maps: Proporciona acceso a la funcionalidad de mapas y servicios relacionados de Google. Permite mostrar mapas interactivos en la aplicación y utilizar características como la geolocalización, rutas y marcadores.

## Capturas de Pantalla

A continuación se muestran algunas capturas de pantalla de las diferentes actividades por las que navegaremos en nuestra aplicación:

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/a81fd437-37aa-4571-84d1-97a2ecf07b4f">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/05604ef7-bbe8-4df3-a0fa-a70600e4bd8c">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/c069212a-4639-47f1-9262-4ddaafc31c31">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/0a0e2c71-5e9b-48f8-b54b-0824fa4c3ea4">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/20db03ec-1097-4220-927f-8eaac02eb36b">
</p>


## Requisitos funcionales

•	La primera pantalla de la aplicación será la de Login, donde el usuario tendrá la opción de identificarse, o de crear una nueva cuenta.

•	En caso de introducir mal la contraseña o su nombre de usuario, no se permitirá el acceso y se lanzará un mensaje de aviso.

•	Tras la correcta identificación del usuario, se accederá a la pantalla principal de la aplicación.

•	En la pantalla principal, el usuario tendrá dos opciones: Añadir un nuevo establecimiento, o acceder en Explorar.

•	Al añadir un nuevo establecimiento, la app tomará la ubicación del usuario, si bien ésta podrá ser modificada manualmente arrastrando la posición del marcador en el mapa.

•	Para completar la creación de un nuevo lugar, tendremos que añadir el nombre y el tipo de establecimiento. Las observaciones y la posibilidad de valorarlo positiva o negativamente son opcionales.

•	Si La base de datos detecta una coincidencia de otro establecimiento con el mismo nombre, no nos permitirá crearlo y lanzará un mensaje de aviso.

•	Desde la pantalla de Exploración, la aplicación nos mostrará los establecimientos más cercanos que admiten animales.

•	Si pulsamos en uno de los marcadores, nos mostrará la distancia, el nombre, el tipo de establecimiento, y la reseña más reciente (en caso de haberla).

•	Podemos hacer un fitro para ver en el mapa solo determinados tipos de establecimientos.

•	Desde la pestaña superior "Listado" veremos otra vista con todos los establecimientos ordenados en base a la distancia a la que se hayan del usuario.


## Contacto

E-mail de contacto: pablokotlin@gmail.com


