Aplicación Android Rankings con Java y Firebase
Esta aplicación te permite crear un ranking en la base de datos gratuita de Firebase y dejar comentarios para cada integrante del ranking, además de visualizar sus puntuaciones.

La estructura de la base de datos en Firebase debe inicializarse de la siguiente manera:

Las colecciones principales deben ser: "rankings", "usernames" y "users". Dentro de estas colecciones, se debe colocar el documento "Doc" dentro de "rankings" con sus respectivas columnas, como se muestra a continuación:

![Distribucion Base de Datos](https://github.com/pablovl95/Aplicaci-n-Android-Rankings-Java-Firebase/assets/73790559/72819248-2039-44e4-8eeb-85607b752d3a)

En cuanto a los campos, todos son de tipo String, excepto "Date", "Messages" y "Players". "Date" es un timestamp de Firebase, mientras que "Messages" y "Players" son dos arrays: uno de maps de string a string y otro de strings, respectivamente.
