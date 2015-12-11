package com.salvasavall.p2_popmov;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class PlayListener implements View.OnClickListener {
    String key;

    public PlayListener(String key) {
        this.key = key;
    }

    @Override
    public void onClick(View v) {
        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
    }
}
