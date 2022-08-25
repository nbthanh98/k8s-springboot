# Kubernetes vỡ lòng

## Nội dung gồm:

1. Giới thiệu một số thành phần cơ bản của Kubernetes.
2. Deploy Spring boot application lên Kubernetes.
3. Cách để debug nếu có lỗi khi deploy service lên Kubernetes.

## 1. Giới thiệu một số thành phần cơ bản của Kubernetes.

Phần này sẽ tập chung giới thiệu một số thành phần cơ bản của Kubernetes và những gì mà Kubernetes có thể làm được.

&#x20;<img src="images/2.png" alt="" data-size="original">

Trên hình thì thấy `Kubernetes architecture` gồm có như:

1. Kubernetes Master (Master Node hay control plane).
   * **API Server**: Trong Kubernetes thì các tương tác giữa các thành phần của kubernetes đều thông qua `API Server`. Khi ta chạy lệnh `kubectl ...` thì cũng là đang tương tác với `API Server` thông qua RestAPI. Ngoài ra `API Server` cũng xử lý các công việc khác như `Authen` -> `Author` -> `Validate YAML`.
   * **Scheduler**: Khi có một cái task cần làm (vd: deploy service A) thì `API server` sẽ assign task cho `Scheduler` để tìm kiếm cái Worker-Node nào phù hợp nhất để service A đc deploy lên. Một vài tiêu chí như: CPU, Memory, Disk,.. .
   * **Controller - Manager**: Ông này thì dùng để quản lý các thành phần như: `Node controller` (quản lý việc healthy của Node), `ReplicationController` (quản lý số lượng Pod), `Endpoint controller` (quản lý các IP của Pod), ..
   * **etcd**: Quản lý các trạng thái của Kubernetes cluster.
2. Các Worker Node.
   * **Pod**: Trong `Docker` thì các `containers` sẽ là đơn vị nhỏ nhất là nơi các service chạy. Còn trong `Kubernetes` thì `Pod` lại là đơn vị nhỏ nhất, bên trong `Pod` có thể chứa một hoặc nhiều các `containers`.
   * **Container runtime**: Pod trong Kubernetes sẽ không phải dùng để chạy các application, mà các containers mới là dùng để chạy các application. Pod chỉ là wrap một hoặc nhiều contaiers lại nên sẽ cần có `container runtime` (có thể là Docker, containerd,..) làm nhiệm vụ pulling image, start, stop các containers.
   * **Kubelet**: Ông này có tác dụng là để giao tiếp giữa `Master Node` và `Worker Node`, ngồi trực xem ông `Master Node` có giao cho cái task gì không (VD Deploy 1 service nào đó chẳng hạn), kubelet sẽ dựa trên `PodSpec` đc cung cấp để đảm bảo các các containers chạy đúng như `PodSpec` và report lại cho ông `Master Node`.
   * **Kube-proxy**:
