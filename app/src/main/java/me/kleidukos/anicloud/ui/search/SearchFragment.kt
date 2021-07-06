package me.kleidukos.anicloud.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.activities.MainActivity
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler
import me.kleidukos.anicloud.models.DisplayStreamContainer

class SearchFragment : Fragment() {

    private lateinit var searchBox: AutoCompleteTextView
    private lateinit var search: ImageView
    private lateinit var searchList: RecyclerView

    private lateinit var root: View

    private lateinit var streamContainer: DisplayStreamContainer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_search, container, false)

        searchBox = root.findViewById(R.id.searchbox)
        search = root.findViewById(R.id.search)
        searchList = root.findViewById(R.id.search_list)
        searchList.layoutManager = GridLayoutManager(requireContext(), 3)

        if (!MainActivity.isStreamContainerInit()) {
            Log.d("Fragment_Search", "Skip onCreateView")
            return root
        }

        if (!this::streamContainer.isInitialized) {
            streamContainer = MainActivity.streamContainer()
            loadSearchBox()
            setSearchOnClick()
        }

        return root
    }

    //Load Components
    private fun loadSearchBox() {
        val streamList = mutableListOf<String>()

        for (displayStream in streamContainer.allSeries) {
            streamList.add(displayStream.name)
        }

        searchBox.setAdapter(
            ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_item,
                streamList
            )
        )
    }

    private fun setSearchOnClick() {
        search.setOnClickListener {
            val matches = streamContainer.allSeries?.filter {
                it.name.contains(
                    searchBox.text.toString(),
                    true
                )
            }.take(100)

            searchList.removeAllViews()

            searchList.adapter = StreamAdapterRecycler(requireContext(), matches)
        }
    }
}