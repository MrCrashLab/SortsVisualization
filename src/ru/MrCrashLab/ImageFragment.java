package ru.MrCrashLab;

public class ImageFragment {
    private int index;
    private int[] imagePartArray;

    public ImageFragment() {
    }

    public ImageFragment(int index) {
        this.index = index;
    }

    public ImageFragment(int index, int[] imagePartArray) {
        this.index = index;
        this.imagePartArray = imagePartArray;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getImagePartArray() {
        return imagePartArray;
    }

    public void setImagePartArray(int[] imagePartArray) {
        this.imagePartArray = imagePartArray;
    }
}
