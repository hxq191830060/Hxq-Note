* 前后端分离  
   * 前端那里，用户操作发出请求，前端请求后端接口，后端将接口封装为API，然后将前端需要的数据以JSON形式返回，前端接受到JSON数据后渲染页面
   * 后端使用 Spring Boot 控制器返回 JSON 十分简单，给方法添加个注解，就能将返回值序列化为 JSON。
    * 也就是说，后端只需要给前端提供一个接口即可
    
* RESTful
  后端接口的编写遵循**RESTful风格**
  SpringBoot开发后端接口
```java
@RestController // 该类是一个Controller bean,可以响应Http请求,并且该bean内所有方法的返回值都会被转换为JSON格式,直接写入响应体
public class GoodsController {
	@Autowired // 自动装配goodsService
	private GoodsService goodsService;
	/**
	 * 查询商品信息 
	 * 1、@GetMapping表示可以使用get方法请求该api
	 * 2、"/goods/{id}"表示请求路径为/goods/{id}的形式，其中{id}为占位符
	 * 3、@PathVariable("id")表示将占位符{id}的值传递给id 
	 * 4、也就是说/goods/123请求的话，会将123传递给参数id
	 */
	@GetMapping("/goods/{id}")
	public GoodsDo getOne(@PathVariable("id") long id) {
		return goodsService.getGoodsById(id);
	}
	/**
	 * 查询商品列表，使用get方法
	 */
	@GetMapping("/goods")
	public List<GoodsDo> getList() {
		return goodsService.getGoodsList();
	}
	/**
	 * 新增商品 
	 * 1、@PostMapping表示使用post方法
	 * 2、@RequestBody表示将请求中的json信息转换为GoodsDo类型的对象信息，该转换也是由SpringMVC自动完成的
	 */
	@PostMapping("/goods")
	public void add(@RequestBody GoodsDo goods) {
		goodsService.addGoods(goods);
	}
	/**
	 * 修改商品
	 */
	@PutMapping("/goods/{id}")
	public void update(@PathVariable("id") long id, @RequestBody GoodsDo goods) {
		// 修改指定id的商品信息
		goods.setId(id);
		goodsService.editGoods(goods);
	}
	/**
	 * 删除商品
	 */
	@DeleteMapping("/goods/{id}")
	public void delete(@PathVariable("id") long id) {
		goodsService.removeGoods(id);
	}
}
```
