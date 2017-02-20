package com.test.android;

/**
 * Created by Manish on 20/2/17.
 */

        import android.app.Dialog;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.DialogFragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.test.android.dto.Pojo;
        import com.test.android.interfaces.OnOperationResult;


public class MyDialogFragment extends DialogFragment {

    private View view;
    private TextView title;
    private ImageView image_view;
    private Button get_image, open_another_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_selection, container, false);

        title = (TextView) view.findViewById(R.id.title);
        image_view = (ImageView) view.findViewById(R.id.image_view);
        get_image = (Button) view.findViewById(R.id.get_image);
        open_another_view = (Button) view.findViewById(R.id.open_another_view);

        title.setText("Pick image inside dialog fragment using Rx-java/android");
        open_another_view.setText("Open Dialog");

        get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyImagePicker myImagePicker = new MyImagePicker(getActivity());
                myImagePicker.setImageView(image_view);
                myImagePicker.setSelectionMode(MyConstants.CAMERA_AND_GALLERY);
                myImagePicker.setOnOperationResult(new OnOperationResult() {
                    @Override
                    public void onOperationResult(Pojo pojo) {
                        Toast.makeText(getActivity(), "Image Result received in dialog fragment", Toast.LENGTH_SHORT).show();
                    }
                });
                myImagePicker.chooseImage();
            }
        });

        open_another_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog.setContentView(R.layout.image_selection);
                dialog.setTitle("Pick image inside dialog using Rx-java/android");

                Button get_image = (Button)dialog.findViewById(R.id.get_image);
                final ImageView image_view = (ImageView) dialog.findViewById(R.id.image_view);
                Button open_another_view = (Button) dialog.findViewById(R.id.open_another_view);
                open_another_view.setVisibility(View.GONE);
                get_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyImagePicker myImagePicker = new MyImagePicker(getActivity());
                        myImagePicker.setImageView(image_view);
                        myImagePicker.setSelectionMode(MyConstants.CAMERA_AND_GALLERY);
                        myImagePicker.setOnOperationResult(new OnOperationResult() {
                            @Override
                            public void onOperationResult(Pojo pojo) {
                                Toast.makeText(getActivity(), "Image Result received in dialog", Toast.LENGTH_SHORT).show();
                            }
                        });
                        myImagePicker.chooseImage();
                    }
                });
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // For full screen dialog
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}