# vertx-gateway
本项目是一个使用Vertx的项目，使用TCP和HTTP搭建一个分布式的高可用的企业级网关，可以对请求进行筛选和判断

## 技术架构
Vertx-Java 3.8.5

## 适用场景
高并发下控制并发并进行信控计费和日志记录

## 配套产品
MySQL > 5.7

## 安全性
请求与响应会强制使用AES加密来保证安全；AES密钥会通过RSA加密和解密来传递；同时为了避免请求来源不明或者被窜改，请求中会有Sign参数来进行参数校验，同时会对来源IP进行合法性校验

## 优势
采用基于事件驱动的Vertx框架，吞吐量高，处理能力强。后续将支持非Java环境（自带JRE）的模式和非必须MYSQL数据库的格式（通过SQL_LITE或者文件）

## 合作方法
发送邮件到 mathcecwang@gmail.com 或着添加个人微信 ![](./resouce/images/wechat01.png)
