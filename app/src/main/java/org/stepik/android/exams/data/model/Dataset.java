package org.stepik.android.exams.data.model;


import java.util.List;

public final class Dataset {

    public Dataset(List<String> options, boolean is_multiple_choice) {
        this.options = options;
        this.is_multiple_choice = is_multiple_choice;
    }

    public Dataset(String string) {
        this.string = string;
    }

    private String string;
    private List<String> options;
    private boolean is_multiple_choice;

    public List<String> getOptions() {
        return options;
    }

    public boolean is_multiple_choice() {
        return is_multiple_choice;
    }
}
