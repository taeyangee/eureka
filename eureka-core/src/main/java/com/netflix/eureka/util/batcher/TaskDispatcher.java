package com.netflix.eureka.util.batcher;

/** 任务分发器接口：
 * Task dispatcher takes task from clients, and delegates their execution to a configurable number of workers.
 * The task can be processed one at a time or in batches. Only non-expired tasks are executed, and if a newer
 * task with the same id is scheduled for execution, the old one is deleted. Lazy dispatch of work (only on demand)
 * to workers, guarantees that data are always up to date, and no stale task processing takes place.
 * <h3>Task processor</h3>  用户代码需要实现TaskProcessor接口
 * A client of this component must provide an implementation of {@link TaskProcessor} interface, which will do
 * the actual work of task processing. This implementation must be thread safe, as it is called concurrently by
 * multiple threads.
 * <h3>Execution modes</h3> TaskDispatchers提供构造功能: 构造群发任务分配器/构造单发任务分配器
 * To create non batched executor call {@link TaskDispatchers#createNonBatchingTaskDispatcher(String, int, int, long, long, TaskProcessor)}
 * method. Batched executor is created by {@link TaskDispatchers#createBatchingTaskDispatcher(String, int, int, int, long, long, TaskProcessor)}.
 *
 * @author Tomasz Bak
 */
public interface TaskDispatcher<ID, T> {

    void process(ID id, T task, long expiryTime); /* expiryTime：标识任务的过期时间， 过期的任务不会执行 */

    void shutdown();
}
