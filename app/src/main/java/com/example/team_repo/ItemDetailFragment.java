package com.example.team_repo;


import android.app.DatePickerDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItemDetailFragment extends Fragment {

    private Item mItem;
    private EditText DatePurchase;

    private Calendar calendar;

    private ArrayList<Tag> tagList;
    private ArrayList<Tag> selectedTags;
    private RecyclerView.LayoutManager layoutManager;
    private AddTagAdapter tagAdapter;



    private ImageView itemImageView;
    private PhotoUtility photoUtility;
    private OnItemUpdatedListener updateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ItemDetailFragment() {
        // Required empty public constructor



    }

    public static ItemDetailFragment newInstance(Item item) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item); // Ensure Item implements Serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItem = (Item) getArguments().getSerializable("item");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);
        Toolbar toolbar = view.findViewById(R.id.item_toolbar);
        TextView nameTextView = view.findViewById(R.id.itemNameTextView);
        TextView dateTextView = view.findViewById(R.id.itemDateTextView);
        TextView valueTextView = view.findViewById(R.id.itemValueTextView);
        TextView descriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView makeTextView = view.findViewById(R.id.itemMakeTextView);
        TextView modelTextView = view.findViewById(R.id.itemModelTextView);
        TextView serialNumberTextView = view.findViewById(R.id.itemSerialNumberTextView);
        TextView commentTextView = view.findViewById(R.id.itemCommentTextView);
        itemImageView = view.findViewById(R.id.itemImageView);
        photoUtility = new PhotoUtility(this);

        // Setup listeners for image update buttons
        view.findViewById(R.id.editImageButton).setOnClickListener(v -> photoUtility.choosePhoto());
        view.findViewById(R.id.takePhotoButton).setOnClickListener(v -> photoUtility.takePhoto());
        view.findViewById(R.id.deleteImageButton).setOnClickListener(v -> deleteImage());

        // Populate the views with item data
        nameTextView.setText(mItem.getName());
        dateTextView.setText(mItem.getPurchase_date());
        valueTextView.setText(String.valueOf(mItem.getValue()));
        descriptionTextView.setText(mItem.getDescription());
        makeTextView.setText(mItem.getMake());
        modelTextView.setText(mItem.getModel());
        serialNumberTextView.setText(mItem.getSerial_number());
        commentTextView.setText(mItem.getComment());

        // Set a placeholder image from the drawable resources
        itemImageView.setImageResource(R.drawable.ic_launcher_background);


        //Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        // Set the image if available, otherwise set a placeholder
        if (mItem.getImagePath() != null && !mItem.getImagePath().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mItem.getImagePath());
            itemImageView.setImageBitmap(bitmap);
        } else {
            // download image from firebase storage
            ImageUtils.downloadImageFromFirebaseStorage(mItem.getItemID().toString(), new ImageUtils.OnBitmapReadyListener() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    if (bitmap != null){
                        itemImageView.setImageBitmap(bitmap);
                    } else {
                        itemImageView.setImageResource(R.drawable.baseline_image_not_supported_24);
                    }
                }
            });

        }

        view.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editExpenseInputDialog(nameTextView, dateTextView, descriptionTextView, makeTextView, modelTextView, serialNumberTextView, valueTextView);
            }
        });

        view.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setAllNull();
                // delete from database
                ((MainActivity) getActivity()).deleteItemFromDB(mItem);
                // Inside a Fragment or Activity
                Toast.makeText(getContext(), "Item has been deleted, Return to the Home Page", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    private void deleteImage() {
        // Set the placeholder image and remove the current image path
        itemImageView.setImageResource(R.drawable.baseline_image_not_supported_24); // Placeholder drawable resource
        mItem.setImagePath(null); // Clear the image path
        ImageUtils.deleteImageFromFirebaseStorage(mItem.getItemID());  // Delete the image from Firebase Storage
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        if (requestCode == PhotoUtility.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Handle gallery image selection
                Uri selectedImageUri = data.getData();
                bitmap = photoUtility.handleImageOnActivityResult(selectedImageUri);

                // save the bitmap to firebase storage
                ImageUtils.uploadImageToFirebaseStorage(bitmap, mItem.getItemID());
            }
        } else if (requestCode == PhotoUtility.REQUEST_CODE_TAKE && resultCode == Activity.RESULT_OK) {
            // Handle camera image capture
            Uri capturedImageUri = photoUtility.getImageUri();
            bitmap = photoUtility.handleImageOnActivityResult(capturedImageUri);

            // save the bitmap to firebase storage
            ImageUtils.uploadImageToFirebaseStorage(bitmap, mItem.getItemID());
        }

        if (bitmap != null) {
            itemImageView.setImageBitmap(bitmap);

            // Create a unique filename based on the current timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timestamp + "_";

            // Save the bitmap to a file and get the path using ImageUtils
            String imagePath = ImageUtils.saveBitmapToFile(requireContext(), bitmap, imageFileName);

            if (imagePath != null) {
                // Store the image path in the item
                mItem.setImagePath(imagePath);
            }
        }
    }


    public interface OnItemUpdatedListener {
        void onItemUpdated(Item item);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            updateListener = (OnItemUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemUpdatedListener");
        }
    }


    // Call this method when the item is updated
    private void notifyItemUpdated(Item item) {
        if (updateListener != null) {
            updateListener.onItemUpdated(item);
        }
    }


    public void editExpenseInputDialog(TextView nameTextView, TextView dateTextView, TextView descriptionTextView, TextView makeTextView, TextView modelTextView, TextView serialNumberTextView, TextView valueTextView) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add, null);

        final EditText ItemName = dialogView.findViewById(R.id.ItemName);
        final EditText Description = dialogView.findViewById(R.id.Description);
        DatePurchase = dialogView.findViewById(R.id.DatePurchase);
        final EditText ItemMake = dialogView.findViewById(R.id.ItemMake);
        final EditText ItemModel = dialogView.findViewById(R.id.ItemModel);
        final EditText ItemSerial = dialogView.findViewById(R.id.ItemSerial);
        final EditText EstimatedValue = dialogView.findViewById(R.id.EstimatedValue);
        final RecyclerView tagRecyclerView = dialogView.findViewById(R.id.tagRecyclerView);

        // Populate the views with item data
        ItemName.setText(mItem.getName());
        Description.setText(mItem.getDescription());
        DatePurchase.setText(mItem.getPurchase_date());
        ItemMake.setText(mItem.getMake());
        ItemModel.setText(mItem.getModel());
        ItemSerial.setText(mItem.getSerial_number());
        EstimatedValue.setText(String.valueOf(mItem.getValue()));
        tagList = ((MainActivity) getActivity()).getTagList();
        // set all tags to unselected
        for (Tag tag : tagList) {
            tag.setSelected(false);
        }
        tagAdapter = new AddTagAdapter(getContext(), tagList);
        tagRecyclerView.setAdapter(tagAdapter);

        // set the layout manager of the recycler view
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(layoutManager);

        calendar = Calendar.getInstance();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(dialogView);

        // edit text get focus -> show date picker dialog
        DatePurchase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(DatePurchase);
                    // showDatePickerDialog(dateTextView);
                }
            }
        });

        // edit text get clicked -> show date picker dialog
        DatePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(DatePurchase);
                // showDatePickerDialog(dateTextView);

            }
        });

        dialogBuilder.setPositiveButton("Confirm", (dialog, which) -> {
            String name = ItemName.getText().toString();
            String date = DatePurchase.getText().toString();
            String item_description = Description.getText().toString();
            String make = ItemMake.getText().toString();
            String model = ItemModel.getText().toString();
            String serial = ItemSerial.getText().toString();
            String valueString = EstimatedValue.getText().toString();


            if (!name.isEmpty()) {
                mItem.setName(name);
                nameTextView.setText(mItem.getName());

                if (!item_description.isEmpty()) {
                    mItem.setDescription(item_description);
                    descriptionTextView.setText((mItem.getDescription()));
                }

                if (!make.isEmpty()) {
                    mItem.setMake(make);
                    makeTextView.setText((mItem.getMake()));
                }

                if (!model.isEmpty()) {
                    mItem.setModel(model);
                    modelTextView.setText((mItem.getModel()));
                }

                if (!serial.isEmpty()) {
                    mItem.setSerial_number(serial);
                    serialNumberTextView.setText(mItem.getSerial_number());
                }

                // Create an item with the received name and other default values or set appropriate values.
                if (!isValidDate(date)) {
                    // TODO: handle invalid date
                    mItem.setPurchase_date("0000-00-00");
                    dateTextView.setText(mItem.getPurchase_date());
                } else {
                    mItem.setPurchase_date(date);
                    dateTextView.setText(mItem.getPurchase_date());
                }

                float value;
                if (valueString.isEmpty()) {
                    value = (float) 0;
                } else {
                    value = Float.parseFloat(EstimatedValue.getText().toString());
                }
                valueTextView.setText(String.valueOf(value));
                mItem.setValue(value);

                // set the selected tags to the item
                selectedTags = new ArrayList<>();
                for (Tag tag : tagList) {
                    if (tag.isSelected()) {
                        selectedTags.add(tag);
                    }
                }
                mItem.setTags(selectedTags);
                ((MainActivity) getActivity()).updateItemToDB(mItem);


                ItemName.setText("");
                DatePurchase.setText("");
                Description.setText("");
                ItemMake.setText("");
                ItemModel.setText("");
                ItemSerial.setText("");
                EstimatedValue.setText("");

                // give a message to show that the item is added successfully
                Toast.makeText(getActivity(), "Item added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                // give a message to show that the item is not added successfully
                Toast.makeText(getActivity(), "Item should have a name", Toast.LENGTH_SHORT).show();
            }


        });
        AlertDialog dialog = dialogBuilder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.fragments_rounded_corner, null));
        }
        dialog.show();
    }




    /**
     * Update the date label using the selected date from the date picker dialog
     */
    private void updateLabel(TextView DatePurchase) {
        String myFormat = "yyyy-MM-dd"; // date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        DatePurchase.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Show a date picker dialog
     */
    private void showDatePickerDialog(TextView DatePurchase){

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // month starts from 0
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(DatePurchase); // update the date label
            }
        };

        // create and show a date picker dialog
        new DatePickerDialog(getActivity(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Check if the date string is valid
     * @param dateStr the date string to be checked
     * @return true: valid; false: invalid
     */
    public boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false); // set to false to check date strictly
        try {
            // try to parse the string to date
            Date date = sdf.parse(dateStr);
            return true; // if success, the date is valid
        } catch (ParseException e) {
            // throw exception if the date string is invalid
            return false;
        }
    }

        // Other methods can be added here if necessary

}

