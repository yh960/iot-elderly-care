$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/elderly_care?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME = "admin"
$env:SPRING_DATASOURCE_PASSWORD = "admin123"
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = "update"
$env:AI_API_KEY = "0c31e6fa6c1d4cb8838e378d2e4ae2cb.AH39rZIgE0l6ks3m"
$env:AI_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
$env:AI_MODEL = "glm-4-flash"

Write-Host "================================================"
Write-Host "正在启动后端服务..."
Write-Host "================================================"
Write-Host ""
Write-Host "数据库连接: $($env:SPRING_DATASOURCE_URL)"
Write-Host ""

java -Xms256m -Xmx512m -jar elderly-care-backend/target/elderly-care-backend-0.0.1-SNAPSHOT.jar
