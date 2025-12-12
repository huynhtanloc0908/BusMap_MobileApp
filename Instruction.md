# HƯỚNG DẪN SỬ DỤNG KHI CLONE CODE VỀ

- Đầu tiên tạo một thư mục trên máy bạn
  + ví dụ : Example
  + Trong thư mục đó bạn sử dụng git clone để clone code về
  git clone https://github.com/huynhtanloc0908/BusMap_MobileApp.git
- Lúc này trong thư mục Example của bạn sẽ có 2 thư mục
  1. UI_Login
  2. PHP
- Sau đó bạn sẽ copy thư mục UI_Login đó ra khỏi folder Example và lưu trên máy bạn
  + ví dụ: E:\UI_Login
- Còn trong thư mục PHP sẽ có 5 file PHP. Lúc này bạn sẽ vào đường dẫn C:\xampp\htdocs\ (có thể khác tùy chỗ bạn lưu ứng dụng xampp)
  + Khi vào tới đường dẫn đó bạn tạo cho tui một folder busmap_api. Vào folder busmap_api paste 5 file PHP vào đây. Lúc này bạn sẽ có
  + C:\xampp\htdocs\busmap_api\*.php
- Trên XAMPP -> phpMyAdmin -> Create Database (users_busmap) -> Create table (users)
  + Tạo bảng theo cấu trúc này
  <img width="1222" height="261" alt="image" src="https://github.com/user-attachments/assets/dc1f44e4-cbed-4599-a9fe-1b6c34eca73d" />
- Xong rồi bạn sẽ mở folder UI_Login trong Android Studio. Vào folder api -> RetrofitClient
  + Kiếm dòng code này. Bạn sẽ đổi base url theo ip và port bạn chạy.
  + Genymotion: 10.0.3.2
  + Máy ảo trên đt: 10.0.2.2
  + Trực tiếp trên đt: localhost
  + Port: 8080 là port tui dùng để chạy Apache server. Bạn chạy trên port nào thì đổi lại chỗ này
  + private const val BASE_URL = "http://10.0.2.2:8080/busmap_api/"


Nếu là theo các bước thì bạn có  thể chạy ứng dụng trục tiếp được rồi
