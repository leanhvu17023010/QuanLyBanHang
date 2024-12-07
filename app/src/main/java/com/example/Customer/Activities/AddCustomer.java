package com.example.Customer.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Customer.model.Customer;
import com.example.demo.R;
import com.example.Customer.database.CustomerDatabase;

import java.io.ByteArrayOutputStream;

public class AddCustomer extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Để chọn ảnh từ bộ sưu tập
    private EditText edtName, edtPhone, edtAddress, edtEmail, edtBirthDate, edtNotes;
    private Button btnSave;
    ImageView imgBack, imgCustomerPhotoAdd;
    private CustomerDatabase databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        // Ánh xạ các trường EditText
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtNotes = findViewById(R.id.edtNotes);
        imgCustomerPhotoAdd = findViewById(R.id.imgCustomerPhotoAdd);

        btnSave = findViewById(R.id.btnSave);
        imgBack = findViewById(R.id.imgback1);

        // Khởi tạo đối tượng cơ sở dữ liệu
        databaseHelper = new CustomerDatabase(this);

        btnSave.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường
            String newName = edtName.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newAddress = edtAddress.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();
            String newBirthDate = edtBirthDate.getText().toString().trim();
            String newNotes = edtNotes.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPhone) || TextUtils.isEmpty(newAddress) ||
                    TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newBirthDate)) {
                Toast.makeText(AddCustomer.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                Log.d("AddCustomer", "Thông tin không đầy đủ. Vui lòng kiểm tra.");
                return;
            }

            // Chuyển ảnh thành byte[] (nếu có)
            byte[] photo = getImageBytes(imgCustomerPhotoAdd);

            // Tạo đối tượng Customer
            Customer customer = new Customer();
            customer.setName(newName);
            customer.setPhone(newPhone);
            customer.setAddress(newAddress);
            customer.setEmail(newEmail);
            customer.setBirthDate(newBirthDate);
            customer.setNotes(newNotes);
            customer.setPhoto(photo);

            // Thêm dữ liệu vào cơ sở dữ liệu
            boolean result = databaseHelper.addCustomer(customer);

            if (result) {
                setResult(RESULT_OK);  // Thông báo thêm thành công
                finish();  // Đóng Activity sau khi thêm thành công
            } else {
                setResult(RESULT_CANCELED);  // Nếu có lỗi
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút quay lại
        imgBack.setOnClickListener(v -> onBackPressed());

        // Xử lý sự kiện khi nhấn vào ảnh khách hàng để chọn ảnh từ bộ sưu tập
        imgCustomerPhotoAdd.setOnClickListener(v -> openGallery());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Quay lại Activity trước đó
    }

    // Phương thức mở gallery để chọn ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả trả về từ gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgCustomerPhotoAdd.setImageURI(data.getData()); // Đặt ảnh chọn được vào ImageView
        }
    }

    // Phương thức chuyển đổi ảnh trong ImageView thành byte[]
    private byte[] getImageBytes(ImageView imageView) {
        // Kiểm tra nếu ImageView có chứa ảnh
        if (imageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // Chuyển đổi bitmap thành PNG
            return stream.toByteArray();
        }
        return null; // Nếu không có ảnh, trả về null
    }
}
