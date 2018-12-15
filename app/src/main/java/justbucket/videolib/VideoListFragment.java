package justbucket.videolib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VideoListFragment extends Fragment {
    private String mKeyTag;

    public static VideoListFragment newInstance(String tag) {
        VideoListFragment fragment = new VideoListFragment();
        fragment.setKeyTag(tag);
        return fragment;
    }

    public void setKeyTag(String mKeyTag) {
        this.mKeyTag = mKeyTag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }
}
