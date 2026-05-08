## Cách Chạy Server:
Bước 1: Truy cập thư mục chứa source server:
$ cd /storage/download/hso2
Bước 2: Chạy server:
$ java -jar -server target/HSO_mint_2-1.0-jar-with-dependencies.jar

## Cách Sửa Ip File Client:
+ Sửa ip: file dw.class, dòng51,63,.....
+ Sủa port: file ft.class, dòng 273 -> 278, port mã hóa bằng hex (vd 19129 là 0xab9)

[Kết nối server với database trong file `hso.conf`]
