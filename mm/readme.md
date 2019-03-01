# mm-zookeeper
## entity
* ZkResult
    * 该实体作为zkAPI所有操作的返回值
    * `boolean stat`：该次操作是否成功
    * `List<String> data`：该次操作返回的数据
    * `String msg`：该次操作的说明
    * `String time`：该次操作的时间
    
## zkAPI
* api
    * 获取路径下最优服务器的ip:端口号
        ```java
        @RequestMapping("/zkGetBestServer")
        public ZkResult zkGetBestServer(String path)
        ```
        * `String path`：节点路径名