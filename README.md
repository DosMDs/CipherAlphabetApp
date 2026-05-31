# Cipher Alphabet App

Android-приложение на Kotlin и Jetpack Compose для конвертации чисел, букв, азбуки Морзе и символов Брайля.

## Автоматическая сборка и релизы

CI запускается автоматически на:

- `push` в ветки `develop` и `main`;
- `pull_request` в ветки `develop` и `main`;
- ручной запуск через вкладку GitHub Actions.

Обычная CI-сборка выполняет unit-тесты, собирает debug APK и публикует APK как workflow artifact. Скачать его можно во вкладке `Actions` в артефакте `CipherAlphabetApp-debug-apk`.

Релизная сборка запускается при отправке тега вида `vX.Y.Z` и публикует debug APK во вкладке `Releases`. Release signing пока не используется и будет добавлен позже.

Чтобы создать релиз:

1. Обновить `versionName` и `versionCode`.
2. Обновить `CHANGELOG.md`.
3. Сделать commit.
4. Слить `develop` в `main`.
5. Создать и отправить tag `vX.Y.Z`.

```powershell
git checkout main
git merge --no-ff develop
git tag -a vX.Y.Z -m "Release X.Y.Z"
git push origin main
git push origin vX.Y.Z
```

Релизный APK будет доступен во вкладке `Releases` с именем вида:

```text
CipherAlphabetApp-vX.Y.Z-debug.apk
```
