package com.example.Customer.AdapterCustomer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.R;
import com.example.Customer.database.CustomerDatabase;
import com.example.Customer.model.Customer;

import java.util.List;

public class CustomerAdapter extends BaseAdapter {
    private Context context;
    private List<Customer> customerList;
    private CustomerDatabase databaseHelper;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
        this.databaseHelper = new CustomerDatabase(context); // Khởi tạo cơ sở dữ liệu
    }

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return customerList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_customer, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.imgDeleteCustomer = convertView.findViewById(R.id.imgDeleteCustomer);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lấy thông tin khách hàng từ danh sách
        Customer customer = customerList.get(position);

        if (customer != null) {
            // Hiển thị tên khách hàng
            viewHolder.tvName.setText(customer.getName());

            // Sự kiện xóa khách hàng
            viewHolder.imgDeleteCustomer.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa khách hàng này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            boolean isDeleted = databaseHelper.deleteCustomer(customer.getId());
                            if (isDeleted) {
                                // Tải lại danh sách từ cơ sở dữ liệu
                                customerList.clear();
                                customerList.addAll(databaseHelper.getAllCustomers());
                                notifyDataSetChanged();  // Cập nhật giao diện
                                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }

        return convertView;
    }


    static class ViewHolder {
        TextView tvName;
        ImageView imgDeleteCustomer; // Thêm thùng rác
    }

    @Override
    protected void finalize() throws Throwable {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.finalize();
    }

}
