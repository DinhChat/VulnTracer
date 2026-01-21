# Vulntracer
Link scanservice: [Scan Service](https://github.com/DinhChat/ToolRunnerContainer.git)
## Cấu hình ban đầu mariadb để chạy vulntracer webservice:
phiên bản mariadb: 10.11.15
mysql -u root -p => nhập mật khẩu root
```bash
CREATE USER 'tracer'@'localhost' IDENTIFIED BY 'dinhchat2k4';
CREATE DATABASE vulntracer;
GRANT ALL PRIVILEGES ON vulntracer.* TO 'tracer'@'localhost';
FLUSH PRIVILEGES;
```

## Khởi tạo user root role ADMIN và import CWE:
- Tạo tài khoản như bình thuờng, tài khoản đuợc lưu với role USER, sau đó vào trực tiếp db để thay đổi role cho nó:
``` bash
UPDATE user
SET role = 1
WHERE user_id = <id_cua_user>;
```
- Từ root user ADMIN có thêm chức năng import cwelist cho hệ thống

## Cài đặt Scanservice theo hướng dẫn tại readme: [Scan Service](https://github.com/DinhChat/ToolRunnerContainer.git)
