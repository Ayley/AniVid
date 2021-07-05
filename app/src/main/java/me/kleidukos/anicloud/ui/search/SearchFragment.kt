package me.kleidukos.anicloud.ui.search

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler
import me.kleidukos.anicloud.databinding.FragmentSearchBinding
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.DisplayStreamContainer

class SearchFragment : Fragment() {

    private lateinit var searchBox: AutoCompleteTextView
    private lateinit var search: ImageView
    private lateinit var searchList : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        searchBox = root.findViewById(R.id.searchbox)
        search = root.findViewById(R.id.search)
        searchList = root.findViewById(R.id.search_list)
        searchList.layoutManager = GridLayoutManager(requireContext(),3)

        val streamList = mutableListOf<String>()

        val displayStreamContainer = DataChannelManager.dataChannelStorage("Main") as DisplayStreamContainer

        for (displayStream in displayStreamContainer.allSeries!!){
            streamList.add(displayStream.name)
        }

        searchBox.setAdapter(ArrayAdapter(root.context, android.R.layout.simple_spinner_item, streamList))

        search.setOnClickListener {
            val matches = displayStreamContainer.allSeries?.filter { it.name.contains(searchBox.text.toString(), true) }?.take(100) as List<DisplayStream>

            searchList.removeAllViews()

            searchList.adapter = StreamAdapterRecycler(requireContext(), matches, 25)
        }

        return root
    }

    fun calculateColumns(context: Context, columnWidthDp: Float): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}