package me.yiblet.yiblet.newsifyly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yiblet on 6/24/16.
 */
public class CategoryDialogFragment extends DialogFragment {

    @BindView(R.id.cbSports) AppCompatCheckBox cbSports;
    @BindView(R.id.cbArts) AppCompatCheckBox cbArts;
    @BindView(R.id.cbTechnology) AppCompatCheckBox cbTechnology;
    @BindView(R.id.cbBusiness) AppCompatCheckBox cbBusiness;
    @BindView(R.id.cbCulture) AppCompatCheckBox cbCulture;
    @BindView(R.id.cbPolitics) AppCompatCheckBox cbPolitics;
    @BindView(R.id.btSubmit) Button btSubmit;
    @BindView(R.id.btQuit) Button btQuit;
    PassData passer;
    String categories;

    public PassData getPasser() {
        return passer;
    }

    public void setPasser(PassData passer) {
        this.passer = passer;
    }

    public CategoryDialogFragment() {

    }

    public static CategoryDialogFragment newInstance(String categories) {
        CategoryDialogFragment frag = new CategoryDialogFragment();
//        args.putString("title", title);
        
        Bundle args = new Bundle();
        if (categories == null) {
            args.putString("categories", "");
        } else {
            args.putString("categories", categories);
        }
//        args.put
//        args.put
        frag.setArguments(args);
        return frag;


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.category_dialog, container);
        ButterKnife.bind(this, view);
        chooseChecks(getArguments().getString("categories"));
        return view;
    }
    
    private void chooseChecks(String categories) {
        if (categories.contains("Technology")) {
            cbTechnology.setChecked(true);
        } if (categories.contains("Politics")) {
            cbPolitics.setChecked(true);
        } if (categories.contains("Sports")) {
            cbSports.setChecked(true);
        } if (categories.contains("Arts")) {
            cbArts.setChecked(true);
        } if (categories.contains("Culture")) {
            cbCulture.setChecked(true);
        } if (categories.contains("Business")) {
            cbBusiness.setChecked(true);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passer != null) {
                    passer.passData(getCategories());
                }
                CategoryDialogFragment.this.getDialog().dismiss();
            }
        });

        btQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passer != null) {
                    passer.noData();
                }
                CategoryDialogFragment.this.getDialog().dismiss();
            }
        });
    }

    protected String getCategories() {
        String result = "";
        if (cbBusiness.isChecked()) {
            result += "\"Business\" ";
        } if (cbArts.isChecked()) {
            result += "\"Arts\" ";
        } if (cbTechnology.isChecked()) {
            result += "\"Technology\" ";
        } if (cbSports.isChecked()) {
            result += "\"Sports\" ";
        } if (cbCulture.isChecked()) {
            result += "\"Culture\" ";
        } if (cbPolitics.isChecked()) {
            result += "\"Politics\" ";
        }
        return result.trim();
    }

    interface PassData {
        void passData(String categories);
        void noData();
    }



}
