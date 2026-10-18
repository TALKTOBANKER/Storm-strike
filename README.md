# STORMSTRIKE (XE SĂN BÃO) — DỰ ÁN ANDROID CHUẨN GOOGLE PLAY

Ứng dụng game Android 3D hoàn chỉnh được đóng gói từ game Three.js **STORMSTRIKE**, sẵn sàng mở bằng **Android Studio**, cài đặt trực tiếp file **APK phát hành (Signed Release APK)** hoặc tải file **AAB phát hành (Signed Release AAB)** lên **Google Play Console**.

---

## 1. THÔNG TIN CẤU HÌNH PHÁT HÀNH

- **Tên gói (Package Name / Application ID):** `com.stormstrike.app`
- **Tên ứng dụng (App Name):** `STORMSTRIKE`
- **Phiên bản phát hành:** VersionCode `4`, VersionName `1.2.1` (Bản cập nhật mới nhất cho Google Play)
- **Khóa ký phát hành (Keystore):** `stormstrike.jks` (tại thư mục gốc dự án)
- **Alias name:** `key0`
- **Mật khẩu (Store Password / Key Password):** `123456`
- **Min SDK:** `24` (Android 7.0+) | **Target SDK / Compile SDK:** `36` (Google Play 2026)
- **Định dạng màn hình:** Ngang cố định (`sensorLandscape`) hỗ trợ tối ưu trên cả điện thoại và máy tính bảng.

---

## 2. HIỆU ỨNG RUNG CAMERA (CAMERA SHAKE) & NÂNG CẤP GAMEPLAY

1. **Rung lắc khi trúng đòn (Damage Impact Shake):**
   - Khi phi cơ bị trúng đạn đối thủ hoặc va chạm với tâm lốc xoáy, hiệu ứng camera trauma được kích hoạt ngay lập tức, tạo độ nảy và rung giật phi cơ chân thực theo đồ thị phi tuyến tính.
2. **Nhiễu động gần tâm lốc xoáy (Vortex Turbulence Rumble):**
   - Khi bay vào bán kính nguy hiểm (< 65m) quanh tâm các cơn lốc xoáy khổng lồ, góc quay và tọa độ camera sẽ tự động rung nhẹ liên tục theo cường độ gió bão tăng dần khi áp sát tâm lốc.
3. **Phản hồi khi hút năng lượng (Siphon Feedback):**
   - Khi nhấn hút năng lượng (SIPHON) thành công, hiệu ứng burst vàng kết hợp rung camera và rung xúc giác (Haptic vibration) tạo cảm giác cực kỳ thỏa mãn.
4. **Nút "VÀO TRẬN (CHƠI NGAY)" ngay tại Menu:**
   - Người chơi có thể bấm ngay nút `[ 🚀 VÀO TRẬN (CHƠI NGAY) ]` nổi bật ở đầu trang để lập tức tham chiến mà không cần qua nhiều bước.
   - Bố cục Menu dạng thẻ cuộn mượt mà trên tất cả màn hình ngang, có khoảng đệm chống che khuất bởi banner quảng cáo.

---

## 3. CÁC TỆP ĐẦU RA ĐÃ BUILD VÀ KÝ THÀNH CÔNG

Tất cả các tệp bên dưới đã được build và ký hoàn tất bằng khóa `stormstrike.jks`:

| Tệp | Đường dẫn | Dung lượng | Mô tả |
| :--- | :--- | :--- | :--- |
| **Signed Release APK** | `app/build/outputs/apk/release/app-release.apk` | ~25 MB | Cài trực tiếp lên điện thoại Android để chơi |
| **Signed Release AAB** | `app/build/outputs/bundle/release/app-release.aab` | ~25 MB | Tải trực tiếp lên Google Play Console |
| **Debug APK** | `app/build/outputs/apk/debug/app-debug.apk` | ~32 MB | Bản cài thử nghiệm nội bộ cho nhà phát triển |

---

## 4. CẤU HÌNH QUẢNG CÁO ADMOB ĐÃ TÍCH HỢP

- **AdMob App ID:** `ca-app-pub-3785297962335569~7993443539`
- **Banner Ad Unit ID:** `ca-app-pub-3785297962335569/4331821111`
  - Tự động hiển thị ở cạnh dưới khi ở Menu / Hangar / Missions / Pause.
  - Tự động ẩn đi khi đang bay chiến đấu để không che khuất cần điều khiển cảm ứng.
- **Interstitial Ad Unit ID:** `ca-app-pub-3785297962335569/2488256404`
  - Tự động tải trước và hiển thị khi hoàn thành hoặc thất bại nhiệm vụ.

---

## 5. HƯỚNG DẪN BUILD LẠI BẰNG LỆNH HOẶC ANDROID STUDIO

### Bằng Terminal:
- **Build Release APK:**
  ```bash
  gradle :app:assembleRelease
  ```
- **Build Release App Bundle (AAB):**
  ```bash
  gradle :app:bundleRelease
  ```

### Bằng Android Studio:
1. Mở thư mục dự án bằng **Android Studio**.
2. Chọn **Build -> Generate Signed Bundle / APK...**
3. Chọn file `stormstrike.jks`, alias `key0`, password `123456`.
4. Chọn variant **release** -> Finish.
