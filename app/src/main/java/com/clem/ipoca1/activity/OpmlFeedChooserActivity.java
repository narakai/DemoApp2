package com.clem.ipoca1.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.color.CircleView;
import com.clem.ipoca1.R;
import com.clem.ipoca1.core.export.opml.OpmlElement;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.core.view.CornerView.CornerButton;
import com.clem.ipoca1.spa.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the feeds that the OPML-Importer has read and lets the user choose
 * which feeds he wants to import.
 */
public class OpmlFeedChooserActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_ITEMS = "com.clem.ipoca1.selectedItems";
    private static final String TAG = "OpmlFeedChooserActivity";
    private CornerButton butConfirm;
    private CornerButton butCancel;
    private ListView feedlist;
    private ArrayAdapter<String> listAdapter;

    private MenuItem selectAll;
    private MenuItem deselectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.opml_selection);
        butConfirm = (CornerButton) findViewById(R.id.butConfirm);
        butConfirm.setBackgroundColor(UserPreferences.getPrefColor());
        butConfirm.setTextColor(ColorUtil.getThemeColor(this.getApplicationContext()));
        butCancel = (CornerButton) findViewById(R.id.butCancel);
        butCancel.setBackgroundColor(UserPreferences.getPrefColor());
        butCancel.setTextColor(ColorUtil.getThemeColor(this.getApplicationContext()));
        feedlist = (ListView) findViewById(R.id.feedlist);

        feedlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice,
                getTitleList());

        feedlist.setAdapter(listAdapter);
        feedlist.setOnItemClickListener((parent, view, position, id) -> {
            SparseBooleanArray checked = feedlist.getCheckedItemPositions();
            int checkedCount = 0;
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedCount++;
                }
            }
            if(checkedCount == listAdapter.getCount()) {
                selectAll.setVisible(false);
                deselectAll.setVisible(true);
            } else {
                deselectAll.setVisible(false);
                selectAll.setVisible(true);
            }
        });

        butCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        butConfirm.setOnClickListener(v -> {
            Intent intent = new Intent();
            SparseBooleanArray checked = feedlist.getCheckedItemPositions();

            int checkedCount = 0;
            // Get number of checked items
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedCount++;
                }
            }
            int[] selection = new int[checkedCount];
            for (int i = 0, collected = 0; collected < checkedCount; i++) {
                if (checked.valueAt(i)) {
                    selection[collected] = checked.keyAt(i);
                    collected++;
                }
            }
            intent.putExtra(EXTRA_SELECTED_ITEMS, selection);
            setResult(RESULT_OK, intent);
            finish();
        });

        int primaryPreselect = UserPreferences.getPrefColor();
        ColorDrawable drawable = new ColorDrawable(primaryPreselect);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(primaryPreselect));
            getWindow().setNavigationBarColor(primaryPreselect);
        }

    }

    private List<String> getTitleList() {
        List<String> result = new ArrayList<String>();
        if (OpmlImportHolder.getReadElements() != null) {
            for (OpmlElement element : OpmlImportHolder.getReadElements()) {
                result.add(element.getText());
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opml_selection_options, menu);
        selectAll = menu.findItem(R.id.select_all_item);
        deselectAll = menu.findItem(R.id.deselect_all_item);
        deselectAll.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all_item:
                selectAll.setVisible(false);
                selectAllItems(true);
                deselectAll.setVisible(true);
                return true;
            case R.id.deselect_all_item:
                deselectAll.setVisible(false);
                selectAllItems(false);
                selectAll.setVisible(true);
                return true;
            default:
                return false;
        }
    }

    private void selectAllItems(boolean b) {
        for (int i = 0; i < feedlist.getCount(); i++) {
            feedlist.setItemChecked(i, b);
        }
    }

}
