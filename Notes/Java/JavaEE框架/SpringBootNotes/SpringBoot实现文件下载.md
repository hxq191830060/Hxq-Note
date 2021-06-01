```java
@RestController
public class DownLoadController {

    @Value("${file.upload.path}")
    String dir;//文件目录

    /**
     * 
     * @param response 存放Http响应报文的信息
     * @param filename 要下载的文件的名字
     * @return
     */
    @RequestMapping("download")
    public String download(HttpServletResponse response, @RequestParam("filename")String filename){
        String path=dir+filename;
        File file=new File(path);
        if(!file.exists()){
            return "文件不存在";
        }
        response.reset();//Http响应报文重新设置
        response.setStatus(200);//设置Http响应状态码
        response.setContentType("application/pdf");//设置返回的请求体中的内容格式

        response.setContentLength((int)file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + path );
        //将文件内容以字节流的形式，写入Http响应报文的响应体中
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载失败";
        }
        return "下载成功";
    }
}

```