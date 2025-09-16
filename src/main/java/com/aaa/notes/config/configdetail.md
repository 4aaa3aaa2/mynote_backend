请求到达
↓
TraceIdFilter (生成追踪ID)
↓
CORS 处理 (跨域验证)
↓
TokenInterceptor (Token验证)
├─ /login, /error → 跳过验证
└─ 其他路径 → 验证Token
↓
Controller 处理
↓
返回响应