package org.stepik.android.exams.ui.listeners;

import android.widget.CompoundButton;

public interface CheckedChangeListenerWithPosition {
    void onCheckedChanged(CompoundButton view, boolean isChecked, int position);
}
