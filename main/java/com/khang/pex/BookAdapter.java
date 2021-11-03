package com.khang.pex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends BaseAdapter {
    private List<BookDto> listBooks;

    public BookAdapter() {
    }

    public void setListBooks(List<BookDto> listBooks) {
        this.listBooks = listBooks;
    }

    @Override
    public int getCount() {
        return listBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return listBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.book_item, null);
        }
        TextView id = convertView.findViewById(R.id.txtId);
        TextView title = convertView.findViewById(R.id.txtTitle);
        TextView isbn = convertView.findViewById(R.id.txtIsbn);

        id.setText("ID: " + String.valueOf(listBooks.get(position).getId()));
        title.setText("Title: " + listBooks.get(position).getTitle());
        isbn.setText("Isbn: " + listBooks.get(position).getIsbn());
        return convertView;
    }
}
