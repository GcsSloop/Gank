package com.sloop.gank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sloop.gank.constant.Constants;
import com.sloop.gank.log.L;
import com.sloop.gank.view.PinchImageView;
import com.sloop.utils.ToastUtils;
import com.sloop.utils.img.ImageUtils;

public class ImageActivity extends AppCompatActivity {

    private PinchImageView mImageView;
    private Button mSave;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setTitle("妹子");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setIcon(R.drawable.actionbar_back);

        mImageView = (PinchImageView) findViewById(R.id.img_image);
        mSave = (Button) findViewById(R.id.img_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(Constants.key_imgurl);
        ImageLoader.getInstance().displayImage(imgUrl, mImageView, options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.img_save) {
            saveImage();
        } else if (id == R.id.img_browser) {
            Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
            startActivity(ie);
        }

        return true;
    }

    private void saveImage() {
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Gank";
        String fullName = imgUrl.substring(imgUrl.lastIndexOf('/')+1);
        L.e(fullName);
        String[] name = fullName.split("\\.");
        L.e(name[0]+"---"+name[1]);/**/

        if (null==bitmap){
            ToastUtils.show(this,"无法保存图片!");
        }

        boolean saved = ImageUtils.saveImageToGallery(this,bitmap,dir,name[0]);

        if (saved){
            ToastUtils.show(this,"保存成功!");
        }else {
            ToastUtils.show(this,"保存失败!");
        }
    }
}
