# Crypto Alerts Telegram Bot — План

## Алерты (v1.0)

| # | Тип | Биржи |
|---|-----|-------|
| 1 | Всплеск агрегированного Open Interest | Binance + Bybit |
| 2 | Крупные ликвидации | Binance + Bybit |
| 3 | Long/Short Ratio на экстремуме | Binance + Bybit |
| 4 | Резкое движение цены | Binance + Bybit |

## Архитектура

### Слои
- `ExchangeClient` — интерфейс (ISP: узкий, только нужное). Реализации: `BinanceClient`, `BybitClient`
- `MarketDataAggregator` — собирает данные со всех клиентов через `List<ExchangeClient>`
- `*AlertDetector` — только логика детекции, не знает про биржи
- `AlertNotifier` — только отправка в Telegram
- `AlertScheduler` — только тригер по расписанию
- `AlertRepository` — только персистентность

Новая биржа = новый `@Component`-класс, агрегатор подберёт автоматически.

### Конфигурация
Типизированные `@ConfigurationProperties` на каждый тип алерта. Никаких `@Value` россыпью.

```yaml
alerts:
  price:
    interval: 60s        # периодичность опроса
    candle-interval: 15m # интервал свечи (нейтральный формат)
    candle-limit: 3      # N свечей — настраиваемо
    threshold: 2.5       # порог срабатывания в %
    cooldown: 30m        # минимальный интервал между алертами
```

Каждый `ExchangeClient` маппит нейтральный `candle-interval` в свой формат самостоятельно (Binance: `15m`, Bybit: `15`).

### Хранение данных
- **PostgreSQL** — история отправленных алертов (аудит + cooldown между перезапусками)
- **In-memory Map** — кэш последнего алерта внутри сессии (чтобы не ходить в БД на каждой итерации)

### Порядок разработки алертов
1. #4 Резкое движение цены — проще всего, REST + `@Scheduled`
2. #3 Long/Short Ratio — тоже REST, минимальная агрегация
3. #1 Всплеск OI — REST, агрегация Binance + Bybit
4. #2 Крупные ликвидации — WebSocket, сложнее всего

## Стек

| Компонент | Технология |
|-----------|------------|
| Язык | Kotlin |
| Фреймворк | Spring Boot (MVC) |
| БД | PostgreSQL |
| Миграции | Liquibase |
| Сборка | Gradle (Kotlin DSL) |
| Telegram | telegrambots |
