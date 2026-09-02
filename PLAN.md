# Crypto Alerts Telegram Bot — План

## Алерты

| # | Тип | Статус |
|---|-----|--------|
| 4 | Резкое движение цены | ✅ готово |
| 3 | Long/Short Ratio на экстремуме | ⏳ |
| 1 | Всплеск агрегированного OI | ⏳ |
| 2 | Крупные ликвидации (WebSocket) | ⏳ |

Источники: Binance + Bybit. Список монет: топ N по обороту с Binance Futures, обновляется раз в сутки.

## Архитектура

Каждая метрика — самодостаточный модуль: **Fetcher → Cache → Evaluator**.
Новая метрика = новый пакет, существующий код не трогаем.

Общие интерфейсы: `MetricFetcher`, `MetricCache<T>`, `AlertEvaluator`.
`UserAlertRunner` собирает все `AlertEvaluator` и прогоняет по каждому юзеру.

Кэш: in-memory реализация `MetricCache<T>`, интерфейс позволяет заменить на Redis без изменения логики.

## Пользовательские настройки

- Таблица `user_alert_settings`: тип алерта, порог, интервал свечи, cooldown, кастомные символы
- Минимальные допустимые значения задаются в `application.yml`
- `alert_history` хранит `user_id` — cooldown независим для каждого юзера

## Веб-панель

Spring MVC + Thymeleaf. Авторизация — токен, выдаётся ботом по команде `/start`.

## Стек

| Компонент | Технология |
|-----------|------------|
| Язык | Kotlin |
| Фреймворк | Spring Boot (MVC) |
| БД | PostgreSQL + Liquibase |
| Кэш | In-memory (интерфейс готов под Redis) |
| Сборка | Gradle (Kotlin DSL) |
| Telegram | telegrambots |
| Веб-панель | Thymeleaf |
