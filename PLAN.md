# Crypto Alerts Telegram Bot — План

## Алерты (v1.0)

| # | Тип | Биржи |
|---|-----|-------|
| 1 | Всплеск агрегированного Open Interest | Binance + Bybit |
| 2 | Крупные ликвидации | Binance + Bybit |
| 3 | Long/Short Ratio на экстремуме | Binance + Bybit |
| 4 | Резкое движение цены | Binance + Bybit |

## Архитектура

### Принципы
- Каждая метрика — самодостаточный модуль (fetcher + cache + evaluator)
- Добавить новую метрику = новый пакет, существующий код не трогаем (OCP)
- Общие интерфейсы только для оркестрации

### Модульная структура метрик

```
price/
  PriceDataFetcher       — REST, обновляет PriceCache
  PriceCache             — типизированный, хранит List<Kline>
  PriceEvaluator         — читает PriceCache, оценивает по настройкам юзера

open-interest/
  OiDataFetcher
  OiCache
  OiEvaluator

long-short/
  LongShortDataFetcher
  LongShortCache
  LongShortEvaluator

liquidations/
  LiquidationAccumulator — WebSocket, пишет в LiquidationCache
  LiquidationCache
  LiquidationEvaluator
```

### Общие интерфейсы

```kotlin
interface MetricFetcher               // run() — обновить кэш
interface AlertEvaluator              // evaluate(user) — проверить и уведомить
interface MetricCache<T> {            // абстракция кэша
    fun put(symbol: String, exchange: String, data: List<T>)
    fun get(symbol: String, exchange: String): List<T>
}
```

`UserAlertRunner` знает только об интерфейсах, собирает реализации через `List<AlertEvaluator>`.

### Кэш

- **Сейчас**: in-memory реализация `MetricCache<T>` (per-metric, типизированный)
- **При масштабировании**: заменяется на Redis-реализацию того же интерфейса без изменения бизнес-логики
- Redis даёт: горизонтальное масштабирование, TTL, Pub/Sub для WebSocket-метрик, выживание при перезапуске

### Фетчер и расписание

- Глобальный фетчер тикает на минимально допустимом интервале (настраивается)
- Фиксированный набор кэшируемых интервалов свечей: `1m, 5m, 15m, 1h, 4h`
- Эвалуатор читает из кэша — API запросы не зависят от числа пользователей

### Пользовательские настройки

Каждый юзер кастомизирует свои алерты. Ограничения задаются в `application.yml`:

```yaml
alerts:
  user-limits:
    min-threshold: 0.3
    min-cooldown: 5m
    allowed-candle-intervals: [1m, 5m, 15m, 1h, 4h]
```

Таблица `user_alert_settings`:
- `user_id` (FK), `alert_type`, `enabled`, `threshold`, `candle_interval`, `candle_limit`, `cooldown_minutes`, `custom_symbols` (json, null = глобальный топ-N)

`alert_history` расширяется полем `user_id` — cooldown считается отдельно для каждого юзера.

### Веб-панель

- **Стек**: Spring MVC + Thymeleaf
- **Авторизация**: токен, выдаётся ботом при `/start` в виде ссылки
- Пользователь управляет своими настройками алертов через браузер

### Список монет
Топ N монет по обороту (quoteVolume за 24ч) с Binance Futures: `GET /fapi/v1/ticker/24hr`.
Список обновляется раз в сутки. N — настраиваемо (дефолт: 20). Только USDT-маржинальные пары.
Юзер может переопределить список своими символами (`custom_symbols`).

### Хранение данных
- **PostgreSQL** — история алертов (с `user_id`), пользователи, настройки
- **In-memory** — кэш рыночных данных (с интерфейсом под замену на Redis)

### Порядок разработки алертов
1. #4 Резкое движение цены — ✅ готово
2. #3 Long/Short Ratio
3. #1 Всплеск OI
4. #2 Крупные ликвидации — WebSocket, сложнее всего

## Стек

| Компонент | Технология |
|-----------|------------|
| Язык | Kotlin |
| Фреймворк | Spring Boot (MVC) |
| БД | PostgreSQL |
| Кэш (будущее) | Redis |
| Миграции | Liquibase |
| Сборка | Gradle (Kotlin DSL) |
| Telegram | telegrambots |
| Веб-панель | Thymeleaf |
