package justbucket.videolib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;

public class VideoListFragment extends Fragment {
    private String mKeyTag;

    public static VideoListFragment newInstance(String tag) {
        VideoListFragment fragment = new VideoListFragment();
        fragment.setKeyTag(tag);
        return fragment;
    }

    public void setKeyTag(String keyTag) {
        this.mKeyTag = keyTag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_for_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
