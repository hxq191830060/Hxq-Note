系统初始化时，告诉 Spring Security 访问路径所需要的对应权限。   
登录时，告诉 Spring Security 真实用户名和密码。  
登录成功时，告诉 Spring Security 当前用户具备的权限。    
用户访问接口时，Spring Security 已经知道用户具备的权限，也知道访问路径需要的对应权限，所以自动判断能否访问。   