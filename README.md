# **Kubernetes vỡ lòng**

## **Nội dung gồm:**

1. Giới thiệu một số thành phần cơ bản của Kubernetes.
2. Deploy Spring boot application lên Kubernetes.
3. Cách để debug nếu có lỗi khi deploy service lên Kubernetes.

## **1. Giới thiệu một số thành phần cơ bản của Kubernetes.**

Phần này sẽ tập chung giới thiệu một số thành phần cơ bản của Kubernetes và những gì mà Kubernetes có thể làm được.

&#x20;<img src="images/2.png" alt="" data-size="original">

Trên hình thì thấy `Kubernetes architecture` gồm có như:

**1. Kubernetes Master (Master Node hay control plane).**
   * **API Server**: Trong Kubernetes thì các tương tác giữa các thành phần của kubernetes đều thông qua `API Server`. Khi ta chạy lệnh `kubectl ...` thì cũng là đang tương tác với `API Server` thông qua RestAPI. Ngoài ra `API Server` cũng xử lý các công việc khác như `Authen` -> `Author` -> `Validate YAML`.
   * **Scheduler**: Khi có một cái task cần làm (vd: deploy service A) thì `API server` sẽ assign task cho `Scheduler` để tìm kiếm cái Worker-Node nào phù hợp nhất để service A đc deploy lên. Một vài tiêu chí như: CPU, Memory, Disk,.. .
   * **Controller - Manager**: Ông này thì dùng để quản lý các thành phần như: `Node controller` (quản lý việc healthy của Node), `ReplicationController` (quản lý số lượng Pod), `Endpoint controller` (quản lý các IP của Pod), ..
   * **etcd**: Quản lý các trạng thái của Kubernetes cluster.

**2. Các Worker Node.**
   * **Pod**: Trong `Docker` thì các `containers` sẽ là đơn vị nhỏ nhất là nơi các service chạy. Còn trong `Kubernetes` thì `Pod` lại là đơn vị nhỏ nhất, bên trong `Pod` có thể chứa một hoặc nhiều các `containers`.
   * **Container runtime**: Pod trong Kubernetes sẽ không phải dùng để chạy các application, mà các containers mới là dùng để chạy các application. Pod chỉ là wrap một hoặc nhiều contaiers lại nên sẽ cần có `container runtime` (có thể là Docker, containerd,..) làm nhiệm vụ pulling image, start, stop các containers.
   * **Kubelet**: Ông này có tác dụng là để giao tiếp giữa `Master Node` và `Worker Node`, ngồi trực xem ông `Master Node` có giao cho cái task gì không (VD Deploy 1 service nào đó chẳng hạn), kubelet sẽ dựa trên `PodSpec` đc cung cấp để đảm bảo các các containers chạy đúng như `PodSpec` và report lại cho ông `Master Node`.
   * **Kube-proxy**:

## **2. Deploy Spring boot application lên Kubernetes.**
Phần này sẽ deploy spring boot service từ docker cho đến Kubernetes.

API:
```java
package com.thanhnb.demok8s.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloK8sController {

    @RequestMapping(value = "/hello-k8s", method = RequestMethod.GET)
    public String helloK8s() {
        return "/hello-k8s";
    }
}

```

DockerFile:
```Dockerfile
FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
WORKDIR /opt
COPY build/libs/demo-k8s-0.0.1-SNAPSHOT.jar /opt/app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
- **FROM**: Dockerfile thì cần define một cái base image layer, trong trường hợp này là `adoptopenjdk/openjdk11:jdk-11.0.2.9-slim`. Trong image này đã có được cài sẵn JDK11, image layer này sẽ đc làm môi trường để chạy lệnh dưới trong Dockerfile.
- **WORKDIR**: Là sẽ set working directory là folder `/opt`. Các lệnh phía dưới Dockerfile sẽ được thực hiện bên trong folder `/opt`.
- **COPY**: Thực hiện copy file jar từ `build/libs/` vào folder `/opt/`.
- **ENTRYPOINT**: Sẽ chạy câu lệnh `"java","-jar","/app.jar"` bên trong containers.

Build image:
```docker
docker build -t hello-k8s -f Dockerfile .

Sending build context to Docker daemon  36.87MB
Step 1/4 : FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
 ---> 9a223081d1a1  
 
# Xong step 1/4 lệnh trên thì được image layer ID: 9a223081d1a1
                                        
Step 2/4 : WORKDIR /opt
 ---> Running in 5f2907646b58
Removing intermediate container 5f2907646b58
 ---> aa838cbddba9

# Step 2 được chạy trong container ID: 5f2907646b58, chạy xong step 2 thì containerID: 5f2907646b58 cũng bị xóa.

Step 3/4 : COPY build/libs/demo-k8s-0.0.1-SNAPSHOT.jar /opt/app.jar
 ---> 8552b72f0e54

# Step 3 chạy xong thì được image layer ID: 8552b72f0e54

Step 4/4 : ENTRYPOINT ["java","-jar","/app.jar"]
 ---> Running in cac2e3c2fc04
Removing intermediate container cac2e3c2fc04
 ---> 057e1422ad43

# Step 4 đc chạy trong containers ID: cac2e3c2fc04, chạy xong thì bị xóa và ra đc image layer ID: 057e1422ad43

Successfully built 057e1422ad43
Successfully tagged hello-k8s:latest

# Gắn tag: hello-k8s:latest cho image layer: 057e1422ad43 và kết thúc build docker image.

docker image -a

REPOSITORY                                               TAG                 IMAGE ID       CREATED          SIZE
hello-k8s                                                latest              057e1422ad43   50 seconds ago   395MB
<none>                                                   <none>              aa838cbddba9   51 seconds ago   358MB
<none>                                                   <none>              8552b72f0e54   51 seconds ago   395MB
adoptopenjdk/openjdk11                                   jdk-11.0.2.9-slim   9a223081d1a1   3 years ago      358MB
```
