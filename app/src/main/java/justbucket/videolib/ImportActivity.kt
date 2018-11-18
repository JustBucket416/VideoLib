package justbucket.videolib

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import dagger.android.AndroidInjection
import justbucket.videolib.adapter.TagListAdapter
import justbucket.videolib.di.ViewModelFactory
import justbucket.videolib.model.TagPres
import justbucket.videolib.state.Resource
import justbucket.videolib.viewmodel.ImportViewModel
import kotlinx.android.synthetic.main.activity_import.*
import javax.inject.Inject

class ImportActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var importViewModel: ImportViewModel
    private lateinit var adapter: TagListAdapter
    private val tagList = ArrayList<TagPres>()
    private val linkList = ArrayList<String>()

    companion object {
        private const val PATH_KEY = "path-key"
        private const val ACTION_INNER_IMPORT = "action-inner-import"

        fun newIntent(context: Context?, path: String): Intent {
            val intent = Intent(context, ImportActivity::class.java)
            intent.action = ACTION_INNER_IMPORT
            intent.putExtra(PATH_KEY, path)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)
        AndroidInjection.inject(this)

        importViewModel = ViewModelProviders.of(this, viewModelFactory)[ImportViewModel::class.java]
        importViewModel.getData().observe(this, Observer { resource ->
            resource?.let { showData(it) }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(applicationContext, getString(R.string.toast_permission_denied), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        when {
            intent.action == Intent.ACTION_SEND_MULTIPLE -> {

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1)
                }

                linkList.addAll((intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM) as ArrayList<Uri>).map { getFilePath(it) })
                textViewImport.text = String.format(getString(R.string.selected_num_videos, linkList.size))

            }
            intent.type?.equals("application/octet-stream") == true -> {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1)
                }
                linkList.add(getFilePath(intent.getParcelableExtra(Intent.EXTRA_STREAM)))
                textViewImport.text = (intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri).path
            }
            intent.type?.contains("video/") == true -> {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1)
                }

                linkList.add(getFilePath(intent.getParcelableExtra(Intent.EXTRA_STREAM)))
                textViewImport.text = (intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri).path
            }
            intent.type?.equals("text/plain") == true -> {
                linkList.add(intent.getStringExtra(Intent.EXTRA_TEXT))
                textViewImport.text = intent.getStringExtra(Intent.EXTRA_TEXT)
            }
            intent.action == ACTION_INNER_IMPORT -> {
                if (intent.getStringExtra(PATH_KEY).contains(Environment.getExternalStorageDirectory().absolutePath)) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                1)
                    }
                    linkList.add(getFilePath(Uri.parse(intent.getStringExtra(PATH_KEY))))
                } else linkList.add(intent.getStringExtra(PATH_KEY))
                textViewImport.text = intent.getStringExtra(PATH_KEY)
            }
        }

        buttonCancel.setOnClickListener { finish() }

        fullscreen_blank_view.setOnClickListener { finish() }

        buttonImport.setOnClickListener { _ ->
            importViewModel.addVideos(this, linkList, tagList)
            finish()
        }
    }

    fun showData(resource: Resource<List<TagPres>>) {
        resource.let {
            val items = it.data
            if (items?.isNotEmpty() == true) {
                adapter = TagListAdapter(items, object : TagListAdapter.TagHolderListener {
                    override fun onTagCheckChange(tag: TagPres, checked: Boolean) {
                        if (checked) tagList.add(tag) else tagList.remove(tag)
                    }
                })
                adapter.parseSelected(emptyList())
                recycler_import.adapter = adapter
                recycler_import.visibility = View.VISIBLE
                text_empty_tags.visibility = View.GONE
            } else {
                recycler_import.visibility = View.GONE
                text_empty_tags.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Parses filename string from the given [Uri]
     */
    private fun getFilePath(uri: Uri): String {
        if (uri.path.contains(Environment.getExternalStorageDirectory().absolutePath)) return uri.path.removePrefix("/documents")
        var result = ""
        val cursor = contentResolver.query(uri, null, null, null, null, null)
        if (cursor.moveToFirst()) result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA))
        cursor.close()
        return result
    }
}
