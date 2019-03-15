package mvpstructure.com.mvpstructure;

import android.content.ClipboardManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 原本是打算写一个mvp架构的，但是突然有这个需求 就用这个mvp的包名写了这个demo 也懒得改了
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycle)
    RecyclerView mRecycle;
    private ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        List<PackageInfo> allApps = PackageInfoManager.getAllApps(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecycle.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        mRecycle.setItemAnimator(new DefaultItemAnimator());

        mRecycle.setAdapter(new NormalAdapter(MainActivity.this, myClipboard, allApps));


    }


}

