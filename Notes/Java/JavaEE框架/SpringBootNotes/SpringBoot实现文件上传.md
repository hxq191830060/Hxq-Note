**1. 配置文件配置上传文件参数**  
```
spring:
  servlet:
    multipart:
      max-file-size: 100MB  //允许的最大文件大小
      max-request-size: 10MB //允许的最大请求大小
file:
  upload:
    path: /  //文件存储目录，默认为D盘下
```
**2. 文件如何从前端发送到服务器**  
通过《input type="file" name="a"》向后端发送文件   
Http请求体中会出现 a：binaryFile 的键值对，后端可以通过这个键值对来获取文件的二进制流   


**3. 控制器配置**   
**可以使用@RequestPart，也可以使用@RequestParam**   
```java
@RestController
public class UploadController {

    @Value("${file.upload.path}")
    private String filePath;//从配置文件取得文件存储目录

    /**
     *
     * @param file 前端上传的文件对象
     * @return
     */
    @PostMapping("/upload")
    public String create(@RequestPart MultipartFile file) throws IOException {
        //获取上传的文件对象的文件名
        String fileName=file.getOriginalFilename();

        //文件存储目录+文件名=文件存储路径
        String path=filePath.concat(fileName);

        //创建文件对象
        File dest=new File(path);

        //将上传的文件对象的内容进行拷贝，拷贝到本地的文件对象上
        Files.copy(file.getInputStream(),dest.toPath());

        return "Upload Success"+dest.getAbsolutePath();
    }
}
``` 

**4. 实现多文件上传**  
* 多个input标签的type为file，name相同  
* 控制器方法的MultipartFile[]的参数名必须与input标签的name相同  
```html
<html>
<head lang="en">
    <meta charset="UTF-8" />
    <title>文件上传页面 - didispace.com</title>
</head>
<body>
<h1>文件上传页面</h1>
<form method="post" action="/upload" enctype="multipart/form-data">
    文件1：<input type="file" name="files"><br>
    文件2：<input type="file" name="files"><br>
    <hr>
    <input type="submit" value="提交">
</form>
</body>
</html>
```
```
    /**
     * 
     * @param files 必须与input标签的name属性相同
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public String create(@RequestPart MultipartFile[] files) throws IOException {
        StringBuffer message = new StringBuffer();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String filePath = path + fileName;

            File dest = new File(filePath);
            Files.copy(file.getInputStream(), dest.toPath());
            message.append("Upload file success : " + dest.getAbsolutePath()).append("<br>");
        }
        return message.toString();
    }
```


**注意**  
* 如果上传的文件在服务器中已经存在，那么服务器会报错，无法正常的响应，所以要对文件是否存在进行判定   