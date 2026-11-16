# Building
Put these jars (not included, they belong to their authors) into:
- emt-legacy/libs: Thaumcraft-1.7.10-4.2.3.5.jar, Baubles-1.7.10-1.0.1.10.jar
- emt-bridge/libs: ModernForgeConnectLoader-1.0.9.jar, tc-modern-lib-0.2.0.0.jar

Then run `./gradlew build` in emt-legacy (Java 8 toolchain via RetroFuturaGradle) and in emt-bridge (Java 17).
