package com.samsung.lib.requestserializer;

import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class PoolableWorkerThreadFactory<U> implements PoolableObjectFactory<PoolableWorkerThread<U>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PoolableWorkerThreadFactory.class);

  private final PoolableWorkerThreadPool<U> blockingWorkerThreadPool;
  private final AsyncRequestSerializerConfig asyncRequestSerializerConfig;

  private final AtomicInteger atomicInteger = new AtomicInteger(1);

  public PoolableWorkerThreadFactory(final PoolableWorkerThreadPool<U> blockingWorkerThreadPool,
      final AsyncRequestSerializerConfig asyncRequestSerializerConfig) {
    this.blockingWorkerThreadPool = blockingWorkerThreadPool;
    this.asyncRequestSerializerConfig = asyncRequestSerializerConfig;
  }

  @Override
  public PoolableWorkerThread<U> makeObject() throws Exception {
    PoolableWorkerThread<U> poolableWorkerThread = new PoolableWorkerThread<>(blockingWorkerThreadPool,
        asyncRequestSerializerConfig);
    poolableWorkerThread.setName("PoolableWorkerThread #" + atomicInteger.getAndIncrement());
    poolableWorkerThread.start();
    LOGGER.info("Created new PoolableWorkerThread -> " + poolableWorkerThread.getName());
    return poolableWorkerThread;
  }

  @Override
  public void destroyObject(PoolableWorkerThread<U> obj) throws Exception {
    LOGGER.debug("destroy Poolable Worker Therad {} object", obj.getName());
    obj.kill();
  }

  @Override
  public boolean validateObject(PoolableWorkerThread<U> obj) {
    return obj.getWorkQueueSize() == 0;
  }

  @Override
  public void activateObject(PoolableWorkerThread<U> obj) throws Exception {
    obj.activate();
  }

  @Override
  public void passivateObject(PoolableWorkerThread<U> obj) throws Exception {
    obj.passivate();
  }
}
