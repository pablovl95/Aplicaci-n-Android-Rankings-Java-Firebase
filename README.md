Aplicación Android Rankings con Java y Firebase

Esta aplicación te permite crear un ranking en la base de datos gratuita de Firebase y dejar comentarios para cada integrante del ranking, además de visualizar sus puntuaciones.

![Captura de pantalla 2024-02-08 180814](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/f4ea5a4c-772e-4f0f-bfa5-08e46fc585b6)
![Captura de pantalla 2024-02-08 181037](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/6654a826-975c-4c23-b950-1422d68cda92)

![Captura de pantalla 2024-02-08 181014](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/e51d0648-411b-461f-b7fa-f62c6fb5f836)
![Captura de pantalla 2024-02-08 180935](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/d7d50d08-2c11-4eb9-b4e3-f22f91d2fc91)
![Captura de pantalla 2024-02-08 180918](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/c575b44e-9a69-4aa2-aa2a-219031b5a115)
![Captura de pantalla 2024-02-08 180904](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/6c07fc40-bb0e-49e4-a480-4ff108a26bb6)
![Captura de pantalla 2024-02-08 180848](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/c6b112e7-167d-4e97-9424-ffd55f76df02)


### Configuración ###
 Para utilizar la base de datos de Firebase, primero debes registrarte, habilitar tu aplicación Android en Firebase y descargar el archivo de configuración que servirá como enlace para conectar tu proyecto Android a la base de datos.

La estructura de la base de datos en Firebase debe inicializarse de la siguiente manera:

Las colecciones principales deben ser: "rankings", "usernames" y "users". Dentro de estas colecciones, se debe colocar el documento "Doc" dentro de "rankings" con sus respectivas columnas, como se muestra a continuación:

![Distribucion Base de Datos](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/72819248-2039-44e4-8eeb-85607b752d3a)

En cuanto a los campos, todos son de tipo String, excepto "Date", "Messages" y "Players". "Date" es un timestamp de Firebase, mientras que "Messages" y "Players" son dos arrays: uno de maps de string a string y otro de strings, respectivamente.

En relación a los "Messages", estos almacenan los mensajes de todos los integrantes del ranking y siguen la siguiente estructura:

![Captura de pantalla 2024-02-08 180202](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/7d0df933-e93d-4b86-8e53-3c18458d9a69)
