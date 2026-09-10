# Handoff — для следующего Клода

## Правила работы с этим пользователем (обязательно соблюдать)

- **Не коммитить без явного апрува.** После каждой итерации показывай диф/описание изменений и жди "апрув", "окей", "коммить" или аналог. Без этого — не коммитить.
- **Не задавать вопросы в конце задачи** и не намекать на следующие шаги. Завершил — молчи.
- **Не писать код без разрешения.** Сначала опиши что будешь делать, жди апрув.
- **Когда нужен ввод от пользователя** (создать репо, переключиться на SSH и т.д.) — явно скажи что конкретно нужно сделать и жди.

---

## Контекст проекта

Telegram-бот для крипто-алертов. Kotlin + Spring Boot 3.3.5, Java 17, Gradle, PostgreSQL, Liquibase, Thymeleaf, telegrambots 6.9.7.1.
Шлёт уведомления по данным с Binance и Bybit (бесплатные API).

Репо кода: https://github.com/zor07/trading_bots  
Рабочая директория сейчас: `trading_bots/` (ты запущен из папки выше, рядом должна быть `trading-bots-infra/`)

---

## Текущее состояние

В `trading_bots/` есть незакоммиченные изменения:

- **`Dockerfile`** (новый) — двухэтапная сборка: `gradle:8.5-jdk17` → `bellsoft/liberica-openjdk-alpine:17`
- **`.github/workflows/deploy.yml`** (новый) — CI/CD: push в master → сборка образа → push в `ghcr.io/zor07/trading_bots:latest` → SSH на VPS → `./deploy.sh`
- **`src/main/resources/application.yml`** (изменён) — DB URL теперь через `${DB_HOST}:${DB_PORT}/${DB_NAME}` вместо хардкода
- **`infra/`** (новая папка) — файлы для отдельного infra репо:
  - `docker-compose.yml` — app + postgres, image из ghcr.io
  - `deploy.sh` — pull → up -d → image prune
  - `.env.example`
  - `.gitignore`
  - `README.md`

---

## Твоя задача — пошагово

### Шаг 1. Показать пользователю что будет закоммичено
Прочитай все незакоммиченные файлы в `trading_bots/`, опиши изменения пользователю, жди апрув.

### Шаг 2. Коммит и пуш `trading_bots`
После апрува:
```
git add Dockerfile .github/ src/main/resources/application.yml infra/
git commit
git push
```

### Шаг 3. Попросить пользователя создать infra репо
Скажи пользователю: *"Создай пустое GitHub репо `trading-bots-infra` и скинь SSH remote URL"*. Жди URL.

### Шаг 4. Инициализировать infra репо
Когда получишь URL:
- Скопировать содержимое `trading_bots/infra/` в `trading-bots-infra/`
- `git init`, `git remote add origin <url>`, закоммитить всё, запушить
- Показать пользователю что будет в коммите перед коммитом

### Шаг 5. GitHub Secrets
Напомни пользователю добавить в `github.com/zor07/trading_bots` → Settings → Secrets and variables → Actions:

| Секрет | Что |
|--------|-----|
| `VPS_HOST` | IP сервера |
| `VPS_USER` | SSH-пользователь |
| `VPS_SSH_KEY` | Приватный SSH-ключ (`cat ~/.ssh/id_rsa` или `~/.ssh/id_ed25519`) |

### Шаг 6. Переключение на сервер
Когда секреты добавлены, скажи пользователю: *"Теперь зайди по SSH на сервер и запусти там Клода. Скажи ему прочитать `/opt/trading-bots-infra/README.md`, а затем `HANDOFF_SERVER.md` — я его сейчас создам"*.

### Шаг 7. Создать HANDOFF_SERVER.md
Создай файл `trading-bots-infra/HANDOFF_SERVER.md` с задачей для Клода на сервере (см. ниже). Этот файл попадёт в infra репо.

---

## Содержимое HANDOFF_SERVER.md (создай этот файл)

```markdown
# Задача для Клода на сервере

## Что нужно сделать

1. Клонировать infra репо (если ещё не):
   git clone git@github.com:zor07/trading-bots-infra.git /opt/trading-bots-infra

2. Создать .env из .env.example, заполнить реальными значениями:
   cp .env.example .env
   Пинать пользователя за каждое значение которое неизвестно (токен бота, пароль БД, домен).

3. chmod +x deploy.sh

4. Настроить nginx как reverse proxy:
   - app слушает на 127.0.0.1:8080
   - nginx слушает 80/443, проксирует на 8080
   - SSL через certbot (Let's Encrypt)
   - Спросить у пользователя домен если не знаешь

5. Первый запуск:
   docker compose pull
   docker compose up -d

6. Проверить что всё работает:
   docker compose ps
   docker compose logs -f trading-bots-app

7. Дополнить README.md в этой папке:
   - Как смотреть логи
   - Как перезапустить вручную
   - Как откатиться
   - Схема nginx

## Правила
- Каждый шаг показывать перед выполнением, ждать апрув
- Не коммитить без апрува
```
