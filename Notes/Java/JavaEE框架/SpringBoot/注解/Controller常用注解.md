**三.处理常见的 HTTP 请求类型**  
Http的5种常见请求——get,post,delete,put,patch
1. get——请求服务器上的资源
2. post——在服务器上创建一个新资源
3. delete——删除服务器上的某个资源
4. put——更新服务器上的资源

对应的注解(对@RequestMapping的简化)——注解方法
1. @GetMapping("/users") 等价于@RequestMapping(value="/users",method=RequestMethod.GET)
2. @PostMapping("/users") 等价于@RequestMapping(value="/users",method=RequestMethod.POST)
3. @PutMapping("/users/{userId}") 等价于@RequestMapping(value="/users/{userId}",method=RequestMethod.PUT)
4. @DeleteMapping("/users/{userId}")等价于@RequestMapping(value="/users/{userId}",method=RequestMethod.DELETE)
5. @PatchMapping("/profile")  
