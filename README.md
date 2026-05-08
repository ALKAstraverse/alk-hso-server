<h2 align="center">CÁCH CHẠY SERVER</h2>

**Bước 1:** Cài đặt Java 17

```bash
pkg install openjdk-17
```

**Bước 2:** Truy cập thư mục chứa source server

```bash
cd /storage/download/hso2
```

**Bước 3:** Chạy server

```bash
java -jar -server target/HSO_mint_2-1.0-jar-with-dependencies.jar
```


<h2 align="center">CÁCH SỬA IP FILE CLIENT</h2>

**Sửa IP:**
```text
File: dw.class
Dòng: 51, 63, ...
```

**Sửa Port:**
```text
File: ft.class
Dòng: 273 -> 278
Port được mã hóa bằng HEX

Ví dụ:
19129 = 0x4AB9
```


<h2 align="center">CẤU HÌNH KHÁC</h2>

**Kết nối database**
```text
File cấu hình: hso.conf
```

**Client**
```text
client/hso-client.jar
```
