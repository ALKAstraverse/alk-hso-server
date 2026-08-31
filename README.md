<h2 align="center">CÁCH CHẠY SERVER</h2>

**Bước 1:** Cài đặt Java 17

```bash
pkg install openjdk-17 -y
```

**Bước 2:** Truy cập thư mục chứa source server

```bash
cd /storage/download/hso
```

**Bước 3:** Chạy server

```bash
java -jar -server target/HSO_Re_2-1.0-jar-with-dependencies.jar
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
## ⚠️ Disclaimer

> **Mục đích sử dụng:** Source code này được công khai nhằm mục đích **học tập, nghiên cứu và tham khảo**.

- Không sử dụng source code cho các mục đích **vi phạm pháp luật**.
- Người sử dụng tự chịu trách nhiệm về mọi hoạt động và hành vi phát sinh từ việc sử dụng source code.
- Tác giả không chịu trách nhiệm đối với bất kỳ thiệt hại hoặc hành vi sử dụng sai mục đích nào.
- Vui lòng tuân thủ pháp luật hiện hành và các quy định liên quan khi sử dụng dự án.

**By using this source code, you agree to use it for lawful purposes only.**
