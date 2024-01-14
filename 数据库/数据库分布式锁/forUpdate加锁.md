**for update加锁也是在事务提交时释放**

* for update no wait
  
  * 如果锁被其他事务持有，那么当前事务立刻返回，抛出异常CannotAcquireLockException

* for update wait seconds
  
  * 超时——超时时间内未获得锁，抛出异常CannotAcquireLockException
  
  * 未超时——正常获得锁，执行业务逻辑
  
  * 业务时间执行过长——正常处理


