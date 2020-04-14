package com.womensafety.shajt3ch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.harjot.rotatingtext.RotatingTextSwitcher;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;
import com.tomer.fadingtextview.FadingTextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainView extends Fragment implements BaseSliderView.OnSliderClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CardView instructionsCard,   communityScreenCard;
    private ImageView schedulePanicButtonCard, nearbyPlacesCard,displayContactsCard,registerContactsCard;
    private SliderLayout instructionsSlider;
    private TextView welcomeText;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SCREEN selectedScreen;
    String[] texts = {};
    FadingTextView FTV;

    public MainView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainView.
     */
    // TODO: Rename and change types and number of parameters
    public static MainView newInstance(String param1, String param2) {
        MainView fragment = new MainView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance()  ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main_view, container, false);

        instructionsCard = view.findViewById(R.id.instructions_card);
        registerContactsCard = view.findViewById(R.id.register_contacts_card);
        schedulePanicButtonCard = view.findViewById(R.id.schedule_panic_button_card);
        displayContactsCard = view.findViewById(R.id.display_contacts_card);
        nearbyPlacesCard = view.findViewById(R.id.nearby_card);
        communityScreenCard = view.findViewById(R.id.community_screen_card);
        welcomeText = view.findViewById(R.id.welcome_text);
        FTV = view.findViewById(R.id.fadingTextView);


        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        instructionsSlider = view.findViewById(R.id.slider);
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Panic Button",R.drawable.cardpanic);
        file_maps.put("Nearby Places", R.drawable.cardnearby);
        file_maps.put("Set Primary Contacts", R.drawable.primarycontact);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                welcomeText.setText(String.format("Hey!! %s", user.getUsername()));
                texts = new String[]{  "Welcome to SafeU!","Stay Safe", "Be Alert", "Take Care"};
                FTV.setTexts(texts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for(String name : file_maps.keySet()){
            TextSliderView  textSliderView = new TextSliderView(getContext());
            textSliderView.description(name).image(file_maps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(this);
            instructionsSlider.addSlider(textSliderView);
        }


        instructionsSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        instructionsSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        instructionsSlider.setCustomAnimation(new DescriptionAnimation());
        instructionsSlider.setDuration(4000);

        instructionsCard.setOnClickListener(
                (v -> {
                    selectedScreen = SCREEN.INSTRUCTIONS;
                    displaySelectedFragment(selectedScreen);
                }
                ));

        registerContactsCard.setOnClickListener((v ->
        {
            selectedScreen = SCREEN.REGISTER_CONTACTS;
            displaySelectedFragment(selectedScreen);
        }));
        displayContactsCard.setOnClickListener((v -> {
            selectedScreen = SCREEN.DISPLAY_CONTACTS;
            displaySelectedFragment(selectedScreen);
        }));
        nearbyPlacesCard.setOnClickListener((v -> {
            selectedScreen = SCREEN.NEARBY;
            displaySelectedFragment(selectedScreen);
        }));
        schedulePanicButtonCard.setOnClickListener((v -> {
            selectedScreen = SCREEN.SCHEDULE_PANIC;
            displaySelectedFragment(selectedScreen);
        }));
        communityScreenCard.setOnClickListener((v -> {
            selectedScreen = SCREEN.COMMUNITY;
            displaySelectedFragment(selectedScreen);
        }));

        return view;

    }

    private void displaySelectedFragment(SCREEN selectedScreen) {
        Fragment fragment = null;
        switch (selectedScreen) {
            case MAIN:
                fragment = new MainView();
                break;
            case REGISTER_CONTACTS:
                fragment = new Register();
                break;
            case DISPLAY_CONTACTS:
                fragment = new Display();
                break;
            case SCHEDULE_PANIC:
                fragment = new PanicTimePicker();
                break;
            case NEARBY:
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
                break;
            case COMMUNITY:
                break;
            case INSTRUCTIONS:
                showInstructionsDialog();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

    }

    private void showInstructionsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_layout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(
                getActivity()).create();

        // Setting Dialog Title
        alertDialog.setTitle("Instructions");
        // Setting Dialog Message
        alertDialog.setView(alertLayout);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.instruct_icon);


        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}

enum SCREEN {
    MAIN,
    REGISTER_CONTACTS,
    DISPLAY_CONTACTS,
    SCHEDULE_PANIC,
    NEARBY,
    COMMUNITY,
    INSTRUCTIONS,

}
