# 從 .NET 搬移 API 整合測試框架到 Java

## 目標

這套框架對應原 .NET 專案的 `WebApplicationFactory`、`IntegrationTestBase`、測試資料建立器與回應解析器。Java 版使用 Spring Boot Test、`TestRestTemplate`、H2 PostgreSQL compatibility mode、JUnit 5 與 AssertJ。完整測試的硬上限是 900 秒（15 分鐘）。

目前 Java production code 只有一支 business API：`POST /api/v1/auth/login`；其正常、驗證失敗與認證失敗情境皆已有測試。Items、Loans 等測試要等相對應 Java API 移植後再逐支加入，不能直接複製 C# 測試而測到不存在的端點。

## 對照關係

| .NET | Java |
| --- | --- |
| `LendingWebApplicationFactory` | `@SpringBootTest(webEnvironment = RANDOM_PORT)` |
| `IntegrationTestBase` | `AbstractApiIntegrationTest` |
| `[WriteTest]` | `@ApiIntegrationTest` |
| `ResponseParser` | `ApiResponseAssertions` + Jackson `JsonNode` |
| EF Core seed | `ApiTestData` + `JdbcClient` |
| test database reset | `DatabaseCleaner` + FK cascade |
| API 人工清單 | `ApiEndpointCoverageTests` 自動比對 Spring mappings |

## 執行方式

先確認 `JAVA_HOME` 指向 JDK 21 以上；這台機器目前 PowerShell 預設找到的是 Java 8，會在編譯前就失敗：

```powershell
java -version
```

在 Java 專案根目錄執行：

```powershell
.\mvnw.cmd test
```

只跑 API integration tests：

```powershell
.\mvnw.cmd test -Dgroups=integration
```

只跑快速 unit / architecture tests：

```powershell
.\mvnw.cmd test -DexcludedGroups=integration
```

Surefire 會重用同一 JVM 與 Spring Context，避免每個測試重啟應用程式；單一測試預設逾時 30 秒，整個 fork 最長 900 秒。Integration tests 目前共用資料庫且每次會清資料，因此先保持單 fork。未來要平行化，必須先把每個 worker 改成獨立 schema/database，否則測試會互相刪資料。

## 新增一支 API 時的固定流程

1. 在 `src/test/java/.../integration/<module>` 建立 `*ApiIntegrationTests` 並繼承 `AbstractApiIntegrationTest`。
2. 先寫一個 happy-path：安排最少資料、送出真實 HTTP request、驗證 HTTP status、通用 envelope，以及關鍵 response data。
3. 使用 `ApiResponseAssertions.assertSuccessful(...)` 驗證成功 envelope；錯誤案例仍明確驗證 status 與 `errorCode`。
4. 將 `METHOD /path` 加進 `ApiEndpointCoverageTests.TESTED_API_INVENTORY`。若只加 Controller 沒加 inventory，coverage test 會失敗；先加 inventory 卻沒寫 happy-path 測試，code review 不應通過。
5. 若新 aggregate 有資料表，擴充 `DatabaseCleaner`，依 FK child-to-parent 順序清除，或從 aggregate root 使用 cascade。
6. 本機跑 integration tests，再跑完整 `mvnw.cmd test`，確認總時間遠低於 15 分鐘並保留成長空間。

## 大型專案的時間預算

- Unit / architecture：2 分鐘。
- API integration：10 分鐘。
- 啟動、報告與 CI 波動：3 分鐘。

每支 API 至少一個 happy-path，但不要為每個案例重啟 Spring。外部 MinIO、OAuth、gRPC 在 integration profile 關閉或以 deterministic fake 取代；真正依賴容器或外部服務的 end-to-end 測試應拆成另一個較低頻率 pipeline。當 integration 超過約 8 分鐘時，先依 module 分片到多個 CI job；若要同 JVM 平行，先完成資料隔離。
