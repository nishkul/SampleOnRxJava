package com.test.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.test.android.dto.Pojo;
import com.test.android.interfaces.OnOperationResult;
import com.test.android.rjx.RxEventBus;

import java.io.IOException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Manish on 20/2/17.
 */

public class MyImagePicker {

    private Context context = null;

    //Default selection mode for dialog
    private String selectionMode = MyConstants.CAMERA_AND_GALLERY;

    private Subscription cameraSubscription = null;
    private Subscription gallerySubscription = null;
    private ImageView imageView = null;
    private OnOperationResult onOperationResult = null;

    private String choosenOption = MyConstants.CAMERA; //Default Capture using camera

    public MyImagePicker(Context context) {
        this.context = context;
    }

    public String getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(String selectionMode) {
        this.selectionMode = selectionMode;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    // To return result to subscribers place
    public OnOperationResult getOnOperationResult() {
        return onOperationResult;
    }

    public void setOnOperationResult(OnOperationResult onOperationResult) {
        this.onOperationResult = onOperationResult;
    }

    public void chooseImage( ) {
        switch (selectionMode) {

            case MyConstants.CAMERA_AND_GALLERY:
                Log.v("wwwwwww",selectionMode+" ");
                final CharSequence[] options = {MyConstants.GALLERY, MyConstants.CAMERA, MyConstants.CANCEL};
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(MyConstants.DIALOG_TITLE);
                dialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(MyConstants.GALLERY)) {
                            choosenOption = MyConstants.GALLERY;
                            openGallery();
                        } else if (options[which].equals(MyConstants.CAMERA)) {
                            choosenOption = MyConstants.CAMERA;
                            openCamera();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;

            case MyConstants.CAMERA:
                choosenOption = MyConstants.CAMERA;
                openCamera();
                break;

            case MyConstants.GALLERY:
                choosenOption = MyConstants.GALLERY;
                openGallery();
                break;

            default:
                selectionMode = MyConstants.CAMERA_AND_GALLERY;
                chooseImage();
        }
    }

    private void openCamera() {
        subscribeForCamera();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((AppCompatActivity) context).startActivityForResult(intent, MyConstants.CAMERA_REQ_CODE);
    }

    private void subscribeForCamera() {
//        here we get subscription object so we can unsubscribe anytime
        cameraSubscription = RxEventBus.getEventBus().getObservables()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o != null) processImage(o);
                    }
                });
    }

    private void openGallery() {
        subscribeForGallery();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((AppCompatActivity) context).startActivityForResult(Intent.createChooser(intent, "Select file")
                , MyConstants.GALLERY_REQ_CODE);
    }

    private void subscribeForGallery() {
        gallerySubscription = RxEventBus.getEventBus().getObservables()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        processImage(o);
                    }
                });
    }

    private void processImage(Object o) {
        if (o instanceof Pojo) {
            Pojo pojo = (Pojo) o;
            if (pojo.getResultCode() == AppCompatActivity.RESULT_OK) {
                if (choosenOption.equals(MyConstants.CAMERA)) {
                    processCameraResult(pojo);
                } else if (choosenOption.equals(MyConstants.GALLERY)) {
                    processGalleryResult(pojo);
                }
            }
        }
        if (choosenOption.equalsIgnoreCase(MyConstants.CAMERA)) {
            unSubscribeCamera();
        } else {
            unSubscribeGallery();
        }
    }

    private void processCameraResult(Pojo pojo) {
        Bitmap bitmap = (Bitmap) pojo.getIntent().getExtras().get("data");
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
        if (onOperationResult != null) {
            onOperationResult.onOperationResult(pojo);
        }
    }

    private void processGalleryResult(Pojo pojo) {

        Bitmap bitmap = null;
        if (pojo.getIntent() != null) {
            try {
                bitmap = MediaStore.Images.Media.
                        getBitmap(context.getApplicationContext().getContentResolver(), pojo.getIntent().getData());
                bitmap = cropAndScale(bitmap, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            if (onOperationResult != null) {
                onOperationResult.onOperationResult(pojo);
            }
        }
    }

    private void unSubscribeCamera() {
        if (cameraSubscription != null && !cameraSubscription.isUnsubscribed()) {
            cameraSubscription.unsubscribe();
        }
    }

    private void unSubscribeGallery() {
        if (gallerySubscription != null && !gallerySubscription.isUnsubscribed()) {
            gallerySubscription.unsubscribe();
        }
    }

    public Bitmap cropAndScale(Bitmap source, int scale) {
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight() : source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        int x = source.getHeight() >= source.getWidth() ? 0 : (longer - factor) / 2;
        int y = source.getHeight() <= source.getWidth() ? 0 : (longer - factor) / 2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }
}