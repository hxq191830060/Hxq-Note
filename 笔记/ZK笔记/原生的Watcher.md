getData(),exists(),getChildren()可以设置Watcher



```
   /**
             * set 事件触发（修改节点值）
             * get 不触发
             * 对一级子节点操作不触发
             * 删除当前节点触发，如果是同步会抛异常（KeeperErrorCode = NoNode），再创建再删除不再触发
             * 创建节点不触发，因为getData的前提是节点存在
             * 同步连接时,exists事件未触发，异步未测试
             */
            zk.getData(PATH, true, null);
            /**
             * get 不触发
             * 对一级子节点操作（CRUD）不触发
             * 删除当前节点触发，未抛异常
             * 创建节点触发，set 事件触发（修改节点值）
             */
            zk.exists(PATH, true);
            /**
             * 删除节点触发，抛出异常
             * set 不触发（修改节点值）
             * get 不触发
             * 删除节点再创建之后 ，操作一级节点不触发
             * 创建一级节点触发
             * 删除一级节点触发
             * 修改一级节点的值不触发
             * 同步连接时,exists事件未触发，异步未测试
             */
            zk.getChildren(PATH, true);

```

