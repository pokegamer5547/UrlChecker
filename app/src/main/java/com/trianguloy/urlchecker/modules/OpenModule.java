package com.trianguloy.urlchecker.modules;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.trianguloy.urlchecker.R;
import com.trianguloy.urlchecker.utilities.GenericPref;
import com.trianguloy.urlchecker.utilities.PackageUtilities;
import com.trianguloy.urlchecker.utilities.UrlUtilities;

import java.util.List;

public class OpenModule extends BaseModule implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private GenericPref<String> latest;

    private List<String> packages;
    private Button btn_open;
    private ImageButton btn_openWith;
    private Menu menu;
    private PopupMenu popup;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getLayoutBase() {
        return R.layout.module_open;
    }

    @Override
    public void initialize(View views) {
        btn_open = views.findViewById(R.id.open);
        btn_open.setOnClickListener(this);
        btn_openWith = views.findViewById(R.id.open_with);
        btn_openWith.setOnClickListener(this);
        views.findViewById(R.id.share).setOnClickListener(this);


        popup = new PopupMenu(cntx, btn_open);
        popup.setOnMenuItemClickListener(this);
        menu = popup.getMenu();

        latest = new GenericPref.Str(cntx, "open", "latest", null);
    }

    @Override
    public void onNewUrl(String url) {
        updateSpinner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                openUrl();
                break;
            case R.id.share:
                shareUrl();
                break;
            case R.id.open_with:
                showList();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final String chosed = packages.get(item.getItemId());
        latest.set(chosed);
        cntx.startActivity(UrlUtilities.getViewIntent(cntx.getUrl(), chosed));
        return false;
    }

    private void updateSpinner() {
        packages = PackageUtilities.getOtherPackages(UrlUtilities.getViewIntent(cntx.getUrl(), null), cntx);

        if (packages.isEmpty()) {
            btn_open.setText("");
            btn_open.setEnabled(false);
            return;
        }

        String main = packages.remove(0);
        if(packages.contains(latest.get())){
            main = latest.get();
            packages.remove(latest.get());
        }else{
            main = packages.remove(0);
        }

        btn_open.setText("Open with " + PackageUtilities.getPackageName(main, cntx));
        btn_open.setEnabled(true);
        menu.clear();
        if (packages.isEmpty()) {
            btn_openWith.setVisibility(View.GONE);
        } else {
            btn_openWith.setVisibility(View.VISIBLE);
            for (int i = 0; i < packages.size(); i++) {
                menu.add(Menu.NONE, i, i, "Open with " + PackageUtilities.getPackageName(packages.get(i), cntx));
            }
        }

    }

    private void openUrl() {
        UrlUtilities.openUrlRemoveThis(cntx.getUrl(), cntx);
    }

    private void showList() {
        popup.show();
    }

    private void shareUrl() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, cntx.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share");
        cntx.startActivity(shareIntent);
    }
}
