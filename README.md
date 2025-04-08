# ServITech – Frontend

<div align="center">
	<div style="display: flex; justify-content: center; gap: 20px; padding: 30px;">
		<img src="https://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg" alt="Android Logo" width="250" height="250">
		<img src="https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin_Icon.png" alt="Kotlin Logo" width="230" height="250">
	</div>
</div>

![Badge En Desarrollo](https://img.shields.io/badge/STATUS-EN%20DESARROLLO-green)

## 📚 Tabla de Contenidos

1. [Sobre el Proyecto](#%EF%B8%8F-sobre-el-proyecto)
2. [Arquitectura del Sistema](#-arquitectura-del-sistema)
3. [Tecnologías Usadas](#-tecnologías-usadas)
4. [Instalación](#%EF%B8%8F-instalación)
5. [Uso de Gitflow](#-uso-de-gitflow)
6. [Buildeo para Producción](#%EF%B8%8F-buildeo-para-producción)
7. [Ejecución de Pruebas](#-ejecución-de-pruebas)
8. [Contribuyendo](#-contribuyendo)
9. [Licencia](#-licencia)
10. [Autores](#-autores)

## 🛠️ Sobre el Proyecto

**ServITech** es una aplicación diseñada para gestionar cotizaciones de artículos tecnológicos, artículos de anime y solicitudes de soporte técnico. Este repositorio contiene únicamente el frontend del sistema. El backend del proyecto está disponible en el siguiente repositorio: [ServITech – Backend](https://github.com/MOSHE9647/ServITech-Backend).

Este proyecto fue desarrollado como parte de un curso universitario en la Universidad Nacional de Costa Rica para el curso **Diseño y Programación de Plataformas Móviles** durante el **I Ciclo Lectivo del año 2025**.

---

## 📊 Arquitectura del Sistema

El sistema está compuesto por los siguientes componentes principales:
- **Cliente móvil:** Implementado en Android (Kotlin), interactúa con el backend a través de la API REST.
- **Backend:** Implementado en Laravel, gestiona la lógica de negocio, autenticación y acceso a la base de datos.
- **Base de datos:** MySQL o SQLite, utilizada para almacenar datos de usuarios, artículos y solicitudes de soporte técnico.

---

## 🚀 Tecnologías Usadas

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Studio](https://developer.android.com/studio)
- [Retrofit](https://square.github.io/retrofit/)
- [Coil](https://coil-kt.github.io/coil/)
- [Material Design 3](https://m3.material.io/)

---

## ⚙️ Instalación

1. Clona el repositorio:

		```bash
		git clone https://github.com/MOSHE9647/ServITech-Frontend.git
		cd ServITech-Frontend
		```

2. Abre el proyecto en Android Studio.

3. Configura el archivo `local.properties` para apuntar al SDK de Android:

		```
		sdk.dir=/ruta/a/tu/android/sdk
		```

4. Sincroniza las dependencias de Gradle.

5. Configura la URL base de la API en el archivo correspondiente (por ejemplo, en un archivo `Constants.kt`):

		```kotlin
		const val BASE_URL = "http://tu-backend-url/api"
		```

6. Ejecuta la aplicación en un emulador o dispositivo físico.

---

## 🧠 Uso de Gitflow

Este proyecto usa **Gitflow** para organizar su desarrollo. Las ramas principales son:

- `main`: Rama de producción
- `dev`: Rama de desarrollo

### Ramas adicionales que Gitflow utiliza:

- `feature/*`: Nuevas funcionalidades
- `release/*`: Versiones candidatas
- `bugfix/*`: Correcciones de errores
- `hotfix/*`: Correcciones críticas en producción

### Cómo iniciar Gitflow:

```bash
git flow init -d
```

Esto configura Gitflow con los nombres por defecto que ya usamos (`main` y `dev`).

#### Ejemplos:

Crear una nueva funcionalidad:

```bash
git flow feature start nombre-de-tu-feature
```

Finalizar y fusionar una funcionalidad:

```bash
git flow feature finish nombre-de-tu-feature
```

Crear un release:

```bash
git flow release start v1.0.0
git flow release finish v1.0.0
```

---

## 🏗️ Buildeo para Producción

Para generar un APK de producción, sigue estos pasos:

1. En Android Studio, selecciona el build type `release`.
2. Usa la opción `Build > Build Bundle(s)/APK(s) > Build APK(s)`.

El APK generado estará disponible en el directorio `app/build/outputs/apk/release/`.

---

## 🧪 Ejecución de Pruebas

El proyecto incluye pruebas unitarias y de instrumentación para asegurar la calidad del código. Las pruebas están ubicadas en los directorios `src/test` y `src/androidTest`.

Para ejecutar todas las pruebas, utiliza el siguiente comando en Android Studio o desde la línea de comandos:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 🤝 Contribuyendo

Gracias por considerar contribuir a **ServITech**. Por favor usa ramas `feature/*` y sigue el flujo Gitflow. Sigue estos pasos para contribuir:

1. Haz un fork del repositorio.
2. Crea una rama para tu funcionalidad o corrección:

		```bash
		git flow feature start nueva-funcionalidad
		```

3. Realiza tus cambios y asegúrate de que las pruebas pasen.
4. Envía un pull request a la rama `dev`.

---

## 📜 Licencia

Este proyecto está protegido por derechos de autor (c) 2025 Isaac Herrera, Carlos Orellana, David Padilla. Todos los derechos reservados.

Consulta el archivo [LICENSE](LICENSE) para más detalles sobre las restricciones y términos de uso.

---

## 👤 Autores

Este proyecto fue desarrollado por:

- **Carlos Orellana**  
	- Rol: Contribuidor  
	- GitHub: [CarlosOrellanaEst](https://github.com/CarlosOrellanaEst)  
	- Correo: [carlos.orellana.obando@est.una.ac.cr](mailto:carlos.orellana.obando@est.una.ac.cr)

- **David Padilla**  
	- Rol: Contribuidor  
	- GitHub: [DavidPMCR](https://github.com/DavidPMCR)  
	- Correo: [alleriaysebastian@gmail.com](mailto:alleriaysebastian@gmail.com)

- **Isaac Herrera**  
	- Rol: Creador del repositorio y desarrollador principal  
	- GitHub: [MOSHE9647](https://github.com/MOSHE9647)  
	- Correo personal: [isaacmhp2001@gmail.com](mailto:isaacmhp2001@gmail.com)  
	- Correo institucional: [isaac.herrera.pastrana@est.una.ac.cr](mailto:isaac.herrera.pastrana@est.una.ac.cr)
