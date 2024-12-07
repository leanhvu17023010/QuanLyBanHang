package com.example.Customer.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.demo.R;
import com.example.Customer.database.CustomerDatabase;
import com.example.Customer.model.Customer;

import java.io.ByteArrayOutputStream;

public class ThongTinChiTiet extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private TextView tvName, tvPhone, tvAddress, tvEmail, tvBirthDate, tvNotes;
    private ImageView ivProfileImage;
    private Button btnTakePhoto, btnReturn;
    private int customerId;
    private CustomerDatabase databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_chi_tiet);

        // Khởi tạo các TextView và Button
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvEmail = findViewById(R.id.tvEmail);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvNotes = findViewById(R.id.tvNotes);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnReturn = findViewById(R.id.btnReturn);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
        // Khởi tạo cameraLauncher để xử lý kết quả từ Camera Intent
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            ivProfileImage.setImageBitmap(imageBitmap); // Hiển thị ảnh trên ImageView
                        }
                    }
                }
        );


        // Lấy customer_id từ Intent
        Intent intent = getIntent();
        customerId = intent.getIntExtra("customer_id", -1);

        if (customerId == -1) {
            Toast.makeText(this, "Customer ID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo database helper
        databaseHelper = new CustomerDatabase(this);

        // Lấy thông tin khách hàng và hiển thị
        Customer customer = databaseHelper.getCustomerById(customerId);
        if (customer != null) {
            tvName.setText(customer.getName());
            tvPhone.setText(customer.getPhone());
            tvAddress.setText(customer.getAddress());
            tvEmail.setText(customer.getEmail());
            tvBirthDate.setText(customer.getBirthDate());
            tvNotes.setText(customer.getNotes());

            // Kiểm tra nếu khách hàng có ảnh
            if (customer.getPhoto() != null) {
                // Giải mã byte[] thành Bitmap và hiển thị trên ImageView
                Bitmap bitmap = BitmapFactory.decodeByteArray(customer.getPhoto(), 0, customer.getPhoto().length);
                ivProfileImage.setImageBitmap(bitmap);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show();
        }


        // Xử lý sự kiện khi nhấn nút Chụp ảnh
        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Yêu cầu quyền camera nếu chưa cấp
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        btnReturn.setOnClickListener(v -> {
            Intent intent1 = new Intent(ThongTinChiTiet.this, DanhSachKhachHang.class);
            startActivity(intent1);
        });
    }

    // Mở camera
    private void openCamera() {
        // Tạo Intent mở camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Sử dụng launcher để mở camera
            cameraLauncher.launch(takePictureIntent);
        }
    }

    // Xử lý kết quả trả về từ camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Xử lý kết quả từ Camera Intent
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Nhận ảnh từ Intent
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Hiển thị ảnh trên ImageView
            ivProfileImage.setImageBitmap(imageBitmap);
        }
    }

    // Xử lý kết quả yêu cầu quyền
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền camera đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
