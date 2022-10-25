package com.hisu.zola.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.hisu.zola.R;

public class EditTextUtil {
    public static void toggleShowClearIconOnEditText(Context context, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                            null, null,
                            ContextCompat.getDrawable(context, R.drawable.ic_close), null);
                else
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null
                    );
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void clearTextOnSearchEditText(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (editText.getCompoundDrawables()[2] == null) return false;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() -
                        editText.getCompoundDrawables()[2].getBounds().width())) {

                    editText.setText("");

                    return true;
                }
            }

            return false;
        });
    }
}