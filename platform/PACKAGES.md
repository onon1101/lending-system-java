# Platform 套件分類

共用程式碼保留在 `platform` Maven 模組，依職責分成以下套件：

| 套件 | 職責 |
| --- | --- |
| `configuration` | 整體執行設定，例如非同步與交易設定 |
| `shared` | 共用型別、命令介面、時間與 Email 正規化工具 |
| `application` | 目前使用者、冪等處理與稽核等應用機制 |
| `infrastructure` | JWT、R2DBC、Redis、RustFS、寄信與影片服務的技術實作 |
| `web` | HTTP 回應、例外處理、請求上下文、分頁與 Web 設定 |

## 放置原則

- 技術專屬的 Configuration、Properties、HealthIndicator 跟著該技術放置。
- 應用機制的介面放在 `application`，Redis、R2DBC 等實作放在 `infrastructure`。
- JWT 相依的 `TokenAuditEventAttribute` 放在 `infrastructure.audit.attributes`，避免 `application` 反向依賴技術實作。
- `shared` 不依賴應用機制、技術實作或 HTTP 層。
- 套件名稱使用小寫；測試的套件與目錄對應受測類別。
- 這五類目前使用 Spring Modulith 的 OPEN 模組宣告，延續原共用模組的公開存取方式。子套件仍保留適用的 NamedInterface 宣告。

分類針對共用程式碼；`auth`、`user`、`item` 的功能套件維持原有結構。
