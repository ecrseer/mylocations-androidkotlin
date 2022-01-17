package infnet.android.smpa_permissao_serv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import infnet.android.smpa_permissao_serv.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class LocalHoraFragment : Fragment() {

    private var columnCount = 2
    private lateinit var viewModel: LocalHoraViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        viewModel = ViewModelProvider(this).get(LocalHoraViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_localhora_list, container, false)
        val caminhoArquivos=requireActivity().getExternalFilesDir(null)?.listFiles()
        caminhoArquivos?.let{
            viewModel.lerPasta(caminhoArquivos)
        }
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.localizacoesLista.observe(viewLifecycleOwner, Observer{localizacoes->
                    localizacoes?.let{
                        adapter = LocalHoraRecyclerViewAdapter(it)
                    }

                })
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            LocalHoraFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}