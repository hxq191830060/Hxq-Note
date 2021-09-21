```
user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;

    upstream globalIdConsole {
        server 10.0.10.169:8080;
        server 10.0.10.197:8080;
    }

    server {
        listen 80 default_server;
        location / {
            proxy_pass http://globalIdConsole;
        }
    }
    server {
        listen 443 ssl default_server;
	ssl_protocols TLSv1.2;
	ssl_certificate     /dev/shm/ssl/zoomdev_chained.crt;
        ssl_certificate_key /dev/shm/ssl/zoomdev_us.key;
        location / {
            proxy_pass http://globalIdConsole;
        }
   }
}
```

