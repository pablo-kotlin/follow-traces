# Sigue mis huellas

<p align="center">
  <img width="512" alt="Logo" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/e2b88c19-6687-4a80-934e-337c09018943">
</p>
<p align="center">
  <img width="512" alt="Logo" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/fde86be0-4b5e-4a44-94d1-6079e2c28025">
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
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/a2a6d04d-2b80-4d00-9369-eb890fde9def">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/c66ea3c1-4346-4dd7-8c95-4912c1df98ed">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/6e950c7f-a4e2-4bf1-ac4f-3d6ac69d86b4">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/69c23717-e314-4a14-a0fc-f08afbc4b9e5">
</p>

<p align="center">
  <img width="300" alt="Screenshot_20220609_211305" src="https://github.com/pablo-kotlin/follow-traces/assets/128930557/9082aff5-297a-4cc5-8b33-b02cd54d0ec3">
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


