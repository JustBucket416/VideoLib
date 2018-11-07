package justbucket.videolib.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter

import justbucket.videolib.fragment.ImageFragment
import justbucket.videolib.model.VideoPres

// Note: Initialize with the child fragment manager.
class ImagePagerAdapter(fragment: Fragment, private val videoList: List<VideoPres>)
    : FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getCount() = videoList.size

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(videoList[position])
    }
}
