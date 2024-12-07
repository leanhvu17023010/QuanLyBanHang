package com.example.Customer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Customer.AdapterCustomer.CustomerAdapter;
import com.example.demo.Activity.DashBoard;
import com.example.demo.R;
import com.example.Customer.database.CustomerDatabase;
import com.example.Customer.model.Customer;

import java.util.List;

public class DanhSachKhachHang extends AppCompatActivity {
    ListView lvCustomerList;
    ImageView imgAdd, imgback;
    CustomerAdapter customerAdapter;
    List<Customer> customerList;
    CustomerDatabase databaseHelper;
    private ActivityResultLauncher<Intent> addCustomerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_khach_hang);

        lvCustomerList = findViewById(R.id.lvCustomerList);
        imgAdd = findViewById(R.id.imgAdd);
        imgback = findViewById(R.id.imgback);

        // Khởi tạo cơ sở dữ liệu và adapter
        databaseHelper = new CustomerDatabase(this);
        loadCustomers();

        // Khởi tạo ActivityResultLauncher để nhận kết quả
        addCustomerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d("DanhSachKhachHang", "Thêm khách hàng thành công.");
                        loadCustomers();  // Tải lại danh sách khách hàng từ cơ sở dữ liệu
                    }
                }
        );
        // Xử lý sự kiện thêm khách hàng
        imgAdd.setOnClickListener(view -> {
            Intent intent = new Intent(DanhSachKhachHang.this, AddCustomer.class);
            addCustomerLauncher.launch(intent);  // Sử dụng ActivityResultLauncher
        });

        imgback.setOnClickListener(view -> {
            Intent intent = new Intent(DanhSachKhachHang.this, DashBoard.class);
            startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn vào một khách hàng
        lvCustomerList.setOnItemClickListener((adapterView, view, position, id) -> {
            Customer selectedCustomer = customerList.get(position);
            Intent intent = new Intent(DanhSachKhachHang.this, ThongTinChiTiet.class);
            intent.putExtra("customer_id", selectedCustomer.getId());  // Truyền ID của khách hàng
            startActivity(intent);
        });
    }

    private void loadCustomers() {
        if (customerList == null) {
            customerList = databaseHelper.getAllCustomers();
        } else {
            customerList.clear();  // Xóa danh sách cũ
            customerList.addAll(databaseHelper.getAllCustomers());  // Thêm danh sách mới
        }
        Log.d("DanhSachKhachHang", "customerList size sau khi load: " + customerList.size());

        if (customerAdapter == null) {
            customerAdapter = new CustomerAdapter(this, customerList);
            lvCustomerList.setAdapter(customerAdapter);
        } else {
            customerAdapter.notifyDataSetChanged();  // Cập nhật giao diện adapter
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DanhSachKhachHang", "onResume được gọi");
        loadCustomers();
    }
}
