package me.kleidukos.anicloud.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler


class SearchFragment : Fragment() {

    private lateinit var searchBox: EditText
    private lateinit var searchList: RecyclerView

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_search, container, false)

        setupView()

        searchList = root.findViewById(R.id.search_list)
        searchList.layoutManager = GridLayoutManager(requireContext(), 3)

        setSearchOnClick()

        return root
    }

    override fun onResume() {
        setupView()
        super.onResume()
    }

    //Load Components
    private fun setSearchOnClick() {
        searchBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val matches = MainActivity.getAllAnime().filter {
                    it.title.contains(
                        searchBox.text.toString(),
                        true
                    )
                }.take(100)

                searchList.removeAllViews()

                searchList.adapter = StreamAdapterRecycler(root.context, matches)
            }

            override fun afterTextChanged(p0: Editable?) {
                //Nothing
            }

        })
    }

    private fun setupView(){
        activity?.findViewById<ImageButton>(R.id.menu_button)?.visibility = View.GONE

        activity?.findViewById<TextView>(R.id.app_title)?.visibility = View.GONE

        searchBox = activity?.findViewById(R.id.searchbox)!!

        searchBox.visibility = View.VISIBLE

        searchBox.requestFocus()

        val imm: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT)
    }
}