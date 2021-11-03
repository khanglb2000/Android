package com.khang.pex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtId, edtTitle, edtIsbn;
    private Button btnAdd, btnUpdate, btnDelete;
    private ListView listBooks;
    BookAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtId = findViewById(R.id.edtId);
        edtTitle = findViewById(R.id.edtTitle);
        edtIsbn = findViewById(R.id.edtIsbn);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        listBooks = findViewById(R.id.listViewBooks);

        adapter = new BookAdapter();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        loadList();
    }

    private void loadList() {
        Cursor cr = getContentResolver().query(BookProvider.CONTENT_URI, null, null, null, null);

        List<BookDto> bookList = new ArrayList<>();

        while (cr.moveToNext()) {
            int id = cr.getInt(0);
            String title = cr.getString(1);
            String isbn = cr.getString(2);

            bookList.add(new BookDto(id, isbn, title));
        }

        adapter.setListBooks(bookList);

        listBooks.setAdapter(adapter);

        if (bookList.size() > 0) {
            listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BookDto dto = (BookDto) listBooks.getItemAtPosition(i);
                    edtId.setText(dto.getId() + "");
                    edtTitle.setText(dto.getTitle());
                    edtIsbn.setText(dto.getIsbn() + "");
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                    btnAdd.setEnabled(false);
                }
            });
        } else {
            Toast.makeText(this, "No books found", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickToAdd(View view) {
        if (isValid()) {
            ContentValues values = new ContentValues();
            values.put(BookDatabase.COLUMN_TITLE, edtTitle.getText().toString());
            values.put(BookDatabase.COLUMN_ISBN, edtIsbn.getText().toString());

            Uri uri = getContentResolver().insert(BookProvider.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                refreshScreen();
            } else {
                Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clickToUpdate(View view) {
        if (isValid()) {
            ContentValues values = new ContentValues();
            values.put(BookDatabase.COLUMN_TITLE, edtTitle.getText().toString());
            values.put(BookDatabase.COLUMN_ISBN, edtIsbn.getText().toString());

            int result = getContentResolver().update(BookProvider.CONTENT_URI, values,
                    BookDatabase.COLUMN_ID + " = ?",
                    new String[]{edtId.getText().toString()});
            if (result > 0) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                refreshScreen();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clickToDelete(View view) {
        if (edtId.getText() != null) {
            int result = getContentResolver().delete(BookProvider.CONTENT_URI, BookDatabase.COLUMN_ID + " = ? ", new String[]{edtId.getText().toString()});
            if (result > 0) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                refreshScreen();
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearTextFields() {
        edtId.setText("");
        edtTitle.setText("");
        edtIsbn.setText("");
        edtTitle.setError(null);
        edtIsbn.setError(null);
    }

    private void refreshScreen() {
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(true);
        clearTextFields();
        loadList();
    }

    public void clickToReload(View view) {
        refreshScreen();
    }

    private boolean isValid() {
        String title = edtTitle.getText().toString();
        String isbn = edtIsbn.getText().toString();
        boolean isValidTitle = true;
        boolean isValidIsbn = true;
        if (title == null || title.trim().isEmpty()) {
            edtTitle.setError("Title cannot be blank");
            isValidTitle = false;
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            edtIsbn.setError("Isbn cannot be blank");
            isValidIsbn = false;
        }
        return isValidTitle && isValidIsbn;
    }
}