package justbucket.videolib

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import justbucket.videolib.extension.inTransaction
import justbucket.videolib.fragment.GridFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val POSITION_KEY = "pos-key"
        private const val FRAGMENT_GRID_TAG = "grid"
        var currentPosition: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
                add(R.id.fragment_container, GridFragment(), FRAGMENT_GRID_TAG)
            }
        } else {
            currentPosition = savedInstanceState.getInt(POSITION_KEY, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(POSITION_KEY, currentPosition)
    }


}
