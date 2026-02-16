# 编程面试题合集

本仓库用于整理各类编程面试题，重点包含多线程题，也会持续增加其他类型题目。

## 目录规划
- `src/multi_threads/`：多线程题
- 其他类型题目将按主题新增目录

## 题目列表

### 多线程
- 顺序打印 1-100（`src/multi_threads/print_1_to_100/`）
- 其他题型示例：打印 1A2B3C、ABCABC 等（后续补充）

## 顺序打印 1-100 的实现方式对比
下面对比几种常见实现方式的优劣，并给出面试场景下的建议。

### synchronized + wait/notifyAll
- 优点：JDK 基础能力，语义直观，面试官容易理解。
- 缺点：notifyAll 有惊群问题，细粒度控制弱；手写易出错（丢信号或死等）。

### ReentrantLock + Condition
- 优点：语义清晰；可以为不同线程建立条件，减少无效唤醒；更易扩展。
- 缺点：代码略复杂，需要显式 lock/unlock。

### Semaphore
- 优点：模型简单（令牌轮转），不依赖 wait/notify，逻辑清晰。
- 缺点：与轮转策略强绑定；异常处理时要注意释放许可。

### BlockingQueue
- 优点：将轮转抽象为令牌传递，概念好讲；阻塞语义可靠。
- 缺点：为简单问题引入队列，偏重；性能和复杂度略高。

### CountDownLatch
- 结论：不适合。CountDownLatch 一次性使用，不能复用。虽然可以通过替换闩锁数组模拟轮转，但不是典型用法，面试易被追问。

## 面试推荐
- 首选：synchronized + wait/notify 或 ReentrantLock + Condition。
- 如果偏基础：用 synchronized + wait/notify，讲清条件等待与轮转逻辑。
- 如果偏并发工具类：用 ReentrantLock + Condition，体现条件变量控制能力。
- 不推荐用 CountDownLatch 作为主要方案（除非专门讨论为什么不合适）。
