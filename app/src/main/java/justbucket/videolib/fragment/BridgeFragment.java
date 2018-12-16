package justbucket.videolib.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import justbucket.videolib.R;

public class BridgeFragment extends Fragment {

    private static final String TAG_KEY = "tag-key";

    public static final BridgeFragment newInstance(String tag) {
        BridgeFragment fragment = new BridgeFragment();
        Bundle args = new Bundle();
        args.putString(TAG_KEY, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_videos, container, false);

        if (getArguments() != null) {
            String string = getArguments().getString(TAG_KEY);

            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, GridFragment.newInstance(string))
                    .addToBackStack(null)
                    .commit();
        }
        return view;
    }

}
