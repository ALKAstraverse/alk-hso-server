[center][size=18][b]CÁCH CHẠY SERVER[/b][/size][/center]

[b]Bước 1:[/b] Truy cập thư mục chứa source server
[code]
cd /storage/download/hso2
[/code]

[b]Bước 2:[/b] Chạy server
[code]
java -jar -server target/HSO_mint_2-1.0-jar-with-dependencies.jar
[/code]


[center][size=18][b]CÁCH SỬA IP FILE CLIENT[/b][/size][/center]

[b]Sửa IP:[/b]
[code]
File: dw.class
Dòng: 51, 63, ...
[/code]

[b]Sửa Port:[/b]
[code]
File: ft.class
Dòng: 273 -> 278
Port được mã hóa bằng HEX
Ví dụ:
19129 = 0x4AB9
[/code]


[center][size=18][b]CẤU HÌNH KHÁC[/b][/size][/center]

[b]Kết nối database:[/b]
[code]
File cấu hình: hso.conf
[/code]

[b]Client:[/b]
[code]
client/hso-client.jar
[/code]
