package com.example.Login;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo.R;

public class DangKyActivity extends AppCompatActivity {
    Button btnDangNhap, btnDangKy;
    SQLiteConnect SQLiteConnect;
    EditText edtTenTK, edtEmailDK, edtSDT, edtMatKhau, edtNgaySinh, edtDiaChi;
    RadioButton radNam, radNu;
    RadioGroup radGioiTinh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnDangNhap = findViewById(R.id.btnDangNhap);
        btnDangNhap.setOnClickListener(view -> {
            Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
            startActivity(intent);
            finish();
        });

        SQLiteConnect = new SQLiteConnect(DangKyActivity.this, "appquanlybanhang.sql", null, 1);
        // Sửa câu lệnh CREATE TABLE để không sử dụng ENUM
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "taikhoan ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ten_tai_khoan VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "so_dien_thoai VARCHAR(15), " +
                "mat_khau VARCHAR(255) NOT NULL, " +
                "gioi_tinh TEXT, " +
                "ngay_sinh DATE, " +
                "dia_chi VARCHAR(255) );";
        SQLiteConnect.queryData(sql);

        btnDangKy = findViewById(R.id.btnDangKy);
        edtTenTK = findViewById(R.id.edtTenTK);
        edtEmailDK = findViewById(R.id.edtEmailDK);
        edtSDT = findViewById(R.id.edtSDT);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        radGioiTinh = findViewById(R.id.radGioiTinh);

        btnDangKy.setOnClickListener(view -> {
            String tenTaiKhoan = edtTenTK.getText().toString().trim();
            String email = edtEmailDK.getText().toString().trim();
            String soDienThoai = edtSDT.getText().toString().trim();
            String matkhau = edtMatKhau.getText().toString().trim();
            String ngaysinh = edtNgaySinh.getText().toString().trim();
            String diachi = edtDiaChi.getText().toString().trim();

            int selectedId = radGioiTinh.getCheckedRadioButtonId();
            String gioiTinh = selectedId == R.id.radNam ? "Nam" : "Nữ";

            // Kiểm tra dữ liệu trước khi lưu
            if (tenTaiKhoan.isEmpty() || email.isEmpty() || matkhau.isEmpty() || soDienThoai.isEmpty() || diachi.isEmpty() || ngaysinh.isEmpty()) {
                Toast.makeText(DangKyActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            String sqlInsert = "INSERT INTO taikhoan (ten_tai_khoan, email, so_dien_thoai, mat_khau, gioi_tinh, ngay_sinh, dia_chi) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            SQLiteDatabase database = SQLiteConnect.getWritableDatabase();
            try {
                database.execSQL(sqlInsert, new Object[]{tenTaiKhoan, email, soDienThoai, matkhau, gioiTinh, ngaysinh, diachi});
                Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình đăng nhập
                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DangKyActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}