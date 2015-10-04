package com.foodroacher.app.android.ui.fragments;

import com.foodroacher.app.android.R;
import com.foodroacher.app.android.utils.Validator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SetMeUpFragment extends Fragment {

    private static final int SCHOOL_AS = 1;
    private static final int SCHOOL_GIESEL = 2;
    private static final int SCHOOL_THAYER = 3;
    private static final int SCHOOL_TUCK = 4;
    private static final int SCHOOL_NONE = 0;

    public interface OnClickSubmitListener {
        public void onClickSubmit(String email, String password, int collegeType);
    }

    private View mRootView = null;
    private EditText mEdtUserId = null;
    private EditText mEdtPassword = null;
    private Button mBtnSubmit = null;
    private CheckBox mChkDartmouth = null;
    private RadioGroup mRgSchool = null;
    private RadioButton mRbTuck = null;
    private RadioButton mRbThayer = null;
    private RadioButton mRbGiesel = null;
    private RadioButton mRbAandS = null;
    private OnClickSubmitListener mOnClickSubmitListener = null;
    private int mCurrentSchool = SCHOOL_NONE;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    public OnClickSubmitListener getOnClickSubmitListener() {
        return mOnClickSubmitListener;
    }

    public void setOnClickSubmitListener(OnClickSubmitListener onClickSubmitListener) {
        this.mOnClickSubmitListener = onClickSubmitListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_set_me_up, null);
        initViewsFromRoot(mRootView);
        return mRootView;
    }

    private void initViewsFromRoot(View mRootView) {
        mEdtUserId = (EditText) mRootView.findViewById(R.id.edtUserName);
        mEdtPassword = (EditText) mRootView.findViewById(R.id.edtPassword);
        mBtnSubmit = (Button) mRootView.findViewById(R.id.btnSubmit);
        mChkDartmouth = (CheckBox) mRootView.findViewById(R.id.chkAddYourCollege);
        mRbTuck = (RadioButton) mRootView.findViewById(R.id.rdTuck);
        mRbThayer = (RadioButton) mRootView.findViewById(R.id.rdThayer);
        mRbGiesel = (RadioButton) mRootView.findViewById(R.id.rdGeisel);
        mRbAandS = (RadioButton) mRootView.findViewById(R.id.rdAs);
        mRgSchool = (RadioGroup) mRootView.findViewById(R.id.rgSchoolPicker);
        mBtnSubmit.setOnClickListener(mOnClickListener);
        mRbTuck.setOnClickListener(mOnClickListener);
        mRbThayer.setOnClickListener(mOnClickListener);
        mRbGiesel.setOnClickListener(mOnClickListener);
        mRbAandS.setOnClickListener(mOnClickListener);
        mChkDartmouth.setOnClickListener(mOnClickListener);
        setInitialData();
    }

    private void setInitialData() {
        mRbAandS.setSelected(true);
        mChkDartmouth.setChecked(true);
        mCurrentSchool = SCHOOL_AS;
    }

    private boolean isValidData() {
        boolean result = false;
        result = checkUserName() && checkPassword();
        return result;
    }
    private boolean checkEditTextEmpty(EditText editText, int errorMessage){
        boolean result = false;
        String text = editText.getText().toString().trim();
        if (text != null && !text.isEmpty()) {
            result = true;
        } else {
            editText.setError(getString(errorMessage));
        }
        return result;
    }
    private boolean checkUserName() {
        boolean result = false;
        if(checkEditTextEmpty(mEdtUserId,R.string.user_id_is_required)){
            if(Validator.isvalidEmail(mEdtUserId.getText().toString())){
                result = true;
            }else{
                mEdtUserId.setError(getString(R.string.user_id_is_required));
            }
        }
        return result;
    }

    private boolean checkPassword() {
        boolean result = false;
        if(checkEditTextEmpty(mEdtPassword,R.string.password_is_required)){
            if(Validator.isValidPassword(mEdtPassword.getText().toString())){
                result = true;
            }else{
                mEdtPassword.setError(getString(R.string.password_min_8));
            }
        }
        return result;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSubmit: {
                    onClickSubmit();
                }
                case R.id.rdTuck: {
                    onClickSchool(SCHOOL_TUCK);
                }
                case R.id.rdGeisel: {
                    onClickSchool(SCHOOL_GIESEL);
                }
                case R.id.rdThayer: {
                    onClickSchool(SCHOOL_THAYER);
                }
                case R.id.rdAs: {
                    onClickSchool(SCHOOL_AS);
                }
                case R.id.chkAddYourCollege: {
                    onClickCollege();
                }
                default:
                    break;
            }

        }

    };

    private void onClickCollege() {
        mRgSchool.setVisibility(mChkDartmouth.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void onClickSchool(int school) {
        mCurrentSchool = school;
    }

    private void onClickSubmit() {
        if (isValidData()) {
            // do register user
            if (mOnClickSubmitListener != null) {
                mOnClickSubmitListener.onClickSubmit(mEdtUserId.getText().toString().trim(), mEdtPassword.getText().toString().trim(), getCurrentSchool());
            }
        }

    }

    private int getCurrentSchool() {
        return mChkDartmouth.isChecked() ? mCurrentSchool : SCHOOL_AS;
    }
}
